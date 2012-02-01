package unused.methods;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;

public class FindUnusedMethodsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<IJavaProject> javaProjects = collectAllJavaProjects();

		FindUnusedMethods job = new FindUnusedMethods(javaProjects);
		job.addJobChangeListener(addMarkersWhenDone(job));
		job.schedule();

		return null;
	}

	private List<IJavaProject> collectAllJavaProjects() {
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

	private JobChangeAdapter addMarkersWhenDone(final FindUnusedMethods job) {
		return new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent e) {
				for (IMethod method : job.getUnusedMethods()) {
					UnusedMethodsMarker.on(method);
				}
			}
		};
	}
}
