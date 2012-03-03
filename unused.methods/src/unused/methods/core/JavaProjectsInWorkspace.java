package unused.methods.core;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

class JavaProjectsInWorkspace {

	List<IJavaProject> collectAllJavaProjects() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		List<IJavaProject> javaProjects = new LinkedList<IJavaProject>();
		for (IProject project : projects) {
			IJavaProject javaProject = toJavaProject(project);
			if (javaProject != null) {
				javaProjects.add(javaProject);
			}
		}
		return javaProjects;
	}

	private IJavaProject toJavaProject(IProject project) {
		IJavaProject javaProject = null;
		try {
			if (project.isOpen() && project.hasNature(JavaCore.NATURE_ID)) {
				javaProject = JavaCore.create(project);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return javaProject;
	}
}
