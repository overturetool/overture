/*
 * #%~
 * Combinatorial Testing
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
package org.overture.ide.plugins.combinatorialtesting.views.treeView;

public class GroupSizeCalculator
{
	private Long size;
	private Double numberOfGroups;
	private Long testCountInGroup;

	public GroupSizeCalculator(Long size)
	{
		this.size = size;

		if (hasGroups())
		{
			numberOfGroups = Math.ceil(size.doubleValue()
					/ TraceTestGroup.GROUP_SIZE);

			if (numberOfGroups > TraceTestGroup.GROUP_SIZE)
			{
				numberOfGroups = TraceTestGroup.GROUP_SIZE.doubleValue();
			}

			testCountInGroup = size / numberOfGroups.longValue();

			if (testCountInGroup < TraceTestGroup.GROUP_SIZE
					&& size >= TraceTestGroup.GROUP_SIZE)
			{
				testCountInGroup = TraceTestGroup.GROUP_SIZE; // top up all
																// groups
			}
		}
	}

	public boolean hasGroups()
	{
		return size > TraceTestGroup.GROUP_SIZE;
	}

	public Double getNumberOfGroups()
	{
		return numberOfGroups;
	}

	public Long getGroupSize()
	{
		return testCountInGroup;

	}

	public Long getTotalSize()
	{
		return size;
	}

}
