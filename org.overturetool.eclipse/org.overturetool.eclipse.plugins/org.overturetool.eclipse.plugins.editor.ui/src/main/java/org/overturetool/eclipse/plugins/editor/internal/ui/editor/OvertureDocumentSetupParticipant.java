package org.overturetool.eclipse.plugins.editor.internal.ui.editor;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.overturetool.eclipse.plugins.editor.internal.ui.UIPlugin;
import org.overturetool.eclipse.plugins.editor.internal.ui.text.OvertureTextTools;
import org.overturetool.eclipse.plugins.editor.ui.text.IOverturePartitions;

public class OvertureDocumentSetupParticipant implements
		IDocumentSetupParticipant {

	public OvertureDocumentSetupParticipant() {
	}

	/*
	 * @see org.eclipse.core.filebuffers.IDocumentSetupParticipant#setup(org.eclipse.jface.text.IDocument)
	 */
	public void setup(IDocument document) {
		OvertureTextTools tools = UIPlugin.getDefault().getTextTools();
		tools.setupDocumentPartitioner(document, IOverturePartitions.OVERTURE_PARTITIONING);
	}
}