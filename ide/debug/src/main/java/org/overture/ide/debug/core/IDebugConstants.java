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

import org.overture.ide.builders.vdmj.IBuilderVdmjConstants;
import org.overture.ide.core.ICoreConstants;

public interface IDebugConstants
{
	final static String VDMPP_DEBUGGING_ENGINE_ID_KEY = "vdmpp_debugging_engine_id";
	final static String PLUGIN_ID = "org.overture.ide.debug";
	final static String EXTENSION_SOURCEVIEWER_EDITOR = "org.overture.ide.debug.sourceviewerEditor";

	// used in the launch configuration
	final static String VDM_LAUNCH_CONFIG_PROJECT = "vdm_launch_config_project";
	final static String VDM_LAUNCH_CONFIG_MODULE = "vdm_launch_config_module";
	final static String VDM_LAUNCH_CONFIG_OPERATION = "vdm_launch_config_method";
	final static String VDM_LAUNCH_CONFIG_STATIC_OPERATION = "vdm_launch_config_static_method";
	final static String VDM_LAUNCH_CONFIG_EXPRESSION = "vdm_launch_config_expression";

	final static String VDM_LAUNCH_CONFIG_DEFAULT = "vdm_launch_config_default";

	final static String VDM_LAUNCH_CONFIG_IS_TRACE = "vdm_launch_config_is_trace";

	// This is used by external tools to force listening on a specific port
	final String VDM_LAUNCH_CONFIG_OVERRIDE_PORT = "vdm_launch_config_override_port";

	final static String VDM_LAUNCH_CONFIG_REMOTE_CONTROL = "vdm_launch_config_remote_control_class";
	final static String VDM_LAUNCH_CONFIG_CREATE_COVERAGE = "vdm_launch_config_create_coverage";
	final static String VDM_LAUNCH_CONFIG_REMOTE_DEBUG = "vdm_launch_config_remote_debug";
	final static String VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION = "vdm_launch_config_memory_option";
	final static String VDM_LAUNCH_CONFIG_ENABLE_LOGGING = "vdm_launch_config_enable_logging";
	final static String VDM_LAUNCH_CONFIG_SHOW_VM_SETTINGS = "VDM_LAUNCH_CONFIG_SHOW_VM_SETTINGS".toLowerCase();

	static final String VDM_LAUNCH_CONFIG_DTC_CHECKS = "vdm_launch_config_dtc_checks";
	static final String VDM_LAUNCH_CONFIG_INV_CHECKS = "vdm_launch_config_inv_checks";
	static final String VDM_LAUNCH_CONFIG_POST_CHECKS = "vdm_launch_config_post_checks";
	static final String VDM_LAUNCH_CONFIG_PRE_CHECKS = "vdm_launch_config_pre_checks";
	static final String VDM_LAUNCH_CONFIG_MEASURE_CHECKS = "vdm_launch_config_measure_checks";

	static final String VDM_LAUNCH_CONFIG_CONSOLE_ENTRY = "vdm_launch_config_console_entry";

	static final String VDM_LAUNCH_CONFIG_CUSTOM_DEBUGGER_PROPERTIES = "VDM_LAUNCH_CONFIG_CUSTOM_DEBUGGER_PROPERTIES".toLowerCase();

	public static final String ID_VDM_DEBUG_MODEL = "org.overture.ide.debug.vdm";
	public static final String ATTR_VDM_PROGRAM = ID_VDM_DEBUG_MODEL
			+ ".ATTR_VDM_PROGRAM";
	public static final String ID_VDM_EXECUTABLE = "java";

	public static final String BREAKPOINT_MARKER_ID = "org.eclipse.debug.core.lineBreakpointMarker";

	// debugger
	public static final String[] DEBUG_ENGINE_BUNDLE_IDS = { PLUGIN_ID,
			ICoreConstants.PLUGIN_ID, IBuilderVdmjConstants.PLUGIN_ID };
	public static final String DEBUG_ENGINE_CLASS = "org.overture.interpreter.debug.DBGPReaderV2";
	static final String EXTENSION_POINT_VDM_BREAKPOINT_LISTENERS = "breakpointListeners";

	/**
	 * Status code indicating an unexpected error.
	 */
	public static final int ERROR = 120;

	public static final String CONSOLE_DEBUG_NAME = "Overture Debug";
	public static final String CONSOLE_LOGGING_NAME = "VDM Debug log";
	static final String LogViewId = "org.overture.ide.debug.logging.logview";
	static final String PREF_DBGP_RESPONSE_TIMEOUT = "dbgp_connection_timeout";

	public static final String FILE_SCHEME = "file"; //$NON-NLS-1$
	public static final String DBGP_SCHEME = "dbgp"; //$NON-NLS-1$
	public static final String UNKNOWN_SCHEME = "unknown"; //$NON-NLS-1$
	static final int ERR_DEBUGGER_PROCESS_TERMINATED = 121;
	static final String CONSOLE_INTERACTIVE_WELCOME_MESSAGE = "**\n** Welcome to the Overture Interactive Console\n**\n> ";
	static final String CONSOLE_MESSAGE = "**\n** Overture Console\n**\n";

	// from marker factory
	public static final String LINE_BREAKPOINT_MARKER_ID = "vdm.lineBreakpoint.marker"; //$NON-NLS-1$
	public static final String METHOD_ENTRY_MARKER_ID = "org.overture.ide.debug.vdmMethodEntryBreakpointMarker"; //$NON-NLS-1$
	public static final String WATCHPOINT_MARKER_ID = "org.overture.ide.debug.vdmWatchPointMarker"; //$NON-NLS-1$

}
