package unused.methods.core;

import static org.eclipse.core.runtime.Status.CANCEL_STATUS;
import static org.eclipse.core.runtime.Status.OK_STATUS;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

public class FindUnusedMethodsInJavaProjects {

	private final List<IJavaProject> javaProjects;
	private final IProgressMonitor monitor;
	private List<IMethod> unusedMethods;

	public FindUnusedMethodsInJavaProjects(List<IJavaProject> javaProjects, IProgressMonitor monitor) {
		this.javaProjects = javaProjects;
		this.monitor = monitor;
	}

	public IStatus run() throws JavaModelException {
		try {
			return runIntern();
		} catch (InterruptedException e) {
			return CANCEL_STATUS;
		}
	}

	private IStatus runIntern() throws JavaModelException, InterruptedException {
		List<IJavaProject> allJavaProjects = new JavaProjectsInWorkspace().collectAllJavaProjects();

		int totalWork = javaProjects.size() + allJavaProjects.size();
		monitor.beginTask("Searching for unused methods", totalWork);

		DeclaredMethods methods = collectDeclaredMethods();
		removedUsedMethods(allJavaProjects, methods);
		monitor.done();

		unusedMethods = methods.getMethods();
		return OK_STATUS;
	}

	private void removedUsedMethods(List<IJavaProject> allJavaProjects, DeclaredMethods methods)
			throws JavaModelException, InterruptedException {
		for (IJavaProject javaProject : allJavaProjects) {
			monitor.subTask("Removing methods used by " + javaProject.getElementName());
			new JavaAstParser(javaProject).accept(new RemoveUsedMethodsFrom(methods));
			monitor.worked(1);

			if (monitor.isCanceled()) {
				throw new InterruptedException();
			}
		}
	}

	private DeclaredMethods collectDeclaredMethods() throws JavaModelException, InterruptedException {
		DeclaredMethods methods = setupDeclaredMethods();
		for (IJavaProject javaProject : javaProjects) {
			monitor.subTask("Collecting declared methods from " + javaProject.getElementName());
			new JavaAstParser(javaProject).accept(new AddDeclaredMethodsTo(methods));
			monitor.worked(1);
			if (monitor.isCanceled()) {
				throw new InterruptedException();
			}
		}
		return methods;
	}

	public List<IMethod> getUnusedMethods() {
		return unusedMethods;
	}

	private DeclaredMethods setupDeclaredMethods() throws JavaModelException {
		DeclaredMethods methods = new DeclaredMethods();
		methods.addFilter(new DoNotAcceptAnnotation("org.junit.Test"));
		methods.addFilter(new DoNotAcceptMethodsOverridingBinary());
		return methods;
	}
}
