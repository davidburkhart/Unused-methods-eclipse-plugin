package unused.methods.core;

import org.eclipse.jdt.core.BindingKey;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
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
		ASTNode parent = node.getParent();
		while (!(parent instanceof MethodDeclaration)) {
			parent = parent.getParent();
		}
		MethodDeclaration declaringMethodDeclaration = (MethodDeclaration) parent;
		IAnnotationBinding[] annotations = declaringMethodDeclaration.resolveBinding().getAnnotations();
		// TODO filter by strongly ignored annotations

		if (binding != null) {
			IMethodBinding methodDeclaration = binding.getMethodDeclaration();
			methods.removeMethod(new BindingKey(methodDeclaration.getKey()));
		}
	}
}
