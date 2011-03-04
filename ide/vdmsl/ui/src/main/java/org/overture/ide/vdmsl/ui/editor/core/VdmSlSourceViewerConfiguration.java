package org.overture.ide.vdmsl.ui.editor.core;

import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.overture.ide.ui.editor.core.VdmSourceViewerConfiguration;
import org.overture.ide.ui.editor.syntax.VdmColorProvider;
import org.overture.ide.vdmsl.ui.editor.contentAssist.VdmSlContentAssistant;
import org.overture.ide.vdmsl.ui.editor.syntax.VdmSlCodeScanner;



public class VdmSlSourceViewerConfiguration extends
		VdmSourceViewerConfiguration {


	@Override
	protected ITokenScanner getVdmCodeScanner() {
		return new VdmSlCodeScanner(new VdmColorProvider());
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant  assistant = new VdmSlContentAssistant();
		assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		return assistant;
	}
	
	
	
}
