package org.overture.ide.builders.vdmj;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IShutdownListener;
import org.osgi.framework.BundleContext;


public class VdmjBuilderPlugin extends Plugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = VdmjBuilderPluginConstants.PLUGIN_ID;

	// The shared instance
	private static VdmjBuilderPlugin plugin;
	
	/**
	 * The constructor
	 */
	public VdmjBuilderPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
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
	public static VdmjBuilderPlugin getDefault() {
		return plugin;
	}

	public static void log(Exception ex) {
		if (DLTKCore.DEBUG){
			ex.printStackTrace();
		}
		String message = ex.getMessage();
		if (message == null){		
			message = "(no message)"; //$NON-NLS-1$
		}
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, 0, message, ex));
	}
	
	private final ListenerList shutdownListeners = new ListenerList();

	public void addShutdownListener(IShutdownListener listener) {
		shutdownListeners.add(listener);
	}

}
