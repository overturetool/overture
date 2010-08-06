package org.overture.ide.ui;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.overture.ide.ui.editor.partitioning.VdmPartitionScanner;
import org.overture.ide.ui.internal.viewsupport.ImageDescriptorRegistry;
import org.overture.ide.ui.internal.viewsupport.ProblemMarkerManager;

public class VdmUIPlugin extends AbstractUIPlugin {
	public static boolean DEBUG = true;
	private static VdmUIPlugin plugin;
	public final static String VDM_PARTITIONING = "__vdm__partitioning__";

	// public static final String PLUGIN_ID = "org.overture.ide.ui";
	private VdmPartitionScanner fPartitionScanner;
	private ImageDescriptorRegistry fImageDescriptorRegistry;
	private ProblemMarkerManager fProblemMarkerManager;
//	private Object fTemplateStore;
//	private Object fContextTypeRegistry;

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
	public static VdmUIPlugin getDefault() {
		return plugin;
	}

	public VdmPartitionScanner getPartitionScanner() {
		if (fPartitionScanner == null)
			fPartitionScanner = new VdmPartitionScanner();

		return fPartitionScanner;
	}

	public static void log(Exception exception) {
		getDefault().getLog().log(new Status(IStatus.ERROR,IVdmUiConstants.PLUGIN_ID,"VdmUIPlugin",exception));
	}
	public static void log(String message,Exception exception) {
		getDefault().getLog().log(new Status(IStatus.ERROR,IVdmUiConstants.PLUGIN_ID,message,exception));
	}

	public static void println(String s) {
		if (DEBUG)
			System.out.println(s);
	}

	public static void printe(Exception e) {
		println(e.getStackTrace().toString());
		println(e.getMessage());

	}

	public static void logErrorMessage(String message) {
		getDefault().getLog().log(new Status(IStatus.ERROR,IVdmUiConstants.PLUGIN_ID,message));

	}

	public static ImageDescriptorRegistry getImageDescriptorRegistry() {
		return getDefault().internalGetImageDescriptorRegistry();
	}

	private synchronized ImageDescriptorRegistry internalGetImageDescriptorRegistry() {
		if (fImageDescriptorRegistry == null)
			fImageDescriptorRegistry = new ImageDescriptorRegistry();
		return fImageDescriptorRegistry;
	}

	public synchronized ProblemMarkerManager getProblemMarkerManager() {
		if (fProblemMarkerManager == null)
			fProblemMarkerManager = new ProblemMarkerManager();
		return fProblemMarkerManager;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	
}
