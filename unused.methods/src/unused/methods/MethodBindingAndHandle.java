package unused.methods;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IMethodBinding;

public class MethodBindingAndHandle {

	private final IMethodBinding binding;
	private final IMethod method;

	public MethodBindingAndHandle(IMethodBinding binding, IMethod method) {
		// TODO could we live without the binding thing?
		// see
		// http://wiki.eclipse.org/JDT/FAQ#How_to_go_from_one_of_IBinding.2C_IJavaElement.2C_ASTNode_to_another.3F
		this.binding = binding;
		this.method = method;
	}

	public IMethodBinding getBinding() {
		return binding;
	}

	public IMethod getMethod() {
		return method;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof MethodBindingAndHandle && equals((MethodBindingAndHandle) other);
	}

	private boolean equals(MethodBindingAndHandle other) {
		return method.equals(other.method);
	}

	@Override
	public int hashCode() {
		return method.hashCode();
	}
}
