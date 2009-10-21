package org.overture.ide.vdmsl.debug.ui;

import org.eclipse.dltk.debug.ui.ScriptDebugModelPresentation;
import org.eclipse.ui.IEditorInput;
import org.overture.ide.vdmsl.ui.internal.editor.VdmslEditorConstants;

/***
 * extension point: org.eclipse.debug.ui.debugModelPresentations
 * @author kedde
 *
 */
public class VdmSlDebugModelPresentation extends ScriptDebugModelPresentation {

	@Override
	public String getEditorId(IEditorInput input, Object element) {
		return VdmslEditorConstants.EDITOR_ID;
	}

}
