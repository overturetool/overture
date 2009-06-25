/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
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

package org.overturetool.vdmjc.config;

import org.overturetool.vdmjc.common.ConfigBase;

/**
 * The Config class is used to hold global configuration values. The
 * values are read from the vdmjc.properties file, and defaults are defined
 * as public statics.
 */

public class Config extends ConfigBase
{
	/** The maximum number of listener connections. */
	public static int listener_connection_limit = 100;

	/** The VDMJ jar location. */
	public static String vdmj_jar = "./vdmj-1.0.0.jar";

	/** Extra VDMJ JVM arguments. */
	public static String vdmj_jvm = "";

	/**
	 * When the class is initialized, we call the ConfigBase init method, which
	 * uses the properties file passed to update the static fields above.
	 * @throws Exception
	 */

	public static void init() throws Exception
	{
		init("vdmjc.properties", Config.class);
	}
}
