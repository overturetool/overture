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
package org.overture.codegen.utils;

import java.util.List;

import org.overture.codegen.analysis.vdm.Renaming;
import org.overture.codegen.analysis.violations.InvalidNamesResult;

public class GeneratedData
{
	private List<GeneratedModule> classes;
	private List<GeneratedModule> quoteValues;
	private InvalidNamesResult invalidNamesResult;
	private List<String> skippedClasses;
	private List<Renaming> allRenamings;
	private List<String> warnings;

	public GeneratedData()
	{
	}

	public GeneratedData(List<GeneratedModule> classes,
			List<GeneratedModule> quoteValues,
			InvalidNamesResult invalidNamesResult, List<String> skippedClasses)
	{
		super();
		this.classes = classes;
		this.quoteValues = quoteValues;
		this.invalidNamesResult = invalidNamesResult;
		this.skippedClasses = skippedClasses;
	}

	public boolean hasErrors()
	{
		return hasErrors(classes) || hasErrors(quoteValues);
	}

	public List<GeneratedModule> getClasses()
	{
		return classes;
	}

	public void setClasses(List<GeneratedModule> classes)
	{
		this.classes = classes;
	}

	public List<GeneratedModule> getQuoteValues()
	{
		return quoteValues;
	}

	public void setQuoteValues(List<GeneratedModule> quoteValues)
	{
		this.quoteValues = quoteValues;
	}

	public InvalidNamesResult getInvalidNamesResult()
	{
		return invalidNamesResult;
	}

	public void setInvalidNamesResult(InvalidNamesResult invalidNamesResult)
	{
		this.invalidNamesResult = invalidNamesResult;
	}

	public List<String> getSkippedClasses()
	{
		return skippedClasses;
	}

	public void setSkippedClasses(List<String> skippedClasses)
	{
		this.skippedClasses = skippedClasses;
	}

	public List<Renaming> getAllRenamings()
	{
		return allRenamings;
	}

	public void setAllRenamings(List<Renaming> allRenamings)
	{
		this.allRenamings = allRenamings;
	}

	public List<String> getWarnings()
	{
		return warnings;
	}

	public void setWarnings(List<String> warnings)
	{
		this.warnings = warnings;
	}

	private boolean hasErrors(List<GeneratedModule> modules)
	{
		if (modules != null)
		{
			for (GeneratedModule clazz : modules)
			{
				if (clazz.hasErrors())
				{
					return true;
				}
			}
		}
		return false;
	}
}
