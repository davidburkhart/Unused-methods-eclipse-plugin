package unused.methods.core;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

final class MethodWithKey extends BaseMatcher<MethodWithBinding> {

	static Matcher<MethodWithBinding> methodWithKey(final String expectedName) {
		return new MethodWithKey(expectedName);
	}

	private final String expectedKey;

	private MethodWithKey(String expectedName) {
		this.expectedKey = expectedName;
	}

	@Override
	public boolean matches(Object item) {
		return ((MethodWithBinding) item).getBindingKey().toString().equals(expectedKey);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("A method with key ").appendValue(expectedKey);
	}
}