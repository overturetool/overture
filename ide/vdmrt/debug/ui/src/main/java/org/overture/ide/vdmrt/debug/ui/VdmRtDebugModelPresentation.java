package org.overture.ide.vdmrt.debug.ui;

import org.eclipse.dltk.debug.ui.ScriptDebugModelPresentation;
import org.eclipse.ui.IEditorInput;
import org.overture.ide.vdmrt.ui.internal.editor.VdmRtEditorConstants;

/***
 * extension point: org.eclipse.debug.ui.debugModelPresentations
 * @author kedde
 *
 */
public class VdmRtDebugModelPresentation extends ScriptDebugModelPresentation {

	@Override
	public String getEditorId(IEditorInput input, Object element) {
		return VdmRtEditorConstants.EDITOR_ID;
	}

}
