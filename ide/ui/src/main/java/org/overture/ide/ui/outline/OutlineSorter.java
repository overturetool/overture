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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.types.PType;
import org.overture.ide.ui.utility.ast.AstNameUtil;

class OutlineSorter extends ViewerSorter
{
	private final static int TYPES = 1;
	private final static int VALUES = 0;

	// private final static int INSTANCEVARIABLES = 2;
	// private final static int OPERATIONS = 3;
	// private final static int FUNCTIONS = 4;
	// private final static int THREADS = 5;
	// private final static int SYN = 6;
	// private final static int TRACES = 7;

	@Override
	public int category(Object element)
	{
		if (element instanceof PType)
		{
			return TYPES;
		} else if (element instanceof PDefinition)
		{
			return VALUES;
		} else
			return super.category(element);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2)
	{
		int cat1 = category(e1);
		int cat2 = category(e2);
		if (cat1 != cat2)
		{
			return cat1 - cat2;
		}

		if (e1 instanceof INode && e2 instanceof INode)
		{
			return collator.compare(AstNameUtil.getName((INode) e1), AstNameUtil.getName(((INode) e2)));
		} else
		{
			return super.compare(viewer, e1, e2);
		}
	}

}