package org.overture.ide.vdmsl.ui.editor.contentAssist;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.overture.ide.ui.templates.VdmContentAssistant;

public class VdmSlContentAssistant extends VdmContentAssistant implements
		IContentAssistant {

	@Override
	public IContentAssistProcessor getContentAssistProcessor(String contentType) {
		return new VdmSlContentAssistProcessor();
	}

}
