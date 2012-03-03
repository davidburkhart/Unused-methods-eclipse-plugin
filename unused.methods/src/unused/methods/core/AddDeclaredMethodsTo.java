package unused.methods.core;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class AddDeclaredMethodsTo extends ASTVisitor {

	private final DeclaredMethods methods;

	public AddDeclaredMethodsTo(DeclaredMethods methods) {
		this.methods = methods;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		addToDeclaredMethods(node.resolveBinding(), node);
		return true;
	}

	private void addToDeclaredMethods(IMethodBinding binding, MethodDeclaration node) {
		if (binding == null) {
			// happens with main methods
			return;
		}

		if (binding.getDeclaringClass().isEnum() && binding.isConstructor()) {
			// ignore enum constructors, they are hard to handle because
			// javaElement of MethodBinding of EnumConstantDeclaration is null
			return;
		}

		IJavaElement javaElement = binding.getJavaElement();
		if (!(javaElement instanceof IMethod)) {
			return;
		}

		IMethod method = (IMethod) javaElement;
		methods.addMethod(new MethodWithBinding(binding, method));
	}
}
