package org.overture.ide.vdmpp.ui.completions;

import org.eclipse.dltk.ui.text.completion.CompletionProposalLabelProvider;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProcessor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.ui.IEditorPart;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overture.ide.vdmpp.ui.UIPlugin;

public class VdmPpCompletionProcessor extends ScriptCompletionProcessor {

	public VdmPpCompletionProcessor(IEditorPart editor,
			ContentAssistant assistant, String partition) {
		super(editor, assistant, partition);
	}

	@Override
	protected String getNatureId() {
		return VdmPpProjectNature.VDM_PP_NATURE;
	}

	@Override
	protected CompletionProposalLabelProvider getProposalLabelProvider() {
		return new CompletionProposalLabelProvider();
	}

	@Override
	protected IPreferenceStore getPreferenceStore() {
		return UIPlugin.getDefault().getPreferenceStore();
	}

}
