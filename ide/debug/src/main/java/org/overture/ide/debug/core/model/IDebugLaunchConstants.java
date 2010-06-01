/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.overture.ide.debug.core.model;

import org.eclipse.debug.core.ILaunch;
import org.overture.ide.debug.core.VdmDebugPlugin;

/**
 * DLTK specific {@link org.eclipse.debug.core.ILaunch} attributes.
 */
public class IDebugLaunchConstants {

	public static final String TRUE = "true"; //$NON-NLS-1$
	public static final String FALSE = "false"; //$NON-NLS-1$

	/**
	 * Boolean launch attribute to specify if DBGP console redirection should be
	 * used. Default value is <code>true</code>.
	 */
	public static final String ATTR_DEBUG_CONSOLE = VdmDebugPlugin.PLUGIN_ID
			+ ".debugConsole"; //$NON-NLS-1$

	public static boolean isDebugConsole(ILaunch launch) {
		return getBoolean(launch, ATTR_DEBUG_CONSOLE, true);
	}

	/**
	 * Boolean launch attribute to specify if debugger should stop on the first
	 * line of code.
	 */
	public static final String ATTR_BREAK_ON_FIRST_LINE = VdmDebugPlugin.PLUGIN_ID
			+ ".breakOnFirstLine"; //$NON-NLS-1$

	/**
	 * Returns the 'break on first line' setting for the specified launch.
	 * Default value is <code>false</code>.
	 * 
	 * @param launch
	 * @return <code>true</code> if the option is enabled, <code>false</code>
	 *         otherwise
	 */
	public static boolean isBreakOnFirstLine(ILaunch launch) {
		return getBoolean(launch, ATTR_BREAK_ON_FIRST_LINE, false);
	}

	private static boolean getBoolean(ILaunch launch, String key,
			boolean defaultValue) {
		final String value = launch.getAttribute(key);
		return defaultValue ? !FALSE.equals(value) : TRUE.equals(value);
	}

}
