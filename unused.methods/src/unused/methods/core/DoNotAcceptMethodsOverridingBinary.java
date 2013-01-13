package unused.methods.core;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.internal.corext.dom.Bindings;

public class DoNotAcceptMethodsOverridingBinary implements MethodFilter {

	@Override
	public boolean accept(MethodWithBinding method, IMethodBinding binding) {
		IMethodBinding overriddenMethod = Bindings.findOverriddenMethod(binding, true);
		while (overriddenMethod != null) {
			if (isBinary(overriddenMethod)) {
				return false;
			}
			overriddenMethod = Bindings.findOverriddenMethod(overriddenMethod, true);
		}
		return true;
	}

	private boolean isBinary(IMethodBinding method) {
		boolean overriddenMethodIsBinary = false;
		ITypeBinding declaringClass = method.getDeclaringClass();
		IJavaElement javaElement = declaringClass.getJavaElement();
		if (javaElement instanceof IType) {
			IType type = (IType) javaElement;
			if (type.isBinary()) {
				overriddenMethodIsBinary = true;
			}
		}
		return overriddenMethodIsBinary;
	}
}
