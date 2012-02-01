package unused.methods;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;

public class MethodCollection implements Iterable<MethodBindingAndHandle> {

	private final Set<MethodBindingAndHandle> methods = new HashSet<MethodBindingAndHandle>();

	public MethodCollection() {
	}

	public MethodCollection(MethodCollection other) {
		this.methods.addAll(other.methods);
	}

	public void add(MethodBindingAndHandle method) {
		methods.add(method);
	}

	public void remove(MethodBindingAndHandle method) {
		methods.remove(method);
	}

	public void removeAll(MethodCollection other) {
		methods.removeAll(other.methods);
	}

	@Override
	public Iterator<MethodBindingAndHandle> iterator() {
		return new HashSet<MethodBindingAndHandle>(methods).iterator();
	}

	public List<IMethod> collectHandles() {
		LinkedList<IMethod> result = new LinkedList<IMethod>();
		for (MethodBindingAndHandle bindingAndHandle : this) {
			result.add(bindingAndHandle.getMethod());
		}
		return result;
	}

}
