package unused.methods.core;

import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eclipse.core.runtime.Status.CANCEL_STATUS;
import static org.eclipse.core.runtime.Status.OK_STATUS;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import unused.methods.UnusedMethodsPlugin;

public class FindUnusedMethodsInJavaProjects extends Job {

	private final List<IJavaProject> javaProjects;
	private List<IMethod> unusedMethods;

	public FindUnusedMethodsInJavaProjects(List<IJavaProject> javaProjects) {
		super("FindUnusedMethodsInJavaProject");
		this.javaProjects = javaProjects;
	}

	public List<IMethod> getUnusedMethods() {
		return unusedMethods == null ? Collections.<IMethod> emptyList() : unusedMethods;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			return findUnusedMethods(monitor);
		} catch (JavaModelException e) {
			return errorStatus(e);
		}
	}

	private DeclaredMethods setupDeclaredMethods() throws JavaModelException {
		DeclaredMethods methods = new DeclaredMethods();
		methods.addFilter(new DoNotAcceptAnnotation("org.junit.Test"));
		methods.addFilter(new DoNotAcceptMethodsOverridingBinary());
		return methods;
	}

	private IStatus findUnusedMethods(IProgressMonitor monitor) throws JavaModelException {
		List<IJavaProject> allJavaProjects = new JavaProjectsInWorkspace().collectAllJavaProjects();

		int totalWork = javaProjects.size() + allJavaProjects.size();
		monitor.beginTask("Searching for unused methods in " + allProjectNames(), totalWork);

		DeclaredMethods methods = setupDeclaredMethods();
		for (IJavaProject project : javaProjects) {
			monitor.subTask("Collecting declared methods from " + project.getElementName());
			new JavaAstParser(project).accept(new AddDeclaredMethodsTo(methods));
			monitor.worked(1);
			if (monitor.isCanceled()) {
				return CANCEL_STATUS;
			}
		}

		for (IJavaProject javaProject : allJavaProjects) {
			monitor.subTask("Removing methods used by " + javaProject.getElementName());
			new JavaAstParser(javaProject).accept(new RemoveUsedMethodsFrom(methods));
			monitor.worked(1);

			if (monitor.isCanceled()) {
				return CANCEL_STATUS;
			}
		}

		monitor.done();

		unusedMethods = methods.getMethods();
		return OK_STATUS;
	}

	private IStatus errorStatus(JavaModelException e) {
		String pluginId = UnusedMethodsPlugin.getDefault().getBundle().getSymbolicName();
		return new Status(ERROR, pluginId, "Problem searching for unused methods in " + allProjectNames(), e);
	}

	private String allProjectNames() {
		StringBuffer javaProjectNames = new StringBuffer();
		for (IJavaProject project : javaProjects) {
			javaProjectNames.append(project.getElementName()).append(",");
		}
		if (javaProjects.size() > 1) {
			javaProjectNames.setLength(javaProjectNames.length() - 1);
		}
		return javaProjectNames.toString();
	}
}
