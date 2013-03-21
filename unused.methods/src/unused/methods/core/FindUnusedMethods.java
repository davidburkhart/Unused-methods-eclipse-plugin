package unused.methods.core;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

public class FindUnusedMethods {

	private final List<IJavaElement> elements;
	private final IProgressMonitor monitor;

	public FindUnusedMethods(List<IJavaElement> elements, IProgressMonitor monitor) {
		this.elements = elements;
		this.monitor = monitor;
	}

	public Set<MethodWithBinding> run() throws JavaModelException {
		List<IJavaProject> allJavaProjects = new JavaProjectsInWorkspace().collectAllJavaProjects();

		int totalWork = elements.size() + allJavaProjects.size();
		monitor.beginTask("Searching for unused methods", totalWork);

		DeclaredMethods methods = collectDeclaredMethods();
		removedUsedMethods(allJavaProjects, methods);
		monitor.done();

		return methods.getMethods();
	}

	private DeclaredMethods collectDeclaredMethods() throws JavaModelException {
		DeclaredMethods methods = setupDeclaredMethods();
		for (IJavaElement element : elements) {
			monitor.subTask("Collecting declared methods from " + element.getElementName());
			new JavaAstParser(new AddDeclaredMethodsTo(methods)).sendVisitorTo(element);
			monitor.worked(1);
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
		return methods;
	}

	private void removedUsedMethods(List<IJavaProject> allJavaProjects, DeclaredMethods methods)
			throws JavaModelException {
		for (IJavaProject javaProject : allJavaProjects) {
			monitor.subTask("Removing methods used by " + javaProject.getElementName());
			new JavaAstParser(new RemoveUsedMethodsFrom(methods)).sendVisitorTo(javaProject);
			monitor.worked(1);

			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
	}

	private DeclaredMethods setupDeclaredMethods() {
		DeclaredMethods methods = new DeclaredMethods();

		UnusedMethodsPreferences preferences = new UnusedMethodsPreferences();
		for (UnusedMethodAnnotationPreference preference : preferences.getPreferences()) {
			methods.addFilter(new DoNotAcceptAnnotation(preference.getFullyQualifiedName()));
		}

		methods.addFilter(new DoNotAcceptMethodsOverridingBinary());
		return methods;
	}
}
