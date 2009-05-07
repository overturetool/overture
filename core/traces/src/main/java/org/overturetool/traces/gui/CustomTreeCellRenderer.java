package org.overturetool.traces.gui;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;


public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		if ((value instanceof TraceNode) && (value != null)) {
			setIcon(((TraceNode) value).getIcon());
		}

		// we can not call super.getTreeCellRendererComponent method, since it
		// overrides our setIcon call and cause rendering of labels to '...'
		// when node expansion is done

		// so, we copy (and modify logic little bit) from super class method:

		String stringValue = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus);

		this.hasFocus = hasFocus;
		setText(stringValue);
		if (sel)
			setForeground(getTextSelectionColor());
		else
			setForeground(getTextNonSelectionColor());

		if (!tree.isEnabled()) {
			setEnabled(false);
		} else {
			setEnabled(true);
		}
		setComponentOrientation(tree.getComponentOrientation());
		selected = sel;
		return this;

	}
}
