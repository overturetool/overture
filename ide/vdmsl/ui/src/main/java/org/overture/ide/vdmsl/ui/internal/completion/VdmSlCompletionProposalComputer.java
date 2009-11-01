package org.overture.ide.vdmsl.ui.internal.completion;

import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalCollector;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;

public class VdmSlCompletionProposalComputer extends ScriptCompletionProposalComputer {

	@Override
	protected TemplateCompletionProcessor createTemplateProposalComputer(ScriptContentAssistInvocationContext context) {
		return new VdmSlTemplateCompletionProcessor(context);
	}

	@Override
	protected ScriptCompletionProposalCollector createCollector(ScriptContentAssistInvocationContext context) {
		return null; //new VdmPpCompletionProposalCollector(context.getSourceModule());
	}

}
