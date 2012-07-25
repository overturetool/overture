package org.overture.ide.plugins.traces.views.internal;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.overture.ide.plugins.traces.views.treeView.TraceTestTreeNode;
import org.overture.interpreter.traces.Verdict;

public class OkTraceViewerFilter extends ViewerFilter
{
	@Override
	public boolean select(Viewer viewer, Object parentElement,
			Object element)
	{
		if (element instanceof TraceTestTreeNode
				&& ((TraceTestTreeNode) element).getStatus() == Verdict.PASSED)
		{
			return false;
		} else
		{
			return true;
		}
	}
}
