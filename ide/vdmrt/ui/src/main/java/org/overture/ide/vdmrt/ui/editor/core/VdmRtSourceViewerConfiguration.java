package org.overture.ide.vdmrt.ui.editor.core;

import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.overture.ide.ui.editor.core.VdmSourceViewerConfiguration;
import org.overture.ide.ui.editor.syntax.VdmColorProvider;
import org.overture.ide.vdmrt.ui.editor.contentAssist.VdmRtContentAssistent;
import org.overture.ide.vdmrt.ui.editor.syntax.VdmRtCodeScanner;


public class VdmRtSourceViewerConfiguration extends
		VdmSourceViewerConfiguration {

	@Override
	protected ITokenScanner getVdmCodeScanner() {
		return new VdmRtCodeScanner(new VdmColorProvider());
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant  assistant = new VdmRtContentAssistent();
		assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		return assistant;
	}

}
