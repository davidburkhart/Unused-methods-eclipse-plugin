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

	private final IJavaElement element;

	public JavaAstParser(IJavaElement project) {
		this.element = project;
	}

	public void accept(ASTVisitor visitor) throws JavaModelException {
		if (element instanceof IJavaProject) {
			collectMethodsFromProject((IJavaProject) element, visitor);
		} else if (element instanceof IPackageFragment) {
			collectMethodsFromPackage((IPackageFragment) element, visitor);
		}
	}

	private void collectMethodsFromProject(IJavaProject javaProject, ASTVisitor visitor) throws JavaModelException {
		for (IPackageFragment mypackage : javaProject.getPackageFragments()) {
			collectMethodsFromPackage(mypackage, visitor);
		}
	}

	private void collectMethodsFromPackage(IPackageFragment packageFragment, ASTVisitor visitor)
			throws JavaModelException {
		if (packageFragment.getKind() == IPackageFragmentRoot.K_BINARY) {
			return;
		}

		collectMethodsFromSourcePackage(packageFragment, visitor);
	}

	private void collectMethodsFromSourcePackage(IPackageFragment packageFragment, ASTVisitor visitor)
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