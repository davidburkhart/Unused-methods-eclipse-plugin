package unused.methods.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import unused.methods.core.AddMarkerToMethods;
import unused.methods.core.FindUnusedMethodsJob;
import unused.methods.core.JavaAstParser;
import unused.methods.core.MethodWithBinding;
import unused.methods.core.UnusedMethodsMarker;

public class FindAndMarkUnusedMethodsInJavaProjects implements IObjectActionDelegate {

	private final List<IJavaElement> elements = new LinkedList<IJavaElement>();

	@Override
	public void run(IAction action) {
		FindUnusedMethodsJob findUnusedMethods = new FindUnusedMethodsJob(elements);
		findUnusedMethods.addJobChangeListener(markUnusedMethodsWhenDone(findUnusedMethods));
		findUnusedMethods.schedule();
	}

	private JobChangeAdapter markUnusedMethodsWhenDone(final FindUnusedMethodsJob findUnusedMethods) {
		return new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				for (IJavaElement element : elements) {
					UnusedMethodsMarker.clear(element.getResource());
				}

				new Job("Marking unused methods") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						for (IJavaElement element : elements) {
							Set<MethodWithBinding> unusedMethods = findUnusedMethods.getUnusedMethods();
							AddMarkerToMethods visitor = new AddMarkerToMethods(unusedMethods);
							try {
								new JavaAstParser(visitor).sendVisitorTo(element);
							} catch (JavaModelException e) {
								e.printStackTrace();
							}
						}
						return Status.OK_STATUS;
					}
				}.schedule();
			}
		};
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		elements.clear();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection strusel = (IStructuredSelection) selection;
			for (Object element : strusel.toList()) {
				if (element instanceof IJavaProject) {
					elements.add((IJavaProject) element);
				} else if (element instanceof IPackageFragment) {
					elements.add((IPackageFragment) element);
				}
			}
		}
		action.setEnabled(!elements.isEmpty());
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
}
