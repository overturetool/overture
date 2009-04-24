package org.overturetool.eclipse.plugins.editor.internal.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.overturetool.eclipse.plugins.editor.internal.ui.text.OvertureTextTools;
import org.overturetool.eclipse.plugins.editor.ui.EditorCoreUIConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class UIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = EditorCoreUIConstants.PLUGIN_ID;//"org.overturetool.ui";

	// The shared instance
	private static UIPlugin plugin;
	
	private OvertureTextTools OvertureTextTools;
	
	/**
	 * The constructor
	 */
	public UIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
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
	
	public synchronized OvertureTextTools getTextTools() {
		if (OvertureTextTools == null)
			OvertureTextTools= new OvertureTextTools(true);
		return OvertureTextTools;
	}
	
	public static void log(Exception exception) {
		exception.printStackTrace();
	}	

}
