package org.overture.ide.vdmpp.ui.editor.contentAssist;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.overture.ide.ui.templates.VdmContentAssistProcessor;
import org.overture.ide.vdmpp.ui.IVdmPpUiConstants;

public class VdmPpContentAssistProcessor extends VdmContentAssistProcessor
		implements IContentAssistProcessor {

	@Override
	protected String getTempleteContextType() {
		return IVdmPpUiConstants.TEMPLATE_CONTEXT_TYPE;
	}

}
