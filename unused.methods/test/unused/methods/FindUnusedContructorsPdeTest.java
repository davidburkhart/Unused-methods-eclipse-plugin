package unused.methods;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static unused.methods.MethodWithName.methodWithName;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;

public class FindUnusedContructorsPdeTest extends PdeTestCaseWithTestProject {

	@Test
	public void findUnusedConstructorsInProject() throws CoreException, IOException {
		List<IMethod> result = calculateUnusedMethods();
		assertThat(result, not(hasItem(methodWithName("SuperClassWithExplicitlyUsedConstructor"))));
		assertThat(result, hasItem(methodWithName("SuperClassWithUnusedConstructor")));
		assertThat(result.size(), is(1));
	}

	private List<IMethod> calculateUnusedMethods() throws JavaModelException {
		FindUnusedMethodsInJavaProject finder = new FindUnusedMethodsInJavaProject(project.asJavaProject());
		finder.run(new NullProgressMonitor());
		return finder.getUnusedMethods();
	}

	@Override
	protected String[] getTestFiles() {
		return new String[] { "/test/SuperClassWithExplicitlyUsedConstructor.java",
				"/test/SuperClassWithUnusedConstructor.java" };
	}
}
