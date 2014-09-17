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
package org.overture.ide.plugins.combinatorialtesting.views.internal;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.TraceTestTreeNode;

/**
 * Trace node sorter that sorts based on the number and not the label string
 * 
 * @author kel
 */
public class TraceNodeSorter extends ViewerSorter
{
	@Override
	public int compare(Viewer viewer, Object e1, Object e2)
	{

		if (e1 instanceof TraceTestTreeNode && e2 instanceof TraceTestTreeNode)
		{
			return ((TraceTestTreeNode) e1).getNumber().compareTo(((TraceTestTreeNode) e2).getNumber());
		}

		return super.compare(viewer, e1, e2);
	}

}
