/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.overture.ide.debug.core.model.ISourceOffsetLookup;
import org.overture.ide.debug.core.model.internal.DbgpService;
import org.overture.ide.debug.core.model.internal.VdmDebugTarget;
import org.overture.ide.debug.ui.DebugConsoleManager;
import org.overture.ide.debug.ui.VdmEvaluationContextManager;
import org.overture.ide.debug.ui.log.VdmDebugLogManager;

@SuppressWarnings("deprecation")
public class VdmDebugPlugin extends AbstractUIPlugin
{

	private static VdmDebugPlugin plugin;
	protected Map<RGB, Color> fColorTable = new HashMap<RGB, Color>(10);
	public static final int INTERNAL_ERROR = 120;

	public static final String PLUGIN_ID = "org.overture.ide.debug.core";

	public static boolean DEBUG = true;

	//	private static final String LOCALHOST = "127.0.0.1"; //$NON-NLS-1$

	public static IWorkbenchPage getActivePage()
	{
		IWorkbenchWindow w = getActiveWorkbenchWindow();
		if (w != null)
		{
			return w.getActivePage();
		}
		return null;
	}

	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		launchManager.addLaunchListener(DebugConsoleManager.getInstance());
		launchManager.addLaunchListener(VdmDebugLogManager.getInstance());
		plugin = this;

		// TODO
		VdmEvaluationContextManager.startup();
		// HotCodeReplaceManager.getDefault().startup();
	}

	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		// HotCodeReplaceManager.getDefault().shutdown();
		super.stop(context);
		if (dbgpService != null)
		{
			dbgpService.shutdown();
		}

		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

		IDebugTarget[] targets = launchManager.getDebugTargets();
		for (int i = 0; i < targets.length; i++)
		{
			IDebugTarget target = targets[i];
			if (target instanceof VdmDebugTarget)
			{
				((VdmDebugTarget) target).shutdown();
			}
		}
	}

	private DbgpService dbgpService;

	public synchronized IDbgpService getDbgpService()
	{

		if (dbgpService == null)
		{
			dbgpService = new DbgpService(getPreferencePort());
			getPluginPreferences().addPropertyChangeListener(new DbgpServicePreferenceUpdater());

		}

		return dbgpService;

	}

	/**
	 * Inefficient use getDbgpService
	 * 
	 * @param freePort
	 * @return
	 */
	public synchronized IDbgpService getDbgpService(int freePort)
	{

		// if (dbgpService == null) {
		dbgpService = new DbgpService(freePort);
		getPluginPreferences().addPropertyChangeListener(new DbgpServicePreferenceUpdater());

		// }

		return dbgpService;

	}

	private class DbgpServicePreferenceUpdater implements

	IPropertyChangeListener
	{

		public void propertyChange(PropertyChangeEvent event)
		{

			final String property = event.getProperty();

			if (IDebugPreferenceConstants.PREF_DBGP_PORT.equals(property))
			{

				if (dbgpService != null)
				{

					dbgpService.restart(getPreferencePort());

				}

			}

		}

	}

	// Logging

	public static void log(Throwable t)
	{
		Throwable top = t;
		if (t instanceof DebugException)
		{
			Throwable throwable = ((DebugException) t).getStatus().getException();
			if (throwable != null)
			{
				top = throwable;
			}
		}
		log(new Status(IStatus.ERROR, PLUGIN_ID, INTERNAL_ERROR, "internalErrorLoggedFromVdmDebugPlugin"
				+ top.getMessage(), top));
	}

	public static void log(IStatus status)
	{
		getDefault().getLog().log(status);
	}

	public static void logWarning(String message)
	{
		logWarning(message, null);
	}

	public static void logWarning(String message, Throwable t)
	{
		log(new Status(IStatus.WARNING, PLUGIN_ID, INTERNAL_ERROR, message, t));
	}

	public static void logError(String message)
	{
		logError(message, null);
	}

	public static void logError(String message, Throwable t)
	{
		Throwable top = t;
		if (t instanceof DebugException)
		{
			Throwable throwable = ((DebugException) t).getStatus().getException();
			if (throwable != null)
			{
				top = throwable;
			}
		}
		log(new Status(IStatus.ERROR, PLUGIN_ID, INTERNAL_ERROR, message, top));
	}

	private int getPreferencePort()
	{

		int p = getPreferenceStore().getInt(IDebugPreferenceConstants.PREF_DBGP_PORT);
		if (p == 0)
		{
			initializeDefaultDebugPreferences(getPreferenceStore());
		}
		return getPreferenceStore().getInt(IDebugPreferenceConstants.PREF_DBGP_PORT);
	}

	public int getConnectionTimeout()
	{
		int t = getDefault().getPluginPreferences().getInt(IDebugPreferenceConstants.PREF_DBGP_CONNECTION_TIMEOUT);
		if (t == 0)
		{
			initializeDefaultDebugPreferences(getPreferenceStore());
		}
		return getDefault().getPluginPreferences().getInt(IDebugPreferenceConstants.PREF_DBGP_CONNECTION_TIMEOUT);
	}

	private static ISourceOffsetLookup sourceOffsetLookup = null;

	public static ISourceOffsetLookup getSourceOffsetLookup()
	{
		return sourceOffsetLookup;
	}

	public static void setSourceOffsetRetriever(ISourceOffsetLookup offsetLookup)
	{
		sourceOffsetLookup = offsetLookup;
	}

	private boolean fTrace = false;

	public boolean isTraceMode()
	{
		return fTrace;
	}

	public static void logTraceMessage(String message)
	{
		if (getDefault().isTraceMode())
		{
			IStatus s = new Status(IStatus.WARNING, IDebugConstants.PLUGIN_ID, INTERNAL_ERROR, message, null);
			getDefault().getLog().log(s);
		}
	}

	public static VdmDebugPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Returns the active workbench window
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow()
	{
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	/**
	 * Returns the active workbench shell or <code>null</code> if none
	 * 
	 * @return the active workbench shell or <code>null</code> if none
	 */
	public static Shell getActiveWorkbenchShell()
	{
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null)
		{
			return window.getShell();
		}
		return null;
	}

	public static void logWarning(Exception e)
	{
		log(e);

	}

	public Color getColor(RGB rgb)
	{
		Color color = fColorTable.get(rgb);
		if (color == null)
		{
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}

	/**
	 * Returns the standard display to be used. The method first checks, if the thread calling this method has an
	 * associated display. If so, this display is returned. Otherwise the method returns the default display.
	 */
	public static Display getStandardDisplay()
	{
		Display display;
		display = Display.getCurrent();
		if (display == null)
		{
			display = Display.getDefault();
		}
		return display;
	}

	/**
	 * Initializes a preference store with default preference values for this plug-in.
	 */
	@Override
	protected void initializeDefaultPluginPreferences()
	{
		IPreferenceStore store = getPreferenceStore();
		initializeDefaultDebugPreferences(store);
	}

	public static void initializeDefaultDebugPreferences(IPreferenceStore store)
	{
		store.setDefault(IDebugPreferenceConstants.PREF_DBGP_PORT, IDebugPreferenceConstants.DBGP_AVAILABLE_PORT);
		store.setDefault(IDebugPreferenceConstants.PREF_DBGP_CONNECTION_TIMEOUT, IDebugPreferenceConstants.DBGP_DEFAULT_CONNECTION_TIMEOUT);
		store.setDefault(IDebugPreferenceConstants.PREF_DBGP_ENABLE_EXPERIMENTAL_MODELCHECKER, false);
	}

}
