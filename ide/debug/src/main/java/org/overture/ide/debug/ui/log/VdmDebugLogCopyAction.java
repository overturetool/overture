/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.overture.ide.debug.ui.log;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

public class VdmDebugLogCopyAction extends Action {

	private final TableViewer viewer;

	VdmDebugLogCopyAction(TableViewer viewer) {
		super(Messages.VdmDebugLogView_copy);
		this.viewer = viewer;
	}

	public void run() {
		final ISelection selection = viewer.getSelection();
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			final Object[] selected = ((IStructuredSelection) selection)
					.toArray();
			final StringBuffer sb = new StringBuffer();
			for (int i = 0; i < selected.length; ++i) {
				final VdmDebugLogItem item = (VdmDebugLogItem) selected[i];
				if (i != 0) {
					sb.append('\n');
				}
				sb.append(item.toString());
			}
			setClipboardText(sb);
		}
	}

	private void setClipboardText(final StringBuffer sb) {
		final Clipboard clipboard = new Clipboard(viewer.getTable()
				.getDisplay());
		try {
			clipboard.setContents(new Object[] { sb.toString() },
					new Transfer[] { TextTransfer.getInstance() });
		} finally {
			clipboard.dispose();
		}
	}
}
