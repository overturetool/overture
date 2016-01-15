/*
 * #%~
 * Code Generator Plugin
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
package org.overture.ide.plugins.codegen;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

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
	 * Initializes a preference store with default preference values for this plug-in.
	 */
	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store)
	{
		store.setDefault(ICodeGenConstants.GENERATE_CHAR_SEQUENCES_AS_STRINGS, ICodeGenConstants.GENERATE_CHAR_SEQUENCES_AS_STRING_DEFAULT);
		store.setDefault(ICodeGenConstants.DISABLE_CLONING, ICodeGenConstants.DISABLE_CLONING_DEFAULT);
		store.setDefault(ICodeGenConstants.GENERATE_CONCURRENCY_MECHANISMS, ICodeGenConstants.GENERATE_CONCURRENCY_MECHANISMS_DEFAULT);
		store.setDefault(ICodeGenConstants.CLASSES_TO_SKIP, ICodeGenConstants.CLASSES_TO_SKIP_DEFAULT);
		store.setDefault(ICodeGenConstants.JAVA_PACKAGE, ICodeGenConstants.JAVA_PACKAGE_DEFAULT);
		store.setDefault(ICodeGenConstants.GENERATE_JML, ICodeGenConstants.GENERATE_JML_DEFAULT);
		store.setDefault(ICodeGenConstants.JML_USE_INVARIANT_FOR, ICodeGenConstants.JML_USE_INVARIANT_FOR_DEFAULT);
	}
	
	public static void savePluginSettings(boolean disableCloning,
			boolean genAsStrings, boolean genConc, boolean genJml, boolean jmlInvFor, String userSpecifiedClassesToSkip, String javaPackage)
	{
		Preferences prefs = InstanceScope.INSTANCE.getNode(ICodeGenConstants.PLUGIN_ID);
		prefs.put(ICodeGenConstants.DISABLE_CLONING, new Boolean(disableCloning).toString());
		prefs.put(ICodeGenConstants.GENERATE_CHAR_SEQUENCES_AS_STRINGS, new Boolean(genAsStrings).toString());
		prefs.put(ICodeGenConstants.GENERATE_CONCURRENCY_MECHANISMS, new Boolean(genConc).toString());
		
		if (userSpecifiedClassesToSkip != null)
		{
			prefs.put(ICodeGenConstants.CLASSES_TO_SKIP, userSpecifiedClassesToSkip);
		}
		
		if (javaPackage != null)
		{
			prefs.put(ICodeGenConstants.JAVA_PACKAGE, javaPackage);
		}
		
		prefs.put(ICodeGenConstants.GENERATE_JML, new Boolean(genJml).toString());
		prefs.put(ICodeGenConstants.JML_USE_INVARIANT_FOR, new Boolean(jmlInvFor).toString());

		try
		{
			prefs.flush();
		} catch (BackingStoreException e)
		{
			e.printStackTrace();
		}
	}
}
