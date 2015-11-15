package unused.methods.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class AddMarkerToMethods extends ASTVisitor {

	private Set<String> bindings;

	public AddMarkerToMethods(Set<MethodWithBinding> methods) {
		bindings = new HashSet<String>();
		for (MethodWithBinding method : methods) {
			bindings.add(method.getBindingKey().toString());
		}
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		addMarker(node.resolveBinding());
		return true;
	}

	private void addMarker(IMethodBinding binding) {
		if (binding == null) {
			// happens with main methods
			return;
		}

		IMethodBinding declaration = binding.getMethodDeclaration();

		if (!bindings.contains(declaration.getKey())) {
			return;
		}

		IJavaElement javaElement = declaration.getJavaElement();
		if (!(javaElement instanceof IMethod)) {
			return;
		}

		IMethod method = (IMethod) javaElement;
		UnusedMethodsMarker.on(method);
	}
}
