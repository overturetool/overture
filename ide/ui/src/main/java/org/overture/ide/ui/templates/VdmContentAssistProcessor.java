package org.overture.ide.ui.templates;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class VdmContentAssistProcessor extends VdmTemplateAssistProcessor {

//	private VdmEditor editor;

//	public VdmContentAssistProcessor(VdmEditor editor) {
//		this.editor = editor;
//	}

	public boolean enableTemplate() {
		return true;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		ICompletionProposal[] completionProposals = null;
//		IEditorInput editorInput = editor.getEditorInput();
//		String text = viewer.getTextWidget().getText();
		List<Object> modList = new ArrayList<Object>();
		if (enableTemplate()) {
			ICompletionProposal[] templates = super.computeCompletionProposals(
					viewer, offset);
			if (templates != null) {
				for (int i = 0; i < templates.length; i++) {
					modList.add(templates[i]);
				}

			}
			if (completionProposals != null) {
				for (int i = 0; i < completionProposals.length; i++) {
					modList.add(completionProposals[i]);
				}
			}
		}

		if (modList.size() > 0)
			return (ICompletionProposal[]) modList
					.toArray(new ICompletionProposal[modList.size()]);

		return completionProposals;
	}
}
