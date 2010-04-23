package org.overture.ide.vdmrt.ui.editor.contentAssist;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.overture.ide.ui.templates.VdmContentAssistProcessor;
import org.overture.ide.vdmrt.ui.IVdmRtUiConstants;

public class VdmRtContentAssistProcessor extends VdmContentAssistProcessor
		implements IContentAssistProcessor {

	@Override
	protected String getTempleteContextType() {
		return IVdmRtUiConstants.TEMPLATE_CONTENT_TYPE;
	}

}
