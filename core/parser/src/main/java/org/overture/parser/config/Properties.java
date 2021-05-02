/*******************************************************************************
 *
 *	Copyright (c) 2010 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.parser.config;

import java.io.File;
import java.io.InputStream;
import java.util.Map.Entry;

import org.overture.util.ConfigBase;

/**
 * The Config class is used to hold global configuration values. The values are read from the vdmj.properties file, and
 * defaults are defined as public statics.
 */

public class Properties extends ConfigBase
{
	/** The tab stop for source files. */
	public static int parser_tabstop = 4;
	
	/** Nesting of block comments: 0-3 = support, warning, error, ignore. */
	public static int parser_comment_nesting = 3;

	/** The maximum number of expansions for "+" and "*" trace patterns. */
	public static int traces_max_repeats = 5;

	/** The default duration for RT statements. */
	public static int rt_duration_default = 2;

	/** The default cycle for RT statements. */
	public static int rt_cycle_default = 2;

	/** The default timeslice (statements executed) for a FCFS policy */
	public static int scheduler_fcfs_timeslice = 10;

	/** The vCPU/vBUS timeslice */
	public static int scheduler_virtual_timeslice = 10000;

	/** The timeslice variation (+/- jitter ticks) */
	public static int scheduler_jitter = 0;

	/** Enable transactional variable updates. */
	public static boolean rt_duration_transactions = false;

	/** Enable InstVarChange RT log entries. */
	public static boolean rt_log_instvarchanges = false;

	/** Maximum period thread overlaps allowed per object */
	public static int rt_max_periodic_overlaps = 20;

	/** Enable extra RT log diagnostics for guards etc. */
	public static boolean diags_guards = false;

	/** Enable extra RT log diagnostics for timesteps. */
	public static boolean diags_timestep = false;

	/** The minimum integer used for type bound type bindings **/
	public static int minint = 0;
	/** The maximum integer used for type bound type bindings **/
	public static int maxint = 255;

	/** Enable interpretation of numeric type binds **/
	public static boolean numeric_type_bind_generation = false;

	/**
	 * When the class is initialized, we call the ConfigBase init method, which uses the properties file passed to
	 * update the static fields above.
	 */

	public static void init()
	{
		try
		{
			init("vdmj.properties", Properties.class);
		} catch (Exception e)
		{
			System.err.println(e.getMessage());
		}

		InputStream fis = ConfigBase.class.getResourceAsStream("/"
				+ "overture.properties");

		if (fis != null)
		{
			try
			{
				java.util.Properties overtureProperties = new java.util.Properties();
				overtureProperties.load(fis);

				final String SYSTEM_KEY = "system.";
				for (Entry<Object, Object> entry : overtureProperties.entrySet())
				{
					String key = entry.getKey().toString();
					String value = entry.getValue().toString();

					if (value.indexOf("/") != -1)
					{
						// assume that this is a file path and correct it
						value = value.replace('/', File.separatorChar);
					}

					int index = key.indexOf(SYSTEM_KEY);
					if (index == 0)
					{
						String newKey = key.substring(SYSTEM_KEY.length());
						System.setProperty(newKey, value);
					}
				}
			} catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}
}
