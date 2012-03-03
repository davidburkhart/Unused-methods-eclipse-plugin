package unused.methods.core;

import static org.eclipse.core.resources.IMarker.LINE_NUMBER;
import static org.eclipse.core.resources.IMarker.MESSAGE;
import static org.eclipse.core.resources.IMarker.SEVERITY;
import static org.eclipse.core.resources.IMarker.SEVERITY_WARNING;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class UnusedMethodsMarker {

	private static final String UNUSED_METHODS_MARKER = "unused.methods.marker";

	public static void on(IMethod method) {
		try {
			addMarker(method);
		} catch (CoreException ex) {
			ex.printStackTrace();
		}
	}

	public static void clear(IResource resource) {
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
			marker.setAttribute(LINE_NUMBER, findLine(method));
		}
	}

	private static int findLine(IMethod method) throws JavaModelException {
		String sourceOfCompilationUnit = method.getCompilationUnit().getSource();
		int offsetInCharacters = method.getSourceRange().getOffset();
		String linesBeforeMethod = sourceOfCompilationUnit.substring(0, offsetInCharacters);
		return countLines(linesBeforeMethod);
	}

	private static int countLines(String text) {
		int countLines = 1;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '\n') {
				countLines++;
			}
		}
		return countLines;
	}
}
