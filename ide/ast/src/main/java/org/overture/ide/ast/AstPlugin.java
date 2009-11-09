package org.overture.ide.ast;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class AstPlugin extends Plugin
{
	// The plug-in ID
	public static final String PLUGIN_ID = AstPluginConstants.PLUGIN_ID;//"org.overturetool.core";

	// The shared instance
	private static AstPlugin plugin;
	
	
	
	/**
	 * The constructor
	 */
	public AstPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AstPlugin getDefault() {
		return plugin;
	}
	

	
	public static void log(Exception exception) {
		exception.printStackTrace();
	}	
}
