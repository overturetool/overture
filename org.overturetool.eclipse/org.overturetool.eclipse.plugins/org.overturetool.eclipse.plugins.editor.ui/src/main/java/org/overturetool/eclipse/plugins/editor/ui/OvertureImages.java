/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.ui;

import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.ui.PluginImagesHelper;
import org.eclipse.jface.resource.ImageDescriptor;
import org.overturetool.eclipse.plugins.editor.internal.ui.UIPlugin;

public class OvertureImages {
	private static final PluginImagesHelper helper = new PluginImagesHelper(UIPlugin.getDefault()
			.getBundle(), new Path("/icons/"));

	public static final ImageDescriptor DESC_WIZBAN_PROJECT_CREATION = helper
			.createUnManaged(PluginImagesHelper.T_WIZBAN, "newjscriptfile_wiz.gif");
}
