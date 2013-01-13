package unused.methods.core;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.BindingKey;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IMethodBinding;

public class MethodWithBinding {

	private final BindingKey bindingKey;
	private final IMethod method;
	private List<BindingKey> overriddenMethodKeys;

	public MethodWithBinding(IMethodBinding binding, IMethod method, List<IMethodBinding> overriddenMethods) {
		this.bindingKey = new BindingKey(binding.getKey());
		this.method = method;
		overriddenMethodKeys = new LinkedList<BindingKey>();
		for (IMethodBinding overriddenMethod : overriddenMethods) {
			String key = overriddenMethod.getMethodDeclaration().getKey();
			overriddenMethodKeys.add(new BindingKey(key));
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
		return bindingKey.toString().equals(other.bindingKey.toString());
	}

	@Override
	public int hashCode() {
		return method.hashCode();
	}

	public List<BindingKey> findThisAndOverriddenMethods() {
		return overriddenMethodKeys;
	}
}
