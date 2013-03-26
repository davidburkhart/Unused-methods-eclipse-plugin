package unused.methods.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.BindingKey;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

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

		if (isBinary(binding)) {
			return;
		}

		// TODO Test for filter by strongly ignored annotations
		if (isStronglyIgnored(collectMethodAndTypAnnotations(node))) {
			return;
		}

		IMethodBinding methodDeclaration = binding.getMethodDeclaration();
		methods.removeMethod(new BindingKey(methodDeclaration.getKey()));
	}

	private List<IAnnotationBinding> collectMethodAndTypAnnotations(ASTNode node) {
		List<IAnnotationBinding> annotations = new LinkedList<IAnnotationBinding>();
		annotations.addAll(collectMethodAnnotations(node));
		annotations.addAll(collectTypeAnnotations(node));
		return annotations;
	}

	private List<IAnnotationBinding> collectMethodAnnotations(ASTNode node) {
		MethodDeclaration methodDeclaration = findParent(MethodDeclaration.class, node);
		if (methodDeclaration != null) {
			IMethodBinding binding = methodDeclaration.resolveBinding();
			if (binding != null) {
				return Arrays.asList(binding.getAnnotations());
			}
		}
		return Collections.emptyList();
	}

	private List<IAnnotationBinding> collectTypeAnnotations(ASTNode node) {
		TypeDeclaration typeDeclaration = findParent(TypeDeclaration.class, node);
		if (typeDeclaration != null) {
			return Arrays.asList(typeDeclaration.resolveBinding().getAnnotations());
		}
		return Collections.emptyList();
	}

	private boolean isBinary(IMethodBinding method) {
		ITypeBinding declaringClass = method.getDeclaringClass();
		IJavaElement element = declaringClass.getJavaElement();
		return element instanceof IType && ((IType) element).isBinary();
	}

	private boolean isStronglyIgnored(Iterable<IAnnotationBinding> annotations) {
		for (IAnnotationBinding annotationBinding : annotations) {
			String qualifiedName = annotationBinding.getAnnotationType().getQualifiedName();
			if (new UnusedMethodsPreferences().isStronglyIgnored(qualifiedName)) {
				return true;
			}
		}
		return false;
	}

	private <T> T findParent(Class<T> clazz, ASTNode node) {
		ASTNode parent = node.getParent();
		while (parent != null && !(clazz.isAssignableFrom(parent.getClass()))) {
			parent = parent.getParent();
		}
		return clazz.cast(parent);
	}
}
