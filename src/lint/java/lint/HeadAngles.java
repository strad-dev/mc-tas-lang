package lint;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("*")
public class HeadAngles extends AbstractProcessor {
	private static final Set<String> TURNS = Set.of("turnHead", "rightClick");
	private static final Set<String> OTHER_TURNS = Set.of("snapHeadToNearestEnemy", "snapHeadAtEntity", "snapHeadAtNearestNamed",
			"aimTerminatorAtNearestEnemy", "aimTerminatorAtEntity", "leap", "runCommand");
	private static final int UNKNOWN_TICK = -1;

	private Trees trees;

	@Override
	public synchronized void init(ProcessingEnvironment env) {
		super.init(env);
		trees = Trees.instance(env);
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment round) {
		for(Element root : round.getRootElements()) {
			TreePath path = trees.getPath(root);
			if(path != null) new Scanner(path.getCompilationUnit()).scan(path, null);
		}
		return false;
	}

	private record Turn(int tick, long end, Float yaw, Float pitch, MethodInvocationTree call) {}

	private final class Scanner extends TreePathScanner<Void, Void> {
		private final CompilationUnitTree unit;
		private final Deque<List<Turn>> scopes = new ArrayDeque<>();

		Scanner(CompilationUnitTree unit) {
			this.unit = unit;
		}

		@Override
		public Void visitMethod(MethodTree method, Void v) {
			scopes.push(new ArrayList<>());
			try {
				return super.visitMethod(method, v);
			} finally {
				checkRepeats(scopes.pop());
			}
		}

		@Override
		public Void visitLambdaExpression(LambdaExpressionTree lambda, Void v) {
			scopes.push(new ArrayList<>());
			try {
				return super.visitLambdaExpression(lambda, v);
			} finally {
				checkRepeats(scopes.pop());
			}
		}

		@Override
		public Void visitMethodInvocation(MethodInvocationTree call, Void v) {
			String name = methodName(call);
			boolean turn = TURNS.contains(name) && call.getArguments().size() == 2;
			if(turn) {
				requireFloat(name, "yaw", call.getArguments().get(0));
				requireFloat(name, "pitch", call.getArguments().get(1));
			} else if(name.equals("startAt") && call.getArguments().size() == 5) {
				requireFloat(name, "yaw", call.getArguments().get(3));
				requireFloat(name, "pitch", call.getArguments().get(4));
			}
			if(turn || OTHER_TURNS.contains(name)) {
				Float yaw = null, pitch = null;
				if(turn) {
					yaw = constantFloat(call.getArguments().get(0));
					pitch = constantFloat(call.getArguments().get(1));
					if(yaw != null && (yaw <= -180 || yaw > 180)) warn(call.getArguments().get(0), name + " yaw " + yaw + " is outside (-180, 180]");
					if(pitch != null && (pitch < -90 || pitch > 90)) warn(call.getArguments().get(1), name + " pitch " + pitch + " is outside [-90, 90]");
				}
				Integer tick = tick(call);
				if(tick != null && !scopes.isEmpty()) {
					scopes.peek().add(new Turn(tick, trees.getSourcePositions().getEndPosition(unit, call), yaw, pitch, call));
				}
			}
			return super.visitMethodInvocation(call, v);
		}

		private void requireFloat(String name, String axis, ExpressionTree arg) {
			new TreeScanner<Void, Void>() {
				@Override
				public Void visitLiteral(LiteralTree literal, Void v) {
					if(literal.getValue() instanceof Number && literal.getKind() != Tree.Kind.FLOAT_LITERAL) {
						warn(literal, name + " " + axis + " " + literal + " needs an f suffix");
					}
					return null;
				}

				@Override
				public Void visitLambdaExpression(LambdaExpressionTree lambda, Void v) {
					return null;
				}

				@Override
				public Void visitMethodInvocation(MethodInvocationTree call, Void v) {
					return null;
				}
			}.scan(arg, null);
		}

		private Float constantFloat(ExpressionTree arg) {
			Double value = constant(arg);
			return value == null ? null : value.floatValue();
		}

		private void warn(Tree tree, String message) {
			trees.printMessage(Diagnostic.Kind.WARNING, message, tree, unit);
		}

		private void checkRepeats(List<Turn> turns) {
			if(turns.stream().anyMatch(t -> t.tick() == UNKNOWN_TICK)) return;
			turns.sort(Comparator.comparingInt(Turn::tick).thenComparingLong(Turn::end));
			Turn last = null;
			for(Turn t : turns) {
				if(t.yaw() == null || t.pitch() == null) {
					last = null;
					continue;
				}
				if(last != null && sameYaw(last.yaw(), t.yaw()) && last.pitch().equals(t.pitch())) {
					trees.printMessage(Diagnostic.Kind.WARNING, methodName(t.call())
							+ " turns to the angle already set on line " + line(last.call()), t.call(), unit);
				}
				last = t;
			}
		}

		private boolean sameYaw(float a, float b) {
			return Math.floorMod(Math.round((a - b) * 1000), 360000) == 0;
		}

		private Integer tick(MethodInvocationTree call) {
			ExpressionTree select = call.getMethodSelect();
			while(true) {
				if(select instanceof IdentifierTree) return 0;
				if(!(select instanceof MemberSelectTree member)) return null;
				ExpressionTree receiver = member.getExpression();
				if(receiver instanceof IdentifierTree self && self.getName().contentEquals("this")) return 0;
				if(!(receiver instanceof MethodInvocationTree inner)) return null;
				if(isOwnAt(inner)) {
					return inner.getArguments().size() == 1 && inner.getArguments().getFirst() instanceof LiteralTree literal
							&& literal.getValue() instanceof Integer n ? Math.max(1, n) : UNKNOWN_TICK;
				}
				select = inner.getMethodSelect();
			}
		}

		private boolean isOwnAt(MethodInvocationTree call) {
			Tree select = call.getMethodSelect();
			return select instanceof IdentifierTree id && id.getName().contentEquals("at")
					|| select instanceof MemberSelectTree member && member.getIdentifier().contentEquals("at")
					&& member.getExpression() instanceof IdentifierTree self && self.getName().contentEquals("this");
		}

		private String methodName(MethodInvocationTree call) {
			Tree select = call.getMethodSelect();
			if(select instanceof IdentifierTree id) return id.getName().toString();
			if(select instanceof MemberSelectTree member) return member.getIdentifier().toString();
			return "";
		}

		private Double constant(ExpressionTree tree) {
			switch(tree) {
				case LiteralTree literal when literal.getValue() instanceof Number n -> {
					return n.doubleValue();
				}
				case ParenthesizedTree paren -> {
					return constant(paren.getExpression());
				}
				case TypeCastTree cast -> {
					return constant(cast.getExpression());
				}
				case UnaryTree unary when unary.getKind() == Tree.Kind.UNARY_MINUS || unary.getKind() == Tree.Kind.UNARY_PLUS -> {
					Double value = constant(unary.getExpression());
					return value == null ? null : unary.getKind() == Tree.Kind.UNARY_MINUS ? -value : value;
				}
				case BinaryTree binary -> {
					Double a = constant(binary.getLeftOperand()), b = constant(binary.getRightOperand());
					if(a == null || b == null) return null;
					return switch(binary.getKind()) {
						case PLUS -> a + b;
						case MINUS -> a - b;
						case MULTIPLY -> a * b;
						case DIVIDE -> a / b;
						default -> null;
					};
				}
				case IdentifierTree _, MemberSelectTree _ -> {
					return variableConstant(tree);
				}
				default -> {
					return null;
				}
			}
		}

		private Double variableConstant(ExpressionTree tree) {
			TreePath path = TreePath.getPath(unit, tree);
			Element element = path == null ? null : trees.getElement(path);
			return element instanceof VariableElement variable && variable.getConstantValue() instanceof Number n ? n.doubleValue() : null;
		}

		private long line(Tree tree) {
			return unit.getLineMap().getLineNumber(trees.getSourcePositions().getStartPosition(unit, tree));
		}
	}
}
