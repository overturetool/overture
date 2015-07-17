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
package org.overture.codegen.trans;

public class TempVarPrefixes
{
	public String set()
	{
		return "set_";
	}

	public final String iterator()
	{
		return "iterator_";
	}

	public String success()
	{
		return "success_";
	}

	public String forIndexToVar()
	{
		return "toVar_";
	}

	public String forIndexByVar()
	{
		return "byVar_";
	}

	public final String[] GENERATED_TEMP_NAMES = {
			forIndexByVar(), forIndexToVar(),
			iterator(), set(),
			success() };
}
