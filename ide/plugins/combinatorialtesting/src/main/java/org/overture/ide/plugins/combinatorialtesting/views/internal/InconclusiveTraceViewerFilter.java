package org.overture.ide.plugins.combinatorialtesting.views.internal;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.TraceTestTreeNode;
import org.overture.interpreter.traces.Verdict;

public class InconclusiveTraceViewerFilter extends ViewerFilter
{
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		if (element instanceof TraceTestTreeNode
				&& ((TraceTestTreeNode) element).getStatus() == Verdict.INCONCLUSIVE)
		{
			return false;
		} else
		{
			return true;
		}
	}
}
