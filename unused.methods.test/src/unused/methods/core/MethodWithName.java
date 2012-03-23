package unused.methods.core;

import org.eclipse.jdt.core.IMethod;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

final class MethodWithName extends BaseMatcher<IMethod> {

	static Matcher<IMethod> methodWithName(final String expectedName) {
		return new MethodWithName(expectedName);
	}

	private final String expectedName;

	private MethodWithName(String expectedName) {
		this.expectedName = expectedName;
	}

	@Override
	public boolean matches(Object item) {
		return ((IMethod) item).getElementName().equals(expectedName);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("A method with name ").appendValue(expectedName);
	}
}