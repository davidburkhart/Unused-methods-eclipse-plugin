package unused.methods.core;

import static org.eclipse.core.runtime.Status.CANCEL_STATUS;
import static org.eclipse.core.runtime.Status.OK_STATUS;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

public class FindUnusedMethods {

	private final List<IJavaElement> elements;
	private final IProgressMonitor monitor;
	private List<IMethod> unusedMethods;

	public FindUnusedMethods(List<IJavaElement> elements, IProgressMonitor monitor) {
		this.elements = elements;
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

		int totalWork = elements.size() + allJavaProjects.size();
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
		for (IJavaElement element : elements) {
			monitor.subTask("Collecting declared methods from " + element.getElementName());
			new JavaAstParser(element).accept(new AddDeclaredMethodsTo(methods));
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

	private DeclaredMethods setupDeclaredMethods() {
		DeclaredMethods methods = new DeclaredMethods();
		methods.addFilter(new DoNotAcceptAnnotation("org.junit.Test"));
		methods.addFilter(new DoNotAcceptMethodsOverridingBinary());
		return methods;
	}
}
