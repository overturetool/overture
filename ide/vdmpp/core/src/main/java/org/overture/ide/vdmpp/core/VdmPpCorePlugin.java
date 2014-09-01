/*
 * #%~
 * org.overture.ide.vdmpp.core
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
package org.overture.ide.vdmpp.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;


public class VdmPpCorePlugin extends Plugin
{
	// The plug-in ID
	public static final String PLUGIN_ID = IVdmPpCoreConstants.PLUGIN_ID;//"org.overture.core";

	public static final boolean DEBUG = true;
	
	
	// The shared instance
	private static VdmPpCorePlugin plugin;
	
	/**
	 * The constructor
	 */
	public VdmPpCorePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
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
	public static VdmPpCorePlugin getDefault() {
		return plugin;
	}

	public static void log(Exception ex) {
		if (DEBUG){
			ex.printStackTrace();
		}
		String message = ex.getMessage();
		if (message == null){		
			message = "(no message)"; //$NON-NLS-1$
		}
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, 0, message, ex));
	}
	
	@SuppressWarnings("unused")
	private final ListenerList shutdownListeners = new ListenerList();

	
}
