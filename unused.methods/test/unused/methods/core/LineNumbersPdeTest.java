package unused.methods.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.SourceRange;
import org.junit.Test;

public class LineNumbersPdeTest extends PdeTestCaseWithTestProject {

	@Test
	public void findUnusedMethodsInProject() throws CoreException, IOException {
		List<IMethod> unusedMethods = calculateUnusedMethods();

		IMethod lonelyMethod = findMethodByName(unusedMethods, "lonelyMethod");
		assertThat(lonelyMethod.getNameRange(), is((ISourceRange) new SourceRange(128, 12)));

		IMethod lonelyStaticMethod = findMethodByName(unusedMethods, "lonelyStaticMethod");
		assertThat(lonelyStaticMethod.getNameRange(), is((ISourceRange) new SourceRange(182, 18)));
	}

	private IMethod findMethodByName(List<IMethod> unusedMethods, String name) {
		for (IMethod method : unusedMethods) {
			if (name.equals(method.getElementName())) {
				return method;
			}
		}
		return null;
	}

	@Override
	protected String[] getTestFiles() {
		return new String[] { "/test/Movie.java", "/test/Actor.java" };
	}
}
