package unused.methods;

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

public class FindAndMarkUnusedMethodsInJavaProject extends Action implements IObjectActionDelegate {

	private IJavaProject project;

	@Override
	public void run(IAction action) {
		FindUnusedMethodsInJavaProject findUnusedMethods = new FindUnusedMethodsInJavaProject(project);
		findUnusedMethods.addJobChangeListener(markUnusedMethodsWhenDone(findUnusedMethods));
		findUnusedMethods.schedule();
	}

	private JobChangeAdapter markUnusedMethodsWhenDone(final FindUnusedMethodsInJavaProject findUnusedMethods) {
		return new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				List<IMethod> unusedMethods = findUnusedMethods.getUnusedMethods();
				for (IMethod method : unusedMethods) {
					UnusedMethodsMarker.on(method);
				}
			}
		};
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection strusel = (IStructuredSelection) selection;
			Object firstElement = strusel.getFirstElement();
			if (firstElement instanceof IJavaProject) {
				project = (IJavaProject) firstElement;
			}
		}
		action.setEnabled(project != null);
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
}
