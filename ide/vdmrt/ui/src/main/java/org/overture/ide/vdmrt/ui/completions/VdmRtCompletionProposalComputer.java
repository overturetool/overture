package org.overture.ide.vdmrt.ui.completions;

import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalCollector;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;

public class VdmRtCompletionProposalComputer extends ScriptCompletionProposalComputer {

	public VdmRtCompletionProposalComputer() {
	}

	@Override
	protected TemplateCompletionProcessor createTemplateProposalComputer(ScriptContentAssistInvocationContext context) {
		return new VdmRtTemplateCompletionProcessor(context);
	}

	@Override
	protected ScriptCompletionProposalCollector createCollector(ScriptContentAssistInvocationContext context) {
		return null; //new VdmPpCompletionProposalCollector(context.getSourceModule());
	}
}
