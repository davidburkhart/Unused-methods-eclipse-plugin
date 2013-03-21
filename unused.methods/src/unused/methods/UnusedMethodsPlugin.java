package unused.methods;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class UnusedMethodsPlugin extends AbstractUIPlugin {

	// TODO setup build with tycho (also running pde tests!)

	private static UnusedMethodsPlugin plugin;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static UnusedMethodsPlugin getDefault() {
		return plugin;
	}

	public static String getPluginId() {
		return plugin.getBundle().getSymbolicName();
	}
}
