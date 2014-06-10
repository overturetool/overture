/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.ui.outline;

import org.eclipse.jface.viewers.ViewerSorter;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.PType;

class OutlineSorter extends ViewerSorter
{
	private final static int TYPES = 0;
	private final static int DEFINITIONS = 1;
	private final static int OTHER = 2;

	@Override
	public int category(Object element)
	{
		if (element instanceof PType)
		{
			return TYPES;
		} else if (element instanceof PDefinition)
		{
			return DEFINITIONS;
		} else
			return OTHER;
	}

}