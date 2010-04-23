package org.overture.ide.vdmsl.ui.editor.contentAssist;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.overture.ide.ui.templates.VdmContentAssistProcessor;
import org.overture.ide.vdmsl.ui.IVdmSlUiConstants;

public class VdmSlContentAssistProcessor extends VdmContentAssistProcessor
		implements IContentAssistProcessor {

	@Override
	protected String getTempleteContextType() {
		return IVdmSlUiConstants.TEMPLATE_CONTEXT_TYPE;
	}

}
