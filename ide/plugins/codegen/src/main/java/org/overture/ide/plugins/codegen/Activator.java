package org.overture.ide.plugins.codegen;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = ICodeGenConstants.PLUGIN_ID;

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault()
	{
		return plugin;
	}
	
	public static void log(Exception exception)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, ICodeGenConstants.PLUGIN_ID, "Code Generator", exception));
	}

	public static void log(String message, Exception exception)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, ICodeGenConstants.PLUGIN_ID, message, exception));
	}

	
	/** 
	 * Initializes a preference store with default preference values 
	 * for this plug-in.
	 */
	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		
	}
}
