package org.overture.ide.ui;





import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;



public class VdmUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = VdmUIPluginConstants.PLUGIN_ID;//"org.overturetool.ui";

	// The shared instance
	private static VdmUIPlugin plugin;
	
	
	
	/**
	 * The constructor
	 */
	public VdmUIPlugin() {
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
	public static VdmUIPlugin getDefault() {
		return plugin;
	}
	

	
	
	public static void log(Exception exception) {
		exception.printStackTrace();
	}
	
	
}
