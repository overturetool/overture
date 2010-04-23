package org.overture.ide.vdmrt.ui.editor.contentAssist;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.overture.ide.ui.templates.VdmContentAssistant;

public class VdmRtContentAssistent extends VdmContentAssistant implements
		IContentAssistant {

	@Override
	public IContentAssistProcessor getContentAssistProcessor(String contentType) {
		return new VdmRtContentAssistProcessor();
	}

}
