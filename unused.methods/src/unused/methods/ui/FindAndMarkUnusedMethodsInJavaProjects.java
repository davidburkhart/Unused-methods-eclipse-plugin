package unused.methods.ui;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import unused.methods.core.FindUnusedMethodsInJavaProjects;
import unused.methods.core.UnusedMethodsMarker;

public class FindAndMarkUnusedMethodsInJavaProjects extends Action implements IObjectActionDelegate {

	private final List<IJavaProject> javaProjects = new LinkedList<IJavaProject>();

	@Override
	public void run(IAction action) {
		FindUnusedMethodsInJavaProjects findUnusedMethods = new FindUnusedMethodsInJavaProjects(javaProjects);
		findUnusedMethods.addJobChangeListener(markUnusedMethodsWhenDone(findUnusedMethods));
		findUnusedMethods.schedule();
	}

	private JobChangeAdapter markUnusedMethodsWhenDone(final FindUnusedMethodsInJavaProjects findUnusedMethods) {
		return new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				for (IJavaProject project : javaProjects) {
					UnusedMethodsMarker.clear(project.getResource());
				}
				List<IMethod> unusedMethods = findUnusedMethods.getUnusedMethods();
				for (IMethod method : unusedMethods) {
					UnusedMethodsMarker.on(method);
				}
			}
		};
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		javaProjects.clear();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection strusel = (IStructuredSelection) selection;
			for (Object element : strusel.toList()) {
				if (element instanceof IJavaProject) {
					javaProjects.add((IJavaProject) element);
				}
			}
		}
		action.setEnabled(!javaProjects.isEmpty());
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
}
