package org.overture.ide.plugins.traces;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class OvertureTracesPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.overture.ide.plugins.traces";

	// The shared instance
	private static OvertureTracesPlugin plugin;
	
	
	public static final String IMG_ERROR = "icons/Critical16.png";
	
	public static final String IMG_INTERPRETER = "icons/debug16.png";
	
	public static final String IMG_VDM_TOOLS_LOGO = "icons/clcl16/vdmt_menu.png";
	public static final String IMG_VDM_TOOLS_LOGO_PRESSED = "icons/clcl16/vdmt_p_menu.png";
	
	public static final String IMG_VDMJ_LOGO = "icons/clcl16/oml_menu.png";
	public static final String IMG_VDMJ_LOGO_PRESSED = "icons/clcl16/oml_p_menu.png";
	
	public static final String IMG_RUN_SAVE = "icons/clcl16/save_menu.png"; 
    public static final String IMG_RUN_ALL_TRACES = "icons/clcl16/run_all_menu.png"; //$NON-NLS-1$
    public static final String IMG_RUN_SELECTED_TRACE = "icons/clcl16/run_selected_menu.png"; //$NON-NLS-1$

    public static final String IMG_TRACE_TEST_CASE_SUCCES = "icons/cview16/succes_obj.png"; //$NON-NLS-1$
    public static final String IMG_TRACE_TEST_CASE_FAIL = "icons/cview16/faild_obj.png"; //$NON-NLS-1$
    public static final String IMG_TRACE_TEST_CASE_EXPANSIN_FAIL = "icons/cview16/faild_obj.png"; //$NON-NLS-1$
    public static final String IMG_TRACE_TEST_CASE_UNKNOWN = "icons/cview16/search_obj.png"; //$NON-NLS-1$
    public static final String IMG_TRACE_TEST_CASE_UNDETERMINED = "icons/cview16/undetermined_obj.png"; //$NON-NLS-1$
   
    public static final String IMG_TRACE_TEST_CASE_SKIPPED = "icons/cview16/skipped_obj.png"; //$NON-NLS-1$
    public static final String IMG_TRACE_CLASS = "icons/cview16/class_obj.png"; //$NON-NLS-1$
    public static final String IMG_TRACE = "icons/cview16/trace_obj.png"; //$NON-NLS-1$
    
    public static final String IMG_TRACE_TEST_SORT = "icons/clcl16/sort_menu.png"; //$NON-NLS-1$
    public static final String IMG_TRACE_TEST_SORT_PRESSED = "icons/clcl16/sort_p_menu.png"; //$NON-NLS-1$
	
    public static final String IMG_TRACE_TEST_CASE_FILTER_UNDETERMINED = "icons/clcl16/undetermined_filter_menu.png"; //$NON-NLS-1$
    public static final String IMG_TRACE_TEST_CASE_FILTER_UNDETERMINED_PRESSED = "icons/clcl16/undetermined_filter_p_menu.png"; //$NON-NLS-1$
    
    public static final String IMG_TRACE_TEST_CASE_FILTER_SUCCES = "icons/clcl16/ok_filter_menu.png"; //$NON-NLS-1$
    public static final String IMG_TRACE_TEST_CASE_FILTER_SUCCES_PRESSED = "icons/clcl16/ok_filter_p_menu.png"; //$NON-NLS-1$
	/**
	 * The constructor
	 */
	public OvertureTracesPlugin() {
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
	public static OvertureTracesPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
//	public ImageDescriptor getImageDescriptor(String key) {
//        return getImageRegistry().getDescriptor(key);
//	}
//}

}
