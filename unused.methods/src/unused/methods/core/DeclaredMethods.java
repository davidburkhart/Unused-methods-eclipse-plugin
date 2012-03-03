package unused.methods.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IMethodBinding;

public class DeclaredMethods {

	private final List<MethodFilter> filters = new LinkedList<MethodFilter>();
	private final Set<MethodWithBinding> methods = new HashSet<MethodWithBinding>();

	public void addMethod(MethodWithBinding method) {
		for (MethodFilter filter : filters) {
			if (!filter.accept(method)) {
				return;
			}
		}
		methods.add(method);
	}

	public void remove(MethodWithBinding methodToRemove) {
		if (methods.contains(methodToRemove)) {
			methods.remove(methodToRemove);
		}
		removeOverridingMethods(methodToRemove);
	}

	private void removeOverridingMethods(MethodWithBinding methodToRemove) {
		for (Iterator<MethodWithBinding> iterator = methods.iterator(); iterator.hasNext();) {
			List<IMethodBinding> overriddenMethods = iterator.next().findThisAndOverriddenMethods();
			for (IMethodBinding overriddenMethodBinding : overriddenMethods) {
				IJavaElement javaElement = overriddenMethodBinding.getJavaElement();
				if (javaElement != null && javaElement.equals(methodToRemove.getMethod())) {
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
