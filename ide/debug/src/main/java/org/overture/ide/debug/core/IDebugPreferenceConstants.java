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

public class IDebugPreferenceConstants
{

	public static final int DBGP_DEFAULT_PORT = 9000;
	public static final int DBGP_AVAILABLE_PORT = -1;
	public static final int DBGP_DEFAULT_CONNECTION_TIMEOUT = 15000;

	public static final String DBGP_AUTODETECT_BIND_ADDRESS = "autodetect"; //$NON-NLS-1$

	public static final String PREF_DBGP_BREAK_ON_FIRST_LINE = "dbgp_break_on_first_line"; //$NON-NLS-1$

	public static final String PREF_DBGP_ENABLE_LOGGING = "dbgp_enable_logging"; //$NON-NLS-1$

	// Communication
	public static final String PREF_DBGP_BIND_ADDRESS = "dbgp_bind_address"; //$NON-NLS-1$
	public static final String PREF_DBGP_PORT = "dbgp_port"; //$NON-NLS-1$

	public static final String PREF_DBGP_REMOTE_PORT = "remote_dbgp_port"; //$NON-NLS-1$

	// Time to wait of connection debugging engine to the IDE
	public static final String PREF_DBGP_CONNECTION_TIMEOUT = "dbgp_connection_timeout"; //$NON-NLS-1$

	// Time to wait after sending DBGP request to the debugging engine
	public static final String PREF_DBGP_RESPONSE_TIMEOUT = "dbgp_response_timeout"; //$NON-NLS-1$

	public static final String PREF_DBGP_SHOW_SCOPE_PREFIX = "dbgp_show_scope_"; //$NON-NLS-1$

	public static final String PREF_DBGP_SHOW_SCOPE_LOCAL = PREF_DBGP_SHOW_SCOPE_PREFIX
			+ "local"; //$NON-NLS-1$

	public static final String PREF_DBGP_SHOW_SCOPE_GLOBAL = PREF_DBGP_SHOW_SCOPE_PREFIX
			+ "global"; //$NON-NLS-1$

	public static final String PREF_DBGP_SHOW_SCOPE_CLASS = PREF_DBGP_SHOW_SCOPE_PREFIX
			+ "class"; //$NON-NLS-1$
	public static final String PREF_DBGP_ENABLE_EXPERIMENTAL_MODELCHECKER = "PREF_DBGP_ENABLE_EXPERIMENTAL_MODELCHECKER".toLowerCase();
}
