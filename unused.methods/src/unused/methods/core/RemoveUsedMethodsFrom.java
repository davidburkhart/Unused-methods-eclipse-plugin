package unused.methods.core;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;

public class RemoveUsedMethodsFrom extends ASTVisitor {

	private final DeclaredMethods methods;

	public RemoveUsedMethodsFrom(DeclaredMethods methods) {
		this.methods = methods;
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		IMethodBinding binding = node.resolveConstructorBinding();
		addToUsedMethods(binding, node);
		return true;
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		IMethodBinding binding = node.resolveConstructorBinding();
		addToUsedMethods(binding, node);
		return true;
	}

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		IMethodBinding binding = node.resolveConstructorBinding();
		addToUsedMethods(binding, node);
		return true;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		IMethodBinding binding = node.resolveMethodBinding();
		addToUsedMethods(binding, node);
		return true;
	}

	private void addToUsedMethods(IMethodBinding binding, ASTNode node) {
		if (binding == null) {
			return;
		}

		IJavaElement javaElement = binding.getJavaElement();
		if (!(javaElement instanceof IMethod)) {
			// this seems to happen with construction of anonymous inner classes
			// or calls to default constructor when default constructor isn't
			// declared explicitly
			// or on values() or valueOf() methods of enums
			return;
		}

		IMethod method = (IMethod) javaElement;
		methods.remove(new MethodWithBinding(binding, method));
	}
}
