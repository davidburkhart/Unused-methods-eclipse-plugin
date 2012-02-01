package unused.methods;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class CollectUnusedMethodsFrom {

	private final IJavaProject project;
	private final UnusedMethodsCollector collector;

	public CollectUnusedMethodsFrom(IJavaProject project, UnusedMethodsCollector collector) {
		this.project = project;
		this.collector = collector;
	}

	public void run() throws JavaModelException {
		for (IPackageFragment mypackage : project.getPackageFragments()) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				collectMethodsFromPackage(mypackage);
			}
			// else would be binary...
		}
	}

	private void collectMethodsFromPackage(IPackageFragment mypackage) throws JavaModelException {
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {

			CompilationUnit compiled = parse(unit);
			compiled.accept(collector);

		}
	}

	private CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(new NullProgressMonitor());
	}
}
