package asteditor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.lausdahl.IAstEditorConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = IAstEditorConstants.PLUGIN_ID;//"astEditor"; //$NON-NLS-1$

	public static final boolean DEBUG = true;

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
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
	public static Activator getDefault() {
		return plugin;
	}
	
	public static void log(Exception e) {
		getDefault().getLog().log(new Status(IStatus.ERROR,IAstEditorConstants.PLUGIN_ID,"AstEditor",e));
		
	}
	
	public static void log(String msg,Exception e) {
		getDefault().getLog().log(new Status(IStatus.ERROR,IAstEditorConstants.PLUGIN_ID,msg,e));
		
	}

	public static void logErrorMessage(String message) {
		getDefault().getLog().log(new Status(IStatus.ERROR,IAstEditorConstants.PLUGIN_ID,message));

	}

}
