package unused.methods;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;

class UnusedMethodsCollector extends ASTVisitor {

	private final MethodsInWorkspace methods = new MethodsInWorkspace();

	@Override
	public boolean visit(MethodDeclaration node) {
		IMethodBinding binding = node.resolveBinding();
		addToDeclaredMethods(binding, node);
		return true;
	}

	private void addToDeclaredMethods(IMethodBinding binding, ASTNode node) {
		if (binding == null) {
			// happens with main methods
			UnusedMethodsPlugin.getDefault().info("UnusedMethodsCollector.addToDeclaredMethods() null-binding for node: " + node);
			return;
		}

		if (binding.getDeclaringClass().isEnum() && binding.isConstructor()) {
			// ignore enum constructors, they are hard to handle because
			// javaElement of MethodBinding of EnumConstantDeclaration is null
			return;
		}

		IJavaElement javaElement = binding.getJavaElement();
		if (!(javaElement instanceof IMethod)) {
			UnusedMethodsPlugin.getDefault().info(
					"UnusedMethodsCollector.addToDeclaredMethods() binding not a method binding, node: " + node
							+ " javaElement:" + javaElement);
			return;
		}

		IMethod method = (IMethod) javaElement;
		methods.addDeclared(new MethodBindingAndHandle(binding, method));
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
			UnusedMethodsPlugin.getDefault().info("UnusedMethodsCollector.addToUsedMethods() null-binding for node:" + node);
			return;
		}

		IJavaElement javaElement = binding.getJavaElement();
		if (!(javaElement instanceof IMethod)) {
			// this seems to happen with construction of anonymous inner classes
			// or calls to default constructor when default constructor isn't
			// declared explicitly
			// or on values() or valueOf() methods of enums
			UnusedMethodsPlugin.getDefault().info(
					"UnusedMethodsCollector.addToUsedMethods() binding not a method binding, node:" + node
							+ " javaElement:" + javaElement);
			return;
		}

		IMethod method = (IMethod) javaElement;
		methods.addUsed(new MethodBindingAndHandle(binding, method));
	}

	public List<IMethod> computeResult() {
		return methods.filterUnusedMethods();
	}
}
