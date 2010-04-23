package org.overture.ide.ui.templates;

import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;

public abstract class VdmContentAssistant extends ContentAssistant implements
		IContentAssistant {

	@Override
	abstract public IContentAssistProcessor getContentAssistProcessor(String contentType);

}
