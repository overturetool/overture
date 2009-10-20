package org.overture.ide.vdmpp.debug.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class VDMPPDebugUIPlugin extends AbstractUIPlugin {
	// The shared instance.
	private static VDMPPDebugUIPlugin plugin;
	
	public VDMPPDebugUIPlugin() {
		plugin = this;
	}

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
	public static VDMPPDebugUIPlugin getDefault() {
		return plugin;
	}
}
