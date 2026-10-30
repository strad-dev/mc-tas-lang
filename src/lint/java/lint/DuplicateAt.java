package lint;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.SwitchExpressionTree;
import com.sun.source.tree.SwitchTree;
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
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("*")
public class DuplicateAt extends AbstractProcessor {
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

	private final class Scanner extends TreePathScanner<Void, Void> {
		private final CompilationUnitTree unit;
		private final Deque<Map<Integer, Tree>> scopes = new ArrayDeque<>();

		Scanner(CompilationUnitTree unit) {
			this.unit = unit;
		}

		@Override
		public Void visitMethod(MethodTree method, Void v) {
			scopes.push(new HashMap<>());
			try {
				return super.visitMethod(method, v);
			} finally {
				scopes.pop();
			}
		}

		@Override
		public Void visitLambdaExpression(LambdaExpressionTree lambda, Void v) {
			scopes.push(new HashMap<>());
			try {
				return super.visitLambdaExpression(lambda, v);
			} finally {
				scopes.pop();
			}
		}

		// Cases and if-branches never both run: each starts from what came before and merges back after.
		@Override
		public Void visitSwitch(SwitchTree tree, Void v) {
			scan(tree.getExpression(), v);
			branches(tree.getCases(), v);
			return null;
		}

		@Override
		public Void visitSwitchExpression(SwitchExpressionTree tree, Void v) {
			scan(tree.getExpression(), v);
			branches(tree.getCases(), v);
			return null;
		}

		@Override
		public Void visitIf(IfTree tree, Void v) {
			scan(tree.getCondition(), v);
			branches(tree.getElseStatement() == null ? List.of(tree.getThenStatement())
					: List.of(tree.getThenStatement(), tree.getElseStatement()), v);
			return null;
		}

		private void branches(List<? extends Tree> branches, Void v) {
			if(scopes.isEmpty()) {
				for(Tree branch : branches) scan(branch, v);
				return;
			}
			Map<Integer, Tree> before = scopes.peek();
			Map<Integer, Tree> after = new HashMap<>(before);
			for(Tree branch : branches) {
				scopes.push(new HashMap<>(before));
				try {
					scan(branch, v);
				} finally {
					scopes.pop().forEach(after::putIfAbsent);
				}
			}
			before.putAll(after);
		}

		@Override
		public Void visitMethodInvocation(MethodInvocationTree call, Void v) {
			Integer tick = atTick(call);
			if(tick != null && !scopes.isEmpty()) {
				Tree first = scopes.peek().putIfAbsent(Math.max(1, tick), call);
				if(first != null) {
					trees.printMessage(Diagnostic.Kind.WARNING, "at(" + tick + ") lands on the same tick as line " + line(first), call, unit);
				}
			}
			return super.visitMethodInvocation(call, v);
		}

		private Integer atTick(MethodInvocationTree call) {
			Tree select = call.getMethodSelect();
			boolean own = select instanceof IdentifierTree id && id.getName().contentEquals("at")
					|| select instanceof MemberSelectTree member && member.getIdentifier().contentEquals("at")
					&& member.getExpression() instanceof IdentifierTree self && self.getName().contentEquals("this");
			if(!own || call.getArguments().size() != 1) return null;
			return call.getArguments().getFirst() instanceof LiteralTree literal && literal.getValue() instanceof Integer n ? n : null;
		}

		private long line(Tree tree) {
			return unit.getLineMap().getLineNumber(trees.getSourcePositions().getStartPosition(unit, tree));
		}
	}
}
