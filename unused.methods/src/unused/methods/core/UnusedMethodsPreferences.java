package unused.methods.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import unused.methods.UnusedMethodsPlugin;

public class UnusedMethodsPreferences {

	private Set<UnusedMethodAnnotationPreference> preferences = new HashSet<UnusedMethodAnnotationPreference>();

	public UnusedMethodsPreferences() {
		loadPreferences();
	}

	public void add(String fullyQualifiedName) {
		preferences.add(new UnusedMethodAnnotationPreference(fullyQualifiedName));
	}

	public void removeAll(List<UnusedMethodAnnotationPreference> toRemove) {
		preferences.removeAll(toRemove);
	}

	public void storeAndFlush() throws BackingStoreException {
		IEclipsePreferences pluginPreferences = InstanceScope.INSTANCE.getNode(UnusedMethodsPlugin.getPluginId());
		pluginPreferences.put("ANNOTATIONS_PREFERENCE", preferencesToString());
		pluginPreferences.flush();
	}

	public UnusedMethodAnnotationPreference[] getPreferences() {
		return preferences.toArray(new UnusedMethodAnnotationPreference[preferences.size()]);
	}

	private void loadPreferences() {
		IEclipsePreferences pluginPreferences = InstanceScope.INSTANCE.getNode(UnusedMethodsPlugin.getPluginId());
		initPreferences(pluginPreferences.get("ANNOTATIONS_PREFERENCE", ""));
	}

	private void initPreferences(String preferencesString) {
		if (preferencesString.isEmpty()) {
			return;
		}
		String[] preferenceParts = preferencesString.split("\\|");
		for (String part : preferenceParts) {
			String[] nameAndIgnored = part.split("\\;");
			UnusedMethodAnnotationPreference preference = new UnusedMethodAnnotationPreference(nameAndIgnored[0]);
			preference.setStronglyIgnored(Boolean.valueOf(nameAndIgnored[1]));
			preferences.add(preference);
		}
	}

	private String preferencesToString() {
		StringBuilder preferenceString = new StringBuilder();
		for (UnusedMethodAnnotationPreference preference : preferences) {
			preferenceString.append(toPreferenceString(preference) + "|");
		}
		if (preferenceString.length() > 0) {
			preferenceString.deleteCharAt(preferenceString.length() - 1);
		}
		return preferenceString.toString();
	}

	private String toPreferenceString(UnusedMethodAnnotationPreference preference) {
		String fullyQualifiedName = preference.getFullyQualifiedName();
		String stronglyIgnoredString = Boolean.valueOf(preference.isStronglyIgnored()).toString();
		return fullyQualifiedName + ";" + stronglyIgnoredString;
	}

	public boolean isStronglyIgnored(String qualifiedName) {
		for (UnusedMethodAnnotationPreference preference : preferences) {
			if (preference.getFullyQualifiedName().equals(qualifiedName)) {
				return preference.isStronglyIgnored();
			}
		}
		return false;
	}
}
