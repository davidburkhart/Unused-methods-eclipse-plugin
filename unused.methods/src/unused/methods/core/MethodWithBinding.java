package unused.methods.core;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.BindingKey;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.internal.corext.dom.Bindings;

@SuppressWarnings("restriction")
public class MethodWithBinding {

	private final BindingKey bindingKey;
	private final IMethod method;
	private List<BindingKey> overriddenMethodKeys;

	public MethodWithBinding(IMethodBinding binding, IMethod method) {
		this.bindingKey = new BindingKey(binding.getKey());
		this.method = method;
		overriddenMethodKeys = new LinkedList<BindingKey>();
		IMethodBinding overriddenMethod = Bindings.findOverriddenMethod(binding, true);
		while (overriddenMethod != null) {
			String key = overriddenMethod.getMethodDeclaration().getKey();
			overriddenMethodKeys.add(new BindingKey(key));
			overriddenMethod = Bindings.findOverriddenMethod(overriddenMethod, true);
		}
	}

	public BindingKey getBindingKey() {
		return bindingKey;
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

	public List<BindingKey> findThisAndOverriddenMethods() {
		return overriddenMethodKeys;
	}
}
