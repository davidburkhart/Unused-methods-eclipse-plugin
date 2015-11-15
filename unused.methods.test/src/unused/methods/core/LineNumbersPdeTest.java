package unused.methods.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.SourceRange;
import org.junit.Test;

public class LineNumbersPdeTest extends PdeTestCaseWithTestProject {

	@Test
	public void findUnusedMethodsInProject() throws CoreException {
		IMethod lonelyMethod = findMethodByName("lonelyMethod");
		assertThat(lonelyMethod.getNameRange(), is((ISourceRange) new SourceRange(128, 12)));

		IMethod lonelyStaticMethod = findMethodByName("lonelyStaticMethod");
		assertThat(lonelyStaticMethod.getNameRange(), is((ISourceRange) new SourceRange(182, 18)));
	}

	private IMethod findMethodByName(String name) throws JavaModelException {
		IJavaElement[] children = project.asJavaProject().getChildren();
		return findMethod(children, name);
	}

	private IMethod findMethod(IJavaElement[] children, String name) throws JavaModelException {
		for (IJavaElement child : children) {
			if (child instanceof IMethod) {
				IMethod method = (IMethod) child;
				if (method.getElementName().equals(name)) {
					return method;
				}
			}
			if (child instanceof IParent) {
				IParent parent = (IParent) child;
				IMethod result = findMethod(parent.getChildren(), name);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	@Override
	protected String[] getTestFiles() {
		return new String[] { "/test/Movie.java", "/test/Actor.java" };
	}
}
