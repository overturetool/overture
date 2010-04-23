package org.overture.ide.vdmpp.ui.editor.core;

import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.overture.ide.ui.editor.core.VdmSourceViewerConfiguration;
import org.overture.ide.ui.editor.syntax.VdmColorProvider;
import org.overture.ide.vdmpp.ui.editor.contentAssist.VdmPpContentAssistant;
import org.overture.ide.vdmpp.ui.editor.syntax.VdmPpCodeScanner;



public class VdmPpSourceViewerConfiguration extends
		VdmSourceViewerConfiguration {

	@Override
	protected ITokenScanner getVdmCodeScanner() {
		return new VdmPpCodeScanner(new VdmColorProvider());
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {		
		return new VdmPpContentAssistant();
	}

}
