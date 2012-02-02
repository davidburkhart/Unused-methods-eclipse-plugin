package unused.methods;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.internal.corext.dom.Bindings;

@SuppressWarnings("restriction")
public class MethodWithBinding {

	private final IMethodBinding binding;
	private final IMethod method;

	public MethodWithBinding(IMethodBinding binding, IMethod method) {
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
		return other instanceof MethodWithBinding && equals((MethodWithBinding) other);
	}

	private boolean equals(MethodWithBinding other) {
		return method.equals(other.method);
	}

	@Override
	public int hashCode() {
		return method.hashCode();
	}

	public List<IMethodBinding> findThisAndOverriddenMethods() {
		List<IMethodBinding> result = new LinkedList<IMethodBinding>();
		IMethodBinding overriddenMethod = Bindings.findOverriddenMethod(binding, true);
		while (overriddenMethod != null) {
			result.add(overriddenMethod);
			overriddenMethod = Bindings.findOverriddenMethod(overriddenMethod, true);
		}
		return result;
	}
}
