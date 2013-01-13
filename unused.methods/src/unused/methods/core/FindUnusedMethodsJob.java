package unused.methods.core;

import static org.eclipse.core.runtime.IStatus.ERROR;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;

import unused.methods.UnusedMethodsPlugin;

public class FindUnusedMethodsJob extends Job {

	private final List<IJavaElement> elements;
	private Set<MethodWithBinding> unusedMethods = Collections.emptySet();

	public FindUnusedMethodsJob(List<IJavaElement> elements) {
		super("Find Unused Methods in " + names(elements));
		this.elements = elements;
	}

	public Set<MethodWithBinding> getUnusedMethods() {
		return unusedMethods;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			unusedMethods = new FindUnusedMethods(elements, monitor).run();
			return Status.OK_STATUS;
		} catch (JavaModelException e) {
			return errorStatus(e);
		}
	}

	private IStatus errorStatus(JavaModelException e) {
		String pluginId = UnusedMethodsPlugin.getDefault().getBundle().getSymbolicName();
		String names = names(elements);
		return new Status(ERROR, pluginId, "Problem searching for unused methods in " + names, e);
	}

	private static String names(List<IJavaElement> elements) {
		StringBuffer elementNames = new StringBuffer();
		for (IJavaElement element : elements) {
			elementNames.append(element.getElementName()).append(",");
		}
		if (elements.size() > 1) {
			elementNames.setLength(elementNames.length() - 1);
		}
		return elementNames.toString();
	}
}
