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
