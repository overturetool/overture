/*
 * #%~
 * VDM Code Generator
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
package org.overture.codegen.vdm2java;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.utils.GeneralCodeGenUtils;

public class JavaSettings
{
	private boolean disableCloning;
	private List<String> classesToSkip;
	private String vdmEntryExp;
	private String javaRootPackage;
	
	public JavaSettings()
	{
		this.disableCloning = false;
		this.classesToSkip = new LinkedList<String>();
		this.vdmEntryExp = null;
		this.javaRootPackage = null;
	}
	
	public List<String> getClassesToSkip()
	{
		return classesToSkip;
	}

	public void setClassesToSkip(List<String> classesToSkip)
	{
		if (classesToSkip != null)
		{
			this.classesToSkip = classesToSkip;
		}
	}

	public boolean getDisableCloning()
	{
		return disableCloning;
	}

	public void setDisableCloning(boolean disableCloning)
	{
		this.disableCloning = disableCloning;
	}

	public String getVdmEntryExp()
	{
		return vdmEntryExp;
	}

	public void setVdmEntryExp(String vdmLaunchConfigEntryExp)
	{
		this.vdmEntryExp = vdmLaunchConfigEntryExp;
	}

	public String getJavaRootPackage()
	{
		return javaRootPackage;
	}

	public void setJavaRootPackage(String javaRootPackage)
	{
		if (GeneralCodeGenUtils.isValidJavaPackage(javaRootPackage))
		{
			this.javaRootPackage = javaRootPackage;
		}
	}
}
