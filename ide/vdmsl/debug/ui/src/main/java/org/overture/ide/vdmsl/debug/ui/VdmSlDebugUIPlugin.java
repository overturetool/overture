package org.overture.ide.vdmsl.debug.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class VdmSlDebugUIPlugin extends AbstractUIPlugin {
	// The shared instance.
	private static VdmSlDebugUIPlugin plugin;
	
	public VdmSlDebugUIPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static VdmSlDebugUIPlugin getDefault() {
		return plugin;
	}
}
