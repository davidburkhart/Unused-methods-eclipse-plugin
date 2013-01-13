package unused.methods.core;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class DoNotAcceptMethodsOverridingBinary implements MethodFilter {

	@Override
	public boolean accept(IMethodBinding binding, List<IMethodBinding> overriddenMethods) {
		for (IMethodBinding overriddenMethod : overriddenMethods) {
			if (isBinary(overriddenMethod)) {
				return false;
			}
		}
		return true;
	}

	private boolean isBinary(IMethodBinding method) {
		ITypeBinding declaringClass = method.getDeclaringClass();
		IJavaElement element = declaringClass.getJavaElement();
		return element instanceof IType && ((IType) element).isBinary();
	}
}
