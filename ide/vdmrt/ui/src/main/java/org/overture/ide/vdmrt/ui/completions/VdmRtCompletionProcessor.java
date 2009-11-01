package org.overture.ide.vdmrt.ui.completions;

import org.eclipse.dltk.ui.text.completion.CompletionProposalLabelProvider;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProcessor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.ui.IEditorPart;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;
import org.overture.ide.vdmrt.ui.UIPlugin;

public class VdmRtCompletionProcessor extends ScriptCompletionProcessor {

	public VdmRtCompletionProcessor(IEditorPart editor,
			ContentAssistant assistant, String partition) {
		super(editor, assistant, partition);
	}

	@Override
	protected String getNatureId() {
		return VdmRtProjectNature.VDM_RT_NATURE;
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
