package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class OvertureDebugUIPlugin extends AbstractUIPlugin {

	public OvertureDebugUIPlugin() {
		plugin = this;
	}

	// The shared instance.
	private static OvertureDebugUIPlugin plugin;

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static OvertureDebugUIPlugin getDefault() {
		return plugin;
	}
}
