package org.overture.ide.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.overture.ide.core.Activator;
import org.overture.ide.ui.editor.partitioning.VdmPartitionScanner;

public class VdmUIPlugin extends AbstractUIPlugin {
	public static boolean DEBUG = true;
	private static VdmUIPlugin plugin;
	public final static String VDM_PARTITIONING = "__vdm__partitioning__";
	private VdmPartitionScanner fPartitionScanner;
	
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
	public static VdmUIPlugin getDefault() {
		return plugin;
	}
	
	public VdmPartitionScanner getPartitionScanner(){
		if(fPartitionScanner == null)
			fPartitionScanner = new VdmPartitionScanner();
		
		return fPartitionScanner;
	}
	
	
	public static void log(Exception exception) {
		exception.printStackTrace();
	}
	
	public static void println(String s)
	{
		if(DEBUG)
			System.out.println(s);
	}
	public static void printe(Exception e) {
		println(e.getStackTrace().toString());
		println(e.getMessage());
		
	}
	
	
}
