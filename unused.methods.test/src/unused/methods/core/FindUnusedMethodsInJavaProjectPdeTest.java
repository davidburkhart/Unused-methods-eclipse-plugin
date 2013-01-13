package unused.methods.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static unused.methods.core.MethodWithKey.methodWithKey;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

public class FindUnusedMethodsInJavaProjectPdeTest extends PdeTestCaseWithTestProject {

	@Test
	public void findUnusedMethodsInProject() throws CoreException {
		// TODO separate Tests (static/nonstatic, used/unused)
		Set<MethodWithBinding> result = calculateUnusedMethods();
		assertThat(result, not(hasItem(methodWithKey("main"))));
		assertThat(result, not(hasItem(methodWithKey("playsIn"))));
		assertThat(result, not(hasItem(methodWithKey("getMainActor"))));
		assertThat(result, hasItem(methodWithKey("Lsrc/test/Actor;.lonelyMethod()V")));
		assertThat(result, hasItem(methodWithKey("Lsrc/test/Actor;.lonelyStaticMethod()V")));
		assertThat(result.size(), is(2));
	}

	// TODO run Test on single package
	// TODO test inheritance
	// TODO test interfaces
	// TODO test constructors
	// TODO test enums
	// TODO test anonymous classes

	@Override
	protected String[] getTestFiles() {
		return new String[] { "/test/Movie.java", "/test/Actor.java" };
	}
}
