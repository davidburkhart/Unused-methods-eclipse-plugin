package unused.methods.core;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.BindingKey;
import org.eclipse.jdt.core.dom.IMethodBinding;

public class MethodWithBinding {

	private final BindingKey bindingKey;
	private List<BindingKey> overriddenMethodKeys;

	public MethodWithBinding(IMethodBinding binding, List<IMethodBinding> overriddenMethods) {
		this.bindingKey = new BindingKey(binding.getKey());
		overriddenMethodKeys = new LinkedList<BindingKey>();
		for (IMethodBinding overriddenMethod : overriddenMethods) {
			String key = overriddenMethod.getMethodDeclaration().getKey();
			overriddenMethodKeys.add(new BindingKey(key));
		}
	}

	public BindingKey getBindingKey() {
		return bindingKey;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof MethodWithBinding && equals((MethodWithBinding) other);
	}

	private boolean equals(MethodWithBinding other) {
		return bindingKey.toString().equals(other.bindingKey.toString());
	}

	@Override
	public int hashCode() {
		return bindingKey.toString().hashCode();
	}

	@Override
	public String toString() {
		return bindingKey.toString();
	}

	public List<BindingKey> findThisAndOverriddenMethods() {
		return overriddenMethodKeys;
	}
}
