package unused.methods.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.BindingKey;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IMethodBinding;

public class DeclaredMethods {

	private final List<MethodFilter> filters = new LinkedList<MethodFilter>();
	private final Set<MethodWithBinding> methods = new HashSet<MethodWithBinding>();

	public void addMethod(MethodWithBinding method, IMethodBinding binding) {
		for (MethodFilter filter : filters) {
			if (!filter.accept(method, binding)) {
				return;
			}
		}
		methods.add(method);
	}

	public void removeMethod(MethodWithBinding methodToRemove) {
		// TODO nur key des bindings verwenden?
		if (methods.contains(methodToRemove)) {
			methods.remove(methodToRemove);
		}
		removeOverridingMethods(methodToRemove);
	}

	private void removeOverridingMethods(MethodWithBinding methodToRemove) {
		for (Iterator<MethodWithBinding> iterator = methods.iterator(); iterator.hasNext();) {
			List<BindingKey> overriddenMethodsBindingKeys = iterator.next().findThisAndOverriddenMethods();
			for (BindingKey overriddenMethodBindingKey : overriddenMethodsBindingKeys) {
				if (methodToRemove.getBindingKey().toString().equals(overriddenMethodBindingKey.toString())) {
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
