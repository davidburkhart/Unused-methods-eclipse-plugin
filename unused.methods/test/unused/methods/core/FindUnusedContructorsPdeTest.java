package unused.methods.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static unused.methods.core.MethodWithName.methodWithName;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.junit.Test;

public class FindUnusedContructorsPdeTest extends PdeTestCaseWithTestProject {

	@Test
	public void findUnusedConstructorsInProject() throws CoreException, IOException {
		List<IMethod> result = calculateUnusedMethods();
		assertThat(result, not(hasItem(methodWithName("SuperClassWithExplicitlyUsedConstructor"))));
		assertThat(result, hasItem(methodWithName("SuperClassWithUnusedConstructor")));
		assertThat(result.size(), is(1));
	}

	@Override
	protected String[] getTestFiles() {
		return new String[] { "/test/SuperClassWithExplicitlyUsedConstructor.java",
				"/test/SuperClassWithUnusedConstructor.java" };
	}
}
