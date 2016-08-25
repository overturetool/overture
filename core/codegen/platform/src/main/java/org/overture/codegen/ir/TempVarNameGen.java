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
package org.overture.codegen.ir;

import java.util.HashMap;

public class TempVarNameGen implements ITempVarGen
{
	private static final int START_VALUE = 1;

	private HashMap<String, Integer> counters;

	public TempVarNameGen()
	{
		super();
		this.counters = new HashMap<String, Integer>();
	}

	@Override
	public void clear()
	{
		counters.clear();
	}

	@Override
	public String nextVarName(String prefix)
	{
		int count = counters.containsKey(prefix) ? 1 + counters.get(prefix)
				: START_VALUE;

		counters.put(prefix, count);

		return prefix + count;
	}
}
