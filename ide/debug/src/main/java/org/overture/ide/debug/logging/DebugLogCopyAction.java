package org.overture.ide.debug.logging;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;


public class DebugLogCopyAction extends Action
{
	private final TableViewer viewer;

	DebugLogCopyAction(TableViewer viewer) {
		super("Copy");
		this.viewer = viewer;
	}

	public void run() {
		final ISelection selection = viewer.getSelection();
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			final Object[] selected = ((IStructuredSelection) selection)
					.toArray();
			final StringBuffer sb = new StringBuffer();
			for (int i = 0; i < selected.length; ++i) {
				final LogItem item = (LogItem) selected[i];
				if (i != 0) {
					sb.append('\n');
				}
				sb.append(item.getData().toString());
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
