package unused.methods;

import static org.eclipse.core.runtime.IStatus.ERROR;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;

public class FindUnusedMethodsInJavaProject extends Job {

	private final IJavaProject project;
	private List<IMethod> unusedMethods;

	public FindUnusedMethodsInJavaProject(IJavaProject project) {
		super("FindUnusedMethodsInJavaProject");
		this.project = project;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {

			findUnusedMethods();

		} catch (JavaModelException e) {
			String pluginId = UnusedMethodsPlugin.getDefault().getBundle().getSymbolicName();
			return new Status(ERROR, pluginId, "Problem searching for unused methods in " + project.getElementName(), e);
		}

		return Status.OK_STATUS;
	}

	void findUnusedMethods() throws JavaModelException {
		DeclaredMethods methods = new DeclaredMethods();
		methods.addFilter(new DoNotAcceptAnnotation("org.junit.Test"));
		methods.addFilter(new DoNotAcceptMethodsOverridingBinary());

		ASTVisitor visitor = new AddDeclaredMethodsTo(methods);
		new JavaAstParser(project).accept(visitor);

		new ScanWorkspaceForUsesOf(methods).removeUsedMethods();

		unusedMethods = methods.getMethods();
	}

	public List<IMethod> getUnusedMethods() {
		return unusedMethods == null ? Collections.<IMethod> emptyList() : unusedMethods;
	}
}
