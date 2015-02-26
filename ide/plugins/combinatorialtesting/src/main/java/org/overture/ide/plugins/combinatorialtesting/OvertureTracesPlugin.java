/*
 * #%~
 * Combinatorial Testing
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.plugins.combinatorialtesting;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.overture.ide.debug.core.IDebugConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class OvertureTracesPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = ITracesConstants.PLUGIN_ID;

	// The shared instance
	private static OvertureTracesPlugin plugin;

	public static final String IMG_ERROR = "icons/Critical16.png";

	public static final String IMG_INTERPRETER = "icons/debug16.png";

	public static final String IMG_VDM_TOOLS_LOGO = "icons/clcl16/vdmt_menu.png";
	public static final String IMG_VDM_TOOLS_LOGO_PRESSED = "icons/clcl16/vdmt_p_menu.png";

//	public static final String IMG_VDMJ_LOGO = "icons/clcl16/oml_menu.png";
//	public static final String IMG_VDMJ_LOGO_PRESSED = "icons/clcl16/oml_p_menu.png";

	public static final String IMG_RUN_SAVE = "icons/clcl16/save_menu.png";
	public static final String IMG_RUN_ALL_TRACES = "icons/clcl16/run_all_menu.png"; //$NON-NLS-1$
	public static final String IMG_RUN_SELECTED_TRACE = "icons/clcl16/run_s_menu.png"; //$NON-NLS-1$

	public static final String IMG_TRACE_TEST_CASE_SUCCES = "icons/cview16/succes_obj.png"; //$NON-NLS-1$
	public static final String IMG_TRACE_TEST_CASE_FAIL = "icons/cview16/faild_obj.png"; //$NON-NLS-1$
	public static final String IMG_TRACE_TEST_CASE_EXPANSIN_FAIL = "icons/cview16/faild_obj.png"; //$NON-NLS-1$
	public static final String IMG_TRACE_TEST_CASE_UNKNOWN = "icons/cview16/search_obj.png"; //$NON-NLS-1$
	public static final String IMG_TRACE_TEST_CASE_UNDETERMINED = "icons/cview16/undetermined_obj.png"; //$NON-NLS-1$

	public static final String IMG_TRACE_TEST_CASE_SKIPPED = "icons/cview16/skipped_obj.png"; //$NON-NLS-1$
	public static final String IMG_TRACE_CLASS = "icons/cview16/class_obj.png"; //$NON-NLS-1$
	public static final String IMG_TRACE = "icons/cview16/trace_obj.png"; //$NON-NLS-1$

	public static final String IMG_TRACE_TEST_SORT = "icons/clcl16/sort_menu.png"; //$NON-NLS-1$
	public static final String IMG_REFRESH = "icons/clcl16/refresh.gif"; //$NON-NLS-1$

	public static final String IMG_TRACE_TEST_CASE_FILTER_UNDETERMINED = "icons/clcl16/undetermined_filter_menu.png"; //$NON-NLS-1$
//	public static final String IMG_TRACE_TEST_CASE_FILTER_UNDETERMINED_PRESSED = "icons/clcl16/undetermined_filter_p_menu.png"; //$NON-NLS-1$

	public static final String IMG_TRACE_TEST_CASE_FILTER_SUCCES = "icons/clcl16/ok_filter_menu.png"; //$NON-NLS-1$
//	public static final String IMG_TRACE_TEST_CASE_FILTER_SUCCES_PRESSED = "icons/clcl16/ok_filter_p_menu.png"; //$NON-NLS-1$

	/**
	 * The constructor
	 */
	public OvertureTracesPlugin()
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
	public static OvertureTracesPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static void log(Exception exception)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, ITracesConstants.PLUGIN_ID, "TracesPlugin", exception));
	}

	public static void log(String message, Exception exception)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, ITracesConstants.PLUGIN_ID, message, exception));
	}

	/**
	 * Initializes a preference store with default preference values for this plug-in.
	 */
	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store)
	{
		initializeDefaultMainPreferences(store);
	}

	public static void initializeDefaultMainPreferences(IPreferenceStore store)
	{
		store.setDefault(ITracesConstants.SORT_VIEW, false);
		store.setDefault(ITracesConstants.REMOTE_DEBUG_PREFERENCE, false);
		store.setDefault(ITracesConstants.REMOTE_DEBUG_FIXED_PORT, false);
		store.setDefault(ITracesConstants.TRACE_REDUCTION_TYPE, ITracesConstants.TRACE_REDUCTION_DEFAULT_TYPE);
		store.setDefault(ITracesConstants.TRACE_SEED, ITracesConstants.TRACE_FILTERING_DEFAULT_SEED);
		store.setDefault(ITracesConstants.TRACE_SUBSET_LIMITATION, ITracesConstants.TRACE_SUBSET_LIMITATION_DEFAULT);
		store.setDefault(IDebugConstants.VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION, "");
		
	}

}
