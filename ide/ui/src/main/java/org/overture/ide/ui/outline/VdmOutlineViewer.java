package org.overture.ide.ui.outline;

import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;


public class VdmOutlineViewer extends TreeViewer
{
	/**
	 * 
	 */
	private final VdmContentOutlinePage vdmContentOutlinePage;

	public VdmOutlineViewer(VdmContentOutlinePage vdmContentOutlinePage, Composite parent)
	{
		super(parent);
		this.vdmContentOutlinePage = vdmContentOutlinePage;
		setAutoExpandLevel(2);
		setUseHashlookup(true);

		//setSorter(new OutlineSorter());
	}

	@Override
	protected void fireSelectionChanged(SelectionChangedEvent event)
	{
		if (!this.vdmContentOutlinePage.inExternalSelectionMode)
		{
			super.fireSelectionChanged(event);
		}
	}

	public void dispose()
	{

		Tree t = getTree();
		if (t != null && !t.isDisposed())
		{
			if (t.getChildren() != null)
			{
				for (Control c : t.getChildren())
				{
					c.dispose();
				}
			}
			getLabelProvider().dispose();
			t.dispose();
		}

	}

}