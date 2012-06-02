package unused.methods.core;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class JavaAstParser {

	private final ASTVisitor visitor;

	public JavaAstParser(ASTVisitor visitor) {
		this.visitor = visitor;
	}

	public void sendVisitorTo(IJavaElement element) throws JavaModelException {
		if (element instanceof IJavaProject) {
			collectMethodsFromProject((IJavaProject) element);
		} else if (element instanceof IPackageFragment) {
			collectMethodsFromPackage((IPackageFragment) element);
		}
	}

	private void collectMethodsFromProject(IJavaProject javaProject) throws JavaModelException {
		for (IPackageFragment mypackage : javaProject.getPackageFragments()) {
			collectMethodsFromPackage(mypackage);
		}
	}

	private void collectMethodsFromPackage(IPackageFragment packageFragment)
			throws JavaModelException {
		if (packageFragment.getKind() == IPackageFragmentRoot.K_BINARY) {
			return;
		}

		collectMethodsFromSourcePackage(packageFragment);
	}

	private void collectMethodsFromSourcePackage(IPackageFragment packageFragment)
			throws JavaModelException {
		for (ICompilationUnit unit : packageFragment.getCompilationUnits()) {

			CompilationUnit compiled = parse(unit);
			compiled.accept(visitor);

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