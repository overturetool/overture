/*
 * #%~
 * The VDM Type Checker
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

package org.overture.typechecker;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;

public class ExcludedDefinitions
{
	private static List<PDefinition> excludedDefinitions = new Vector<PDefinition>();
	
	public static void setExcluded(List<PDefinition> excluded)
	{
		excludedDefinitions.clear();
		excludedDefinitions.addAll(excluded);
	}
	
	public static void setExcluded(PDefinition excluded)
	{
		excludedDefinitions.clear();
		excludedDefinitions.add(excluded);
	}
	
	public static void clearExcluded()
	{
		excludedDefinitions.clear();
	}
	
	public static boolean isExcluded(PDefinition def)
	{
		for (PDefinition d: excludedDefinitions)
		{
			if (d == def)	// Note: identical references
			{
				return true;
			}
		}
		
		return false;
	}
}
