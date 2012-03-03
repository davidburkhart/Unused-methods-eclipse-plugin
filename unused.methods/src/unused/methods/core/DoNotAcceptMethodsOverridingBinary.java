package unused.methods.core;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class DoNotAcceptMethodsOverridingBinary implements MethodFilter {

	@Override
	public boolean accept(MethodWithBinding method) {
		List<IMethodBinding> overriddenMethods = method.findThisAndOverriddenMethods();
		for (IMethodBinding binding : overriddenMethods) {
			if (isBinary(binding)) {
				return false;
			}
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
