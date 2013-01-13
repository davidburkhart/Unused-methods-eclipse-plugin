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
		addToDeclaredMethods(node.resolveBinding());
		return true;
	}

	private void addToDeclaredMethods(IMethodBinding binding) {
		if (binding == null) {
			// happens with main methods
			return;
		}

		IMethodBinding declaration = binding.getMethodDeclaration();
		if (declaration.getDeclaringClass().isEnum() && declaration.isConstructor()) {
			// ignore enum constructors, they are hard to handle because
			// javaElement of MethodBinding of EnumConstantDeclaration is null
			return;
		}

		IJavaElement javaElement = declaration.getJavaElement();
		if (!(javaElement instanceof IMethod)) {
			return;
		}

		IMethod method = (IMethod) javaElement;
		methods.addMethod(method, declaration);
	}
}
