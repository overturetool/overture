package org.overture.ide.vdmsl.ui.internal.completion;

import org.eclipse.dltk.ui.text.completion.CompletionProposalLabelProvider;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProcessor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.ui.IEditorPart;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;
import org.overture.ide.vdmsl.ui.UIPlugin;

public class VdmSlCompletionProcessor extends ScriptCompletionProcessor {

	public VdmSlCompletionProcessor(IEditorPart editor,
			ContentAssistant assistant, String partition) {
		super(editor, assistant, partition);
	}

	@Override
	protected String getNatureId() {
		return VdmSlProjectNature.VDM_SL_NATURE;
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
