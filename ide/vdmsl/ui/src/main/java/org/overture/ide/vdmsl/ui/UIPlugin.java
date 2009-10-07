package org.overture.ide.vdmsl.ui;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.overture.ide.vdmsl.ui.internal.editor.VdmSlTextTools;


public class UIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = VdmSlUiPluginConstants.PLUGIN_ID;//"org.overturetool.ui";

	// The shared instance
	private static UIPlugin plugin;
	
	
	
	/**
	 * The constructor
	 */
	public UIPlugin() {
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
	public static UIPlugin getDefault() {
		return plugin;
	}
	
	private VdmSlTextTools fPythonTextTools;
	 
	public synchronized VdmSlTextTools getTextTools() {
		if (fPythonTextTools == null)
			fPythonTextTools= new VdmSlTextTools(true);
	        return fPythonTextTools;
	}
	
	
	public static void log(Exception exception) {
		exception.printStackTrace();
	}
	
	
}
