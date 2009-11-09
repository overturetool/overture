package org.overture.ide.vdmrt.debug.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class VdmRtDebugUIPlugin extends AbstractUIPlugin {
	// The shared instance.
	private static VdmRtDebugUIPlugin plugin;
	
	public VdmRtDebugUIPlugin() {
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
	public static VdmRtDebugUIPlugin getDefault() {
		return plugin;
	}
}
