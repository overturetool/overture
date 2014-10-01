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

import org.overture.ide.plugins.combinatorialtesting.views.treeView.TraceTestTreeNode;
import org.overture.interpreter.traces.Verdict;

public class TraceViewerSorter extends TraceNodeSorter
{
	@Override
	public int category(Object element)
	{
		if (element instanceof TraceTestTreeNode)
		{
			Verdict res = ((TraceTestTreeNode) element).getStatus();
			if (res == Verdict.FAILED)
			{
				return 1;
			} else if (res == Verdict.INCONCLUSIVE)
			{
				return 2;
			} else if (res == Verdict.PASSED)
			{
				return 3;
			}
		}
		return 3;
	}
}
