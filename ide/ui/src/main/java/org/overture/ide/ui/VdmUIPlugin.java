/*
 * #%~
 * org.overture.ide.ui
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
