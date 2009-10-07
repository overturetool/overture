package org.overture.ide.vdmpp.ui;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.overture.ide.vdmpp.ui.internal.editor.VdmPpTextTools;


public class UIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = VdmPpUiPluginConstants.PLUGIN_ID;//"org.overturetool.ui";

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
	
	private VdmPpTextTools fPythonTextTools;
	 
	public synchronized VdmPpTextTools getTextTools() {
		if (fPythonTextTools == null)
			fPythonTextTools= new VdmPpTextTools(true);
	        return fPythonTextTools;
	}
	
	
	public static void log(Exception exception) {
		exception.printStackTrace();
	}
	
	
}
