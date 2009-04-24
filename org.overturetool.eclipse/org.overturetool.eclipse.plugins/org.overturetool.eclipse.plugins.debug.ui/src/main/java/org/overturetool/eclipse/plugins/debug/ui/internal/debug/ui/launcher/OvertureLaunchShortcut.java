/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.htmls
 ******************************************************************************/
package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.launcher;

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.dltk.internal.debug.ui.launcher.AbstractScriptLaunchShortcut;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;
import org.overturetool.eclipse.plugins.launching.OvertureLaunchConstants;

public class OvertureLaunchShortcut extends AbstractScriptLaunchShortcut {
	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType(
				OvertureLaunchConstants.ID_OVERTURE_SCRIPT);
	}

	protected String getNatureId() {
		return OvertureNature.NATURE_ID;
	}
}
