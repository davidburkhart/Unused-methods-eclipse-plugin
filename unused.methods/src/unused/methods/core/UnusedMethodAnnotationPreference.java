package unused.methods.core;

public class UnusedMethodAnnotationPreference {

	private final String fullyQualifiedName;
	private boolean stronglyIgnored;

	public UnusedMethodAnnotationPreference(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}

	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	public boolean isStronglyIgnored() {
		return stronglyIgnored;
	}

	public void setStronglyIgnored(boolean stronglyIgnored) {
		this.stronglyIgnored = stronglyIgnored;
	}

	@Override
	public int hashCode() {
		return fullyQualifiedName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof UnusedMethodAnnotationPreference
				&& fullyQualifiedName.equals(((UnusedMethodAnnotationPreference) obj).fullyQualifiedName);
	}
}
