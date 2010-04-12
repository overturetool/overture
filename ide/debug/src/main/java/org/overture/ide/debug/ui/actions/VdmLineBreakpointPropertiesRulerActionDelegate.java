package org.overture.ide.debug.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.AbstractRulerActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;

public class VdmLineBreakpointPropertiesRulerActionDelegate extends
		AbstractRulerActionDelegate {

	public VdmLineBreakpointPropertiesRulerActionDelegate() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IAction createAction(ITextEditor editor,
			IVerticalRulerInfo rulerInfo) {
		return new VdmBreakpointPropertiesRulerAction(editor, rulerInfo);

	}
}
