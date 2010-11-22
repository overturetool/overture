package org.overture.ide.vdmpp.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class VdmPpUIPlugin extends AbstractUIPlugin {

	private static final boolean DEBUG = true;
	private static VdmPpUIPlugin plugin;
	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
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
	public static VdmPpUIPlugin getDefault() {
		return plugin;
	}
		
	public static void println(String s)
	{
		if(DEBUG)
			System.out.println(s);
	}
	
	public static void log(Exception exception) {
		getDefault().getLog().log(new Status(IStatus.ERROR,IVdmPpUiConstants.PLUGIN_ID,"VdmPpUIPlugin",exception));
	}
	public static void log(String message,Exception exception) {
		getDefault().getLog().log(new Status(IStatus.ERROR,IVdmPpUiConstants.PLUGIN_ID,message,exception));
	}

	public static void logErrorMessage(String message) {
		getDefault().getLog().log(new Status(IStatus.ERROR,IVdmPpUiConstants.PLUGIN_ID,message));

	}

}
