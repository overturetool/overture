package org.overture.ide.platform;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class Activator extends AbstractUIPlugin
{

	private static Activator fgPlugin;
			
	
	


	public static boolean DEBUG = true;
	
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}
	
		
	private boolean fTrace = false;
		
	public boolean isTraceMode() {
		return fTrace;
	}



	public static Activator getDefault() {		
		return fgPlugin;
	}

	public Activator() {
		super();	
		fgPlugin = this;
	}
	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);		
		
		//getDefault().getLog().log(new Status(Status.INFO, PLUGIN_ID, Status.OK, "TCLDebugPlugin starting...", null));
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void stop(BundleContext context) throws Exception {
		try {			
			savePluginPreferences();
		} finally {
			fgPlugin = null;
			super.stop(context);
		}
	}

}
