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

public class FindUnusedMethodsInJavaProjectPdeTest extends PdeTestCaseWithTestProject {

	@Test
	public void findUnusedMethodsInProject() throws CoreException, IOException {
		// TODO separate Tests (static/nonstatic, used/unused)
		List<IMethod> result = calculateUnusedMethods();
		assertThat(result, not(hasItem(methodWithName("main"))));
		assertThat(result, not(hasItem(methodWithName("playsIn"))));
		assertThat(result, not(hasItem(methodWithName("getMainActor"))));
		assertThat(result, hasItem(methodWithName("lonelyMethod")));
		assertThat(result, hasItem(methodWithName("lonelyStaticMethod")));
		assertThat(result.size(), is(2));
	}

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
