package org.overture.ide.plugins.combinatorialtesting.views.internal;

import org.eclipse.jface.viewers.ViewerSorter;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.TraceTestTreeNode;
import org.overture.interpreter.traces.Verdict;

public class TraceViewerSorter extends ViewerSorter
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
		// return super.category(element);
	}
}
