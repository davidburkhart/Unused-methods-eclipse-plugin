package unused.methods.core;

import java.util.List;

import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class DoNotAcceptAnnotation implements MethodFilter {

	private final String annotationType;

	public DoNotAcceptAnnotation(String annotationType) {
		this.annotationType = annotationType;
	}

	@Override
	public boolean accept(IMethodBinding binding, List<IMethodBinding> overriddenMethods) {
		return !hasAnnotation(binding);
	}

	private boolean hasAnnotation(IMethodBinding binding) {
		// TODO use preferences configured in UnusedMethodsPreferencePage
		boolean hasAnnotation = false;
		IAnnotationBinding[] annotations = binding.getAnnotations();
		for (IAnnotationBinding annotation : annotations) {
			ITypeBinding annotationType = annotation.getAnnotationType();
			if (this.annotationType.equals(annotationType.getQualifiedName())) {
				hasAnnotation = true;
			}
		}
		return hasAnnotation;
	}

}
