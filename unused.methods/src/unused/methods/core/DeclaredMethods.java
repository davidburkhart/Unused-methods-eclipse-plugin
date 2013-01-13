package unused.methods.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.BindingKey;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.internal.corext.dom.Bindings;

@SuppressWarnings("restriction")
public class DeclaredMethods {

	private final List<MethodFilter> filters = new LinkedList<MethodFilter>();
	private final Set<MethodWithBinding> methods = new HashSet<MethodWithBinding>();

	public void addMethod(IMethod method, IMethodBinding binding) {
		List<IMethodBinding> overriddenMethods = getOverriddenMethods(binding);
		MethodWithBinding methodWithBinding = new MethodWithBinding(binding, method, overriddenMethods);
		for (MethodFilter filter : filters) {
			if (!filter.accept(binding, overriddenMethods)) {
				return;
			}
		}
		methods.add(methodWithBinding);
	}

	private List<IMethodBinding> getOverriddenMethods(IMethodBinding binding) {
		List<IMethodBinding> overriddenMethods = new LinkedList<IMethodBinding>();
		IMethodBinding overriddenMethod = Bindings.findOverriddenMethod(binding, true);
		while (overriddenMethod != null) {
			overriddenMethods.add(overriddenMethod);
			overriddenMethod = Bindings.findOverriddenMethod(overriddenMethod, true);
		}
		return overriddenMethods;
	}

	public void removeMethod(BindingKey keyToRemove) {
		for (Iterator<MethodWithBinding> iterator = methods.iterator(); iterator.hasNext();) {
			BindingKey next = iterator.next().getBindingKey();
			if (next.toString().equals(keyToRemove.toString())) {
				iterator.remove();
			}
		}
		removeOverridingMethods(keyToRemove);
	}

	private void removeOverridingMethods(BindingKey keyToRemove) {
		for (Iterator<MethodWithBinding> iterator = methods.iterator(); iterator.hasNext();) {
			List<BindingKey> overriddenMethodsBindingKeys = iterator.next().findThisAndOverriddenMethods();
			for (BindingKey overriddenMethodBindingKey : overriddenMethodsBindingKeys) {
				if (keyToRemove.toString().equals(overriddenMethodBindingKey.toString())) {
					iterator.remove();
				}
			}
		}
	}

	public void addFilter(MethodFilter filter) {
		filters.add(filter);
	}

	public List<IMethod> getMethods() {
		List<IMethod> result = new LinkedList<IMethod>();
		for (MethodWithBinding method : methods) {
			result.add(method.getMethod());
		}
		return result;
	}
}
