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

package org.overturetool.vdmtools.dbgp;

public enum DBGPCommandType
{
	STATUS("status"),
	FEATURE_GET("feature_get"),
	FEATURE_SET("feature_set"),
	RUN("run"),
	EVAL("eval"),
	STEP_INTO("step_into"),
	STEP_OVER("step_over"),
	STEP_OUT("step_out"),
	STOP("stop"),
	DETACH("detach"),
	BREAKPOINT_GET("breakpoint_get"),
	BREAKPOINT_SET("breakpoint_set"),
	BREAKPOINT_UPDATE("breakpoint_update"),
	BREAKPOINT_REMOVE("breakpoint_remove"),
	BREAKPOINT_LIST("breakpoint_list"),
	STACK_DEPTH("stack_depth"),
	STACK_GET("stack_get"),
	CONTEXT_NAMES("context_names"),
	CONTEXT_GET("context_get"),
	PROPERTY_GET("property_get"),
	PROPERTY_SET("property_set"),
	SOURCE("source"),
	STDOUT("stdout"),
	STDIN("stdin"),
	STDERR("stderr"),
	UNKNOWN("?");

	public String value;

	DBGPCommandType(String value)
	{
		this.value = value;
	}

	public static DBGPCommandType lookup(String string) throws DBGPException
	{
		for (DBGPCommandType cmd: values())
		{
			if (cmd.value.equals(string))
			{
				return cmd;
			}
		}

		throw new DBGPException(DBGPErrorCode.PARSE, string);
	}

	@Override
	public String toString()
	{
		return value;
	}
}
