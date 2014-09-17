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

import org.overture.codegen.analysis.violations.InvalidNamesResult;

public class GeneratedData
{
	private List<GeneratedModule> classes;
	private GeneratedModule quoteValues;
	private InvalidNamesResult invalidNamesResult;

	public GeneratedData(List<GeneratedModule> classes,
			GeneratedModule quoteValues, InvalidNamesResult invalidNamesResult)
	{
		super();
		this.classes = classes;
		this.quoteValues = quoteValues;
		this.invalidNamesResult = invalidNamesResult;
	}

	public List<GeneratedModule> getClasses()
	{
		return classes;
	}

	public GeneratedModule getQuoteValues()
	{
		return quoteValues;
	}

	public InvalidNamesResult getInvalidNamesResult()
	{
		return invalidNamesResult;
	}
}
