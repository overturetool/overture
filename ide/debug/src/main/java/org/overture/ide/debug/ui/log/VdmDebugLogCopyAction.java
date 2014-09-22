/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.ui.log;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

public class VdmDebugLogCopyAction extends Action
{

	private final TableViewer viewer;

	VdmDebugLogCopyAction(TableViewer viewer)
	{
		super(Messages.VdmDebugLogView_copy);
		this.viewer = viewer;
	}

	public void run()
	{
		final ISelection selection = viewer.getSelection();
		if (!selection.isEmpty() && selection instanceof IStructuredSelection)
		{
			final Object[] selected = ((IStructuredSelection) selection).toArray();
			final StringBuffer sb = new StringBuffer();
			for (int i = 0; i < selected.length; ++i)
			{
				final VdmDebugLogItem item = (VdmDebugLogItem) selected[i];
				if (i != 0)
				{
					sb.append('\n');
				}
				sb.append(item.toString());
			}
			setClipboardText(sb);
		}
	}

	private void setClipboardText(final StringBuffer sb)
	{
		final Clipboard clipboard = new Clipboard(viewer.getTable().getDisplay());
		try
		{
			clipboard.setContents(new Object[] { sb.toString() }, new Transfer[] { TextTransfer.getInstance() });
		} finally
		{
			clipboard.dispose();
		}
	}
}
