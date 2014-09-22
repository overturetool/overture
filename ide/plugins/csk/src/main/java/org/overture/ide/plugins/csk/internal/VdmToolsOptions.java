/*
 * #%~
 * org.overture.ide.plugins.csk
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
package org.overture.ide.plugins.csk.internal;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.overture.ide.ui.utility.PluginFolderInclude;

public class VdmToolsOptions
{
	public Integer FormatVersion = 2;
	public Boolean DTC = true;
	public Boolean PRE = true;
	public Boolean POST = true;
	public Boolean INV = true;
	public Boolean CONTEXT = false;
	public Integer MAXINSTR = 1000;
	public Integer PRIORITY = 0;
	public String PRIMARYALGORITHM = "instruction_number_slice";
	public Boolean TASKSWITCH = false;
	public Integer MAXTIME = 1000;
	public Integer TIMEFACTOR = 1;
	public Integer STEPSIZE = 100;
	public String JITTERMODE = "Early";
	public Integer DEFAULTCPUCAPACITY = 1000000;
	public String DEFAULTVCPUCAPACITY = "INFINITE";
	public String LOGARGS = "";
	public Integer PRINT_FORMAT = 1;
	public String DEF = "pos";
	public Integer errlevel = 1;
	public Integer SEP = 1;
	public Integer VDMSLMOD = 0;
	public Integer INDEX = 0;
	public Boolean PrettyPrint_RTI = false;
	public Boolean CG_RTI = false;
	public Boolean CG_CHECKPREPOST = true;
	public Integer C_flag = 0;
	public Integer JCG_SKEL = 0;
	public Boolean JCG_GENPREPOST = false;
	public Boolean JCG_TYPES = false;
	public Boolean JCG_SMALLTYPES = false;
	public Boolean JCG_LONGS = true;
	public String JCG_PACKAGE = "";
	public Boolean JCG_CONCUR = false;
	public Boolean JCG_CHECKPREPOST = false;
	public Boolean JCG_VDMPREFIX = true;
	public String JCG_INTERFACES = "";
	public Integer Seed_nondetstmt = -1;
	public Boolean j2v_stubsOnly = false;
	public Boolean j2v_transforms = false;

	public void Save(File location, String projectName) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			sb.append(getEntry());
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		
		
		PluginFolderInclude.writeFile(location,projectName+".opt", sb.toString());
	}

	private String getEntry(/*Object... entries*/) throws IllegalArgumentException, IllegalAccessException
	{
		StringBuilder data = new StringBuilder();
//		List<Object> fooList = Arrays.asList(entries);
		for (Field field : this.getClass().getFields())
		{
//			if (fooList.contains(field.get(this)))
//			{
				data.append(field.getName()+":"+getValue(field.get(this))+"\n");
//			}
		}
		if(data.length()>1)
		{
			data.deleteCharAt(data.length()-1);	
		}
		return data.toString();
		
	}

	private String getValue(Object object)
	{
		if(object instanceof Boolean)
		{
			if((Boolean)object)
			{
				return "1";
			}else
			{
				return "0";
			}
		}else
		{
			return object.toString();
		}
	}
}
