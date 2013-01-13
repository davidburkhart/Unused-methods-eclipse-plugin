package unused.methods.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static unused.methods.core.MethodWithKey.methodWithKey;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

public class FindUnusedContructorsPdeTest extends PdeTestCaseWithTestProject {

	@Test
	public void findUnusedConstructorsInProject() throws CoreException {
		Set<MethodWithBinding> result = calculateUnusedMethods();
		assertThat(result, not(hasItem(methodWithKey("Lsrc/test/SuperClassWithExplicitlyUsedConstructor;.()V"))));
		assertThat(result, hasItem(methodWithKey("Lsrc/test/SuperClassWithUnusedConstructor;.()V")));
		assertThat(result.size(), is(1));
	}

	@Override
	protected String[] getTestFiles() {
		return new String[] { "/test/SuperClassWithExplicitlyUsedConstructor.java",
				"/test/SuperClassWithUnusedConstructor.java" };
	}
}
