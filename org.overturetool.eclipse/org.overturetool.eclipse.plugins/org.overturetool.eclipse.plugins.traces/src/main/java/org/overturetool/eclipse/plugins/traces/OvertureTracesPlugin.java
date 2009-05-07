package org.overturetool.eclipse.plugins.traces;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class OvertureTracesPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.overturetool.eclipse.plugins.traces";

	// The shared instance
	private static OvertureTracesPlugin plugin;
	
	
	public static final String IMG_ERROR = "icons/Critical16.png";
	
	public static final String IMG_INTERPRETER = "icons/debug16.png";
	
	public static final String IMG_VDM_TOOLS_LOGO = "icons/VDMToolsLogo16.png";
	public static final String IMG_VDM_TOOLS_LOGO_PRESSED = "icons/VDMToolsLogoPressed16.png";
	
	public static final String IMG_VDMJ_LOGO = "icons/OML16.png";
	public static final String IMG_VDMJ_LOGO_PRESSED = "icons/OMLPressed16.png";
	
	public static final String IMG_RUN_SAVE = "icons/save16.png"; 
    public static final String IMG_RUN_ALL_TRACES = "icons/runAll16.png"; //$NON-NLS-1$
    public static final String IMG_RUN_SELECTED_TRACE = "icons/runSelected16.png"; //$NON-NLS-1$

    public static final String IMG_TRACE_TEST_CASE_SUCCES = "icons/succes16.png"; //$NON-NLS-1$
    public static final String IMG_TRACE_TEST_CASE_FAIL = "icons/faild16.png"; //$NON-NLS-1$
    public static final String IMG_TRACE_TEST_CASE_EXPANSIN_FAIL = "icons/faild16.png"; //$NON-NLS-1$
    public static final String IMG_TRACE_TEST_CASE_UNKNOWN = "icons/search16.png"; //$NON-NLS-1$
    public static final String IMG_TRACE_TEST_CASE_UNDETERMINED = "icons/undetermined16.png"; //$NON-NLS-1$
   
    public static final String IMG_TRACE_TEST_CASE_SKIPPED = "icons/skipped16.png"; //$NON-NLS-1$
    public static final String IMG_TRACE_CLASS = "icons/class16.png"; //$NON-NLS-1$
    public static final String IMG_TRACE = "icons/trace16.png"; //$NON-NLS-1$
    
    public static final String IMG_TRACE_TEST_SORT = "icons/sort16.png"; //$NON-NLS-1$
    public static final String IMG_TRACE_TEST_SORT_PRESSED = "icons/sortPressed16.png"; //$NON-NLS-1$
	
    public static final String IMG_TRACE_TEST_CASE_FILTER_UNDETERMINED = "icons/undeterminedFilter16.png"; //$NON-NLS-1$
    public static final String IMG_TRACE_TEST_CASE_FILTER_UNDETERMINED_PRESSED = "icons/undeterminedFilterPressed16.png"; //$NON-NLS-1$
    
    public static final String IMG_TRACE_TEST_CASE_FILTER_SUCCES = "icons/okFilter16.png"; //$NON-NLS-1$
    public static final String IMG_TRACE_TEST_CASE_FILTER_SUCCES_PRESSED = "icons/okFilterPressed16.png"; //$NON-NLS-1$
	/**
	 * The constructor
	 */
	public OvertureTracesPlugin() {
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
