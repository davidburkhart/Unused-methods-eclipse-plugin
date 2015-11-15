package unused.methods.ui;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import unused.methods.core.UnusedMethodsMarker;

public class ClearUnusedMethodMarkers implements IObjectActionDelegate {

	private final List<IJavaElement> elements = new LinkedList<IJavaElement>();

	@Override
	public void run(IAction action) {
		for (IJavaElement javaElement : elements) {
			UnusedMethodsMarker.clear(javaElement.getResource());
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		elements.clear();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection strusel = (IStructuredSelection) selection;
			for (Object element : strusel.toList()) {
				if (element instanceof IJavaElement) {
					elements.add((IJavaElement) element);
				}
			}
		}
		action.setEnabled(!elements.isEmpty());
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
}
