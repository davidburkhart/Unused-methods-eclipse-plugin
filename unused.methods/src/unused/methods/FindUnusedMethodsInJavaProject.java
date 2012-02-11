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

			return findUnusedMethods(monitor);

		} catch (JavaModelException e) {
			String pluginId = UnusedMethodsPlugin.getDefault().getBundle().getSymbolicName();
			return new Status(ERROR, pluginId, "Problem searching for unused methods in " + project.getElementName(), e);
		}
	}

	private IStatus findUnusedMethods(IProgressMonitor monitor) throws JavaModelException {
		List<IJavaProject> allJavaProjects = new JavaProjectsInWorkspace().collectAllJavaProjects();

		int totalWork = 1 + allJavaProjects.size();
		monitor.beginTask("Searching for unused methods in " + project.getElementName(), totalWork);

		monitor.subTask("Collecting declared methods from " + project.getElementName());
		DeclaredMethods methods = setupDeclaredMethods();
		new JavaAstParser(project).accept(new AddDeclaredMethodsTo(methods));
		monitor.worked(1);

		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}

		for (IJavaProject javaProject : allJavaProjects) {
			monitor.subTask("Removing methods used by " + javaProject.getElementName());
			new JavaAstParser(javaProject).accept(new RemoveUsedMethodsFrom(methods));
			monitor.worked(1);

			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
		}

		monitor.done();

		unusedMethods = methods.getMethods();
		return Status.OK_STATUS;
	}

	private DeclaredMethods setupDeclaredMethods() throws JavaModelException {
		DeclaredMethods methods = new DeclaredMethods();
		methods.addFilter(new DoNotAcceptAnnotation("org.junit.Test"));
		methods.addFilter(new DoNotAcceptMethodsOverridingBinary());
		return methods;
	}

	public List<IMethod> getUnusedMethods() {
		return unusedMethods == null ? Collections.<IMethod> emptyList() : unusedMethods;
	}
}
