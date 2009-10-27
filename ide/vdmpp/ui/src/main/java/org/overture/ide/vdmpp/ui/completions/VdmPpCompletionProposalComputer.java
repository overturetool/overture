package org.overture.ide.vdmpp.ui.completions;

import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalCollector;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;

public class VdmPpCompletionProposalComputer extends ScriptCompletionProposalComputer {

	public VdmPpCompletionProposalComputer() {
	}

	@Override
	protected TemplateCompletionProcessor createTemplateProposalComputer(ScriptContentAssistInvocationContext context) {
		return new VdmPpTemplateCompletionProcessor(context);
	}

	@Override
	protected ScriptCompletionProposalCollector createCollector(ScriptContentAssistInvocationContext context) {
		return null; //new VdmPpCompletionProposalCollector(context.getSourceModule());
	}
}
