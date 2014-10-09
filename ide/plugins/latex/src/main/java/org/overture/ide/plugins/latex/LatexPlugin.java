/*
 * #%~
 * org.overture.ide.plugins.latex
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
package org.overture.ide.plugins.latex;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class LatexPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "org.overture.ide.plugins.latex";

	// The shared instance
	private static LatexPlugin plugin;

	/**
	 * The constructor
	 */
	public LatexPlugin()
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
	public static LatexPlugin getDefault()
	{
		return plugin;
	}

	public static boolean usePdfLatex()
	{
		String builder = getDefault().getPreferenceStore().getString(ILatexConstants.PDF_BUILDER);
		return builder == null || builder.equals("pdflatex");
	}

	public static boolean useXetex()
	{
		String builder = getDefault().getPreferenceStore().getString(ILatexConstants.PDF_BUILDER);
		return builder != null && builder.equals("xetex");
	}

	
	/** 
	 * Initializes a preference store with default preference values 
	 * for this plug-in.
	 */
	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		
		store.setDefault(ILatexConstants.OSX_LATEX_PATH_PREFERENCE, ILatexConstants.DEFAULT_OSX_LATEX_PATH);
		store.setDefault(ILatexConstants.PDF_BUILDER, ILatexConstants.DEFAULT_PDF_BUILDER);
	}
}
