package org.overture.ide.vdmrt.ui;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.overture.ide.vdmrt.ui.internal.editor.VdmRtTextTools;


public class UIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = VdmRtUiPluginConstants.PLUGIN_ID;//"org.overturetool.ui";

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
	
	private VdmRtTextTools fPythonTextTools;
	 
	public synchronized VdmRtTextTools getTextTools() {
		if (fPythonTextTools == null)
			fPythonTextTools= new VdmRtTextTools(true);
	        return fPythonTextTools;
	}
	
	
	public static void log(Exception exception) {
		exception.printStackTrace();
	}
	
	
}
