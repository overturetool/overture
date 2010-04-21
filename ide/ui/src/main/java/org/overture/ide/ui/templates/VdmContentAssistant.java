package org.overture.ide.ui.templates;

import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.overture.ide.ui.editor.core.VdmEditor;

public class VdmContentAssistant extends ContentAssistant implements IContentAssistant {

	
	
	@Override
	public IContentAssistProcessor getContentAssistProcessor(String contentType) {
		return new VdmContentAssistProcessor();
	}
	


	

}
