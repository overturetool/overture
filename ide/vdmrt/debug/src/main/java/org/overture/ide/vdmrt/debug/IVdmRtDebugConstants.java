/*
 * #%~
 * org.overture.ide.vdmrt.debug
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
package org.overture.ide.vdmrt.debug;

public interface IVdmRtDebugConstants
{

	final static String ATTR_VDM_PROGRAM = "org.overture.ide.vdmrt.debug.launchConfigurationType";
	static final String PLUGIN_ID = "org.overture.ide.vdmrt.debug";
	
	public final static String VDM_LAUNCH_CONFIG_ENABLE_REALTIME_LOGGING = "vdm_launch_config_enable_realtime_logging";
	public final static boolean VDM_LAUNCH_CONFIG_ENABLE_REALTIME_LOGGING_DEFAULT = false;
	
	static final String VDM_LAUNCH_CONFIG_ENABLE_REALTIME_TIME_INV_CHECKS = "vdm_launch_config_enable_realtime_time_inv_checks";

}
