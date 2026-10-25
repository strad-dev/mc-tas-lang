package lint;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("*")
public class GuiStillOpen extends AbstractProcessor {
	private static final Set<String> BLOCKED = Set.of("move", "setHotbarSlot", "turnHead", "snapHeadToNearestEnemy", "snapHeadAtEntity",
			"snapHeadAtNearestNamed", "aimTerminatorAtNearestEnemy", "aimTerminatorAtEntity", "loopLeftClick", "loopRightClick", "leftClick",
			"rightClick", "stopRightClick", "dropItem", "leap", "runCommand", "useSpeedLoadout", "useDpsLoadout");

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

	private record Input(long end, String name, MethodInvocationTree call) {}

	private final class Scanner extends TreePathScanner<Void, Void> {
		private final CompilationUnitTree unit;
		private final Deque<Map<Object, List<Input>>> scopes = new ArrayDeque<>();

		Scanner(CompilationUnitTree unit) {
			this.unit = unit;
		}

		@Override
		public Void visitMethod(MethodTree method, Void v) {
			scopes.push(new LinkedHashMap<>());
			try {
				return super.visitMethod(method, v);
			} finally {
				check(scopes.pop());
			}
		}

		@Override
		public Void visitLambdaExpression(LambdaExpressionTree lambda, Void v) {
			scopes.push(new LinkedHashMap<>());
			try {
				return super.visitLambdaExpression(lambda, v);
			} finally {
				check(scopes.pop());
			}
		}

		@Override
		public Void visitMethodInvocation(MethodInvocationTree call, Void v) {
			String name = methodName(call);
			if(!scopes.isEmpty() && (BLOCKED.contains(name) || name.equals("clickGui") || name.equals("closeGui"))) {
				Object tick = tick(call);
				if(tick != null) scopes.peek().computeIfAbsent(tick, k -> new ArrayList<>())
						.add(new Input(trees.getSourcePositions().getEndPosition(unit, call), name, call));
			}
			return super.visitMethodInvocation(call, v);
		}

		private void check(Map<Object, List<Input>> ticks) {
			for(List<Input> inputs : ticks.values()) {
				inputs.sort(Comparator.comparingLong(Input::end));
				MethodInvocationTree click = null;
				for(Input input : inputs) {
					if(input.name().equals("clickGui")) click = input.call();
					else if(input.name().equals("closeGui")) click = null;
					else if(click != null) {
						trees.printMessage(Diagnostic.Kind.ERROR, input.name() + " on the tick of the clickGui on line " + line(click)
								+ ": the GUI is still open until next tick (closeGui first)", input.call(), unit);
					}
				}
			}
		}

		private Object tick(MethodInvocationTree call) {
			ExpressionTree select = call.getMethodSelect();
			while(true) {
				if(select instanceof IdentifierTree) return 0;
				if(!(select instanceof MemberSelectTree member)) return null;
				ExpressionTree receiver = member.getExpression();
				if(receiver instanceof IdentifierTree self && self.getName().contentEquals("this")) return 0;
				if(!(receiver instanceof MethodInvocationTree inner)) return null;
				if(isOwnAt(inner)) {
					return inner.getArguments().size() == 1 && inner.getArguments().getFirst() instanceof LiteralTree literal
							&& literal.getValue() instanceof Integer n ? (Object) Math.max(1, n) : inner;
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

		private long line(Tree tree) {
			return unit.getLineMap().getLineNumber(trees.getSourcePositions().getStartPosition(unit, tree));
		}
	}
}
