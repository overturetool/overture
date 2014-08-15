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
package org.overture.ide.debug.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.overture.ide.debug.ui.messages"; //$NON-NLS-1$
	public static String DebugConsoleManager_debugConsole;
	/**
	 * @since 2.0
	 */
	public static String DebugConsoleManager_terminated;
	public static String VDMDebugUIPlugin_internalError;
	public static String VdmDebuggerConsoleToFileHyperlink_error;
	public static String VdmDebugModelPresentation_breakpointText;
	public static String VdmDebugModelPresentation_breakpointNoResourceText;
	public static String VdmDebugModelPresentation_breakpointText2;
	public static String VdmDebugModelPresentation_breakpointText3;
	public static String VdmDebugModelPresentation_breakpointText4;
	public static String VdmDebugModelPresentation_breakpointText5;
	public static String VdmDebugModelPresentation_debugTargetText;
	public static String VdmDebugModelPresentation_expressionText;
	public static String VdmDebugModelPresentation_stackFrameText;
	public static String VdmDebugModelPresentation_stackFrameText2;
	public static String VdmDebugModelPresentation_stackFrameText3;
	public static String VdmDebugModelPresentation_threadText;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
