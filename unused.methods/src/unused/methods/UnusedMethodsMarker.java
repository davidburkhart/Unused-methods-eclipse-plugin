package unused.methods;

import static org.eclipse.core.resources.IMarker.MESSAGE;
import static org.eclipse.core.resources.IMarker.SEVERITY;
import static org.eclipse.core.resources.IMarker.SEVERITY_WARNING;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;

public class UnusedMethodsMarker {

	private static final String UNUSED_METHODS_MARKER = "unused.methods.marker";

	public static void on(IMethod method) {
		try {
			addMarker(method);
		} catch (CoreException ex) {
			ex.printStackTrace();
		}
	}

	public static void clear(IWorkspaceRoot resource) {
		try {
			resource.deleteMarkers(UNUSED_METHODS_MARKER, false, DEPTH_INFINITE);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private static void addMarker(IMethod method) throws CoreException {
		IMarker marker = method.getResource().createMarker(UNUSED_METHODS_MARKER);
		if (marker.exists()) {
			JavaCore.getJavaCore().configureJavaElementMarker(marker, method);
			marker.setAttribute(MESSAGE, "Method " + method.getElementName() + " is not used.");
			marker.setAttribute(SEVERITY, SEVERITY_WARNING);
		}
	}
}
