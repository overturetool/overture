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
package org.overture.ide.debug.ui.log;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.overture.ide.debug.ui.log.messages"; //$NON-NLS-1$
	public static String VdmDebugLogView_clear;
	public static String VdmDebugLogView_copy;
	public static String EventKind_Change;
	public static String EventKind_Create;
	public static String EventKind_ModelSpecific;
	public static String EventKind_Resume;
	public static String EventKind_Suspend;
	public static String EventKind_Terminate;
	public static String EventKind_Unknown;
	public static String ItemType_Input;
	public static String ItemType_Output;
	public static String ItemType_Event;
	public static String Column_Date;
	public static String Column_Time;
	public static String Column_Type;
	public static String Column_Session;
	public static String Column_Message;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
