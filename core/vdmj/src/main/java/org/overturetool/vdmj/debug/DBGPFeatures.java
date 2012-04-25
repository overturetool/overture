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

package org.overturetool.vdmj.debug;

import java.util.Properties;

import org.overturetool.vdmj.Settings;

@SuppressWarnings("serial")
public class DBGPFeatures extends Properties
{
	public static final String MAX_CHILDREN = "max_children";
	public static final String LANGUAGE_NAME = "language_name";
	public static final String PROTOCOL_VERSION = "protocol_version";
	
	public DBGPFeatures()
	{
		setProperty("lanuage_supports_threads", "1");
		setProperty(LANGUAGE_NAME, Settings.dialect.name());
		setProperty("language_version", "1");
		setProperty("encoding", "UTF-8");
    	setProperty(PROTOCOL_VERSION, "1");
    	setProperty("supports_async", "0");
    	setProperty("data_encoding", "base64");
    	setProperty("breakpoint_languages", "");
    	setProperty("breakpoint_types", "line conditional");
    	setProperty("multiple_sessions", "1");
    	setProperty(MAX_CHILDREN, "10");
    	setProperty("max_data", "1000");
    	setProperty("max_depth", "10");
    	setProperty("supports_postmortem", "0");
    	setProperty("show_hidden", "0");
    	setProperty("notify_ok", "0");
	}
}
