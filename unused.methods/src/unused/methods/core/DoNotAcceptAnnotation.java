package unused.methods.core;

import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class DoNotAcceptAnnotation implements MethodFilter {

	private final String annotationType;

	public DoNotAcceptAnnotation(String annotationType) {
		this.annotationType = annotationType;
	}

	@Override
	public boolean accept(MethodWithBinding method) {
		return !hasAnnotation(method);
	}

	private boolean hasAnnotation(MethodWithBinding method) {
		// TODO configurable Annotations (prefs) e.g. Spring stuff, ...
		// TODO autocompletion dialog when adding Annotation to prefs ;-)
		boolean hasAnnotation = false;
		IAnnotationBinding[] annotations = method.getBinding().getAnnotations();
		for (IAnnotationBinding annotation : annotations) {
			ITypeBinding annotationType = annotation.getAnnotationType();
			if (this.annotationType.equals(annotationType.getQualifiedName())) {
				hasAnnotation = true;
			}
		}
		return hasAnnotation;
	}

}
