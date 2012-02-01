package unused.methods;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.internal.corext.dom.Bindings;

@SuppressWarnings("restriction")
public class MethodsInWorkspace {

	// TODO This class gets too big

	private final MethodCollection declaredMethods = new MethodCollection();
	private final MethodCollection usedMethods = new MethodCollection();

	public void addDeclared(MethodBindingAndHandle method) {
		declaredMethods.add(method);
	}

	public void addUsed(MethodBindingAndHandle method) {
		usedMethods.add(method);
	}

	public List<IMethod> filterUnusedMethods() {
		MethodCollection result = assumeAllDeclaredMethodsUnused();
		removeUsedMethods(result);
		removeJUnitTestMethods(result);
		removeUsedOverriddenMethods(result);
		return result.collectHandles();
	}

	private void removeJUnitTestMethods(MethodCollection result) {
		// TODO JUnit3 (check superclasses for TestCase and method-name
		// beginning with "test")
		for (MethodBindingAndHandle bindingAndHandle : result) {
			if (hasTestAnnotation(bindingAndHandle)) {
				UnusedMethodsPlugin.getDefault().info("found JUnit4 test method: " + bindingAndHandle.getMethod());
				result.remove(bindingAndHandle);
			}
		}
	}

	private boolean hasTestAnnotation(MethodBindingAndHandle bindingAndHandle) {
		// TODO configurable Annotations (prefs) e.g. Spring stuff, ...
		// TODO autocompletion dialog when adding Annotation to prefs ;-)
		boolean isTest = false;
		IAnnotationBinding[] annotations = bindingAndHandle.getBinding().getAnnotations();
		for (IAnnotationBinding annotation : annotations) {
			ITypeBinding annotationType = annotation.getAnnotationType();
			if ("org.junit.Test".equals(annotationType.getQualifiedName())) {
				isTest = true;
			}
		}
		return isTest;
	}

	private MethodCollection assumeAllDeclaredMethodsUnused() {
		return new MethodCollection(declaredMethods);
	}

	private void removeUsedMethods(MethodCollection result) {
		result.removeAll(usedMethods);
	}

	private void removeUsedOverriddenMethods(MethodCollection result) {
		for (MethodBindingAndHandle bindingAndHandle : result) {
			checkResultForUsedOverriddenMethods(result, bindingAndHandle);
		}
	}

	private void checkResultForUsedOverriddenMethods(MethodCollection result, MethodBindingAndHandle bindingAndHandle) {
		List<IMethodBinding> overriddenMethods = collectOverriddenMethods(bindingAndHandle.getBinding());
		for (IMethodBinding overriddenMethod : overriddenMethods) {
			if (isUsed(overriddenMethod)) {
				UnusedMethodsPlugin.getDefault().info("found used overridden method for: " + bindingAndHandle.getMethod());
				result.remove(bindingAndHandle);
				break;
			} else if (isBinary(overriddenMethod)) {
				UnusedMethodsPlugin.getDefault().info("found overridden framework method: " + bindingAndHandle.getMethod());
				result.remove(bindingAndHandle);
				break;
			}
		}
	}

	private boolean isBinary(IMethodBinding method) {
		boolean overriddenMethodIsBinary = false;
		ITypeBinding declaringClass = method.getDeclaringClass();
		IJavaElement javaElement = declaringClass.getJavaElement();
		if (javaElement instanceof IType) {
			IType type = (IType) javaElement;
			if (type.isBinary()) {
				overriddenMethodIsBinary = true;
			}
		}
		return overriddenMethodIsBinary;
	}

	private boolean isUsed(IMethodBinding overriddenMethod) {
		boolean isUsed = false;
		for (MethodBindingAndHandle used : usedMethods) {
			if (used.getMethod().equals(overriddenMethod.getJavaElement())) {
				isUsed = true;
				break;
			}
		}
		return isUsed;
	}

	private List<IMethodBinding> collectOverriddenMethods(IMethodBinding binding) {
		List<IMethodBinding> result = new LinkedList<IMethodBinding>();
		IMethodBinding overriddenMethod = Bindings.findOverriddenMethod(binding, true);
		while (overriddenMethod != null) {
			result.add(overriddenMethod);
			overriddenMethod = Bindings.findOverriddenMethod(overriddenMethod, true);
		}
		return result;
	}
}
