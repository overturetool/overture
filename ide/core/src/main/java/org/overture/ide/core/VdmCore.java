/*
 * #%~
 * org.overture.ide.core
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
package org.overture.ide.core;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.BundleContext;
import org.overture.ide.internal.core.DeltaProcessingState;
import org.overture.ide.internal.core.DeltaProcessor;
import org.overture.ide.internal.core.ResourceManager;
import org.overture.ide.internal.core.ast.VdmModelManager;


public class VdmCore extends Plugin
{
	// The plug-in ID
	public static final String PLUGIN_ID = ICoreConstants.PLUGIN_ID;

	// The shared instance
	private static VdmCore plugin;

	public static boolean DEBUG= true;

	static DeltaProcessor deltaProcessor = new DeltaProcessor(new DeltaProcessingState(), VdmModelManager.getInstance());
	public static DeltaProcessor getDeltaProcessor()
	{
		return deltaProcessor;
	}
	
	public static void addElementChangedListener(IElementChangedListener listener) {
		getDeltaProcessor().getState().addElementChangedListener(listener);
	}
	
	public static void removeElementChangedListener(IElementChangedListener listener) {
		getDeltaProcessor().getState().removeElementChangedListener(listener);
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(ResourceManager.getInstance());
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
	public static VdmCore getDefault() {
		return plugin;
	}

	public static void log(Throwable exception)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, "VdmCore", exception));
	}

	public static void log(String message, Throwable exception)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, exception));
	}
}
