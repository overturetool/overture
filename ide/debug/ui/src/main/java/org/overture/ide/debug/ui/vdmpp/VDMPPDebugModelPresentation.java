package org.overture.ide.debug.ui.vdmpp;

import org.eclipse.dltk.debug.ui.ScriptDebugModelPresentation;
import org.eclipse.ui.IEditorInput;
import org.overture.ide.vdmpp.ui.internal.editor.VdmppEditorConstants;

/***
 * extension point: org.eclipse.debug.ui.debugModelPresentations
 * @author kedde
 *
 */
public class VDMPPDebugModelPresentation extends ScriptDebugModelPresentation {

	@Override
	public String getEditorId(IEditorInput input, Object element) {
		return VdmppEditorConstants.EDITOR_ID;
	}

}
