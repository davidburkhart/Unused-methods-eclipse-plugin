package unused.methods.core;

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

import unused.methods.UnusedMethodsPlugin;

public class FindUnusedMethodsInJavaProjectsJob extends Job {

	private final List<IJavaProject> javaProjects;
	private List<IMethod> unusedMethods;

	public FindUnusedMethodsInJavaProjectsJob(List<IJavaProject> javaProjects) {
		super("Find Unused Methods in " + projectNames(javaProjects));
		this.javaProjects = javaProjects;
	}

	public List<IMethod> getUnusedMethods() {
		return unusedMethods == null ? Collections.<IMethod> emptyList() : unusedMethods;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			FindUnusedMethodsInJavaProjects finder = new FindUnusedMethodsInJavaProjects(javaProjects, monitor);
			IStatus resultStatus = finder.run();
			unusedMethods = finder.getUnusedMethods();
			return resultStatus;
		} catch (JavaModelException e) {
			return errorStatus(e);
		}
	}

	private IStatus errorStatus(JavaModelException e) {
		String pluginId = UnusedMethodsPlugin.getDefault().getBundle().getSymbolicName();
		String projectNames = projectNames(javaProjects);
		return new Status(ERROR, pluginId, "Problem searching for unused methods in " + projectNames, e);
	}

	private static String projectNames(List<IJavaProject> projects) {
		StringBuffer javaProjectNames = new StringBuffer();
		for (IJavaProject project : projects) {
			javaProjectNames.append(project.getElementName()).append(",");
		}
		if (projects.size() > 1) {
			javaProjectNames.setLength(javaProjectNames.length() - 1);
		}
		return javaProjectNames.toString();
	}
}
