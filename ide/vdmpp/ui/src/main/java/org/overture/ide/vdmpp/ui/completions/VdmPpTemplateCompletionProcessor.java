package org.overture.ide.vdmpp.ui.completions;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.overture.ide.ui.completion.templates.VdmTemplateCompletionProcessor;
import org.overture.ide.vdmpp.ui.VdmPpUiPluginConstants;

public class VdmPpTemplateCompletionProcessor  extends VdmTemplateCompletionProcessor{

	public VdmPpTemplateCompletionProcessor(ScriptContentAssistInvocationContext context) {
		super(context);
	}

	@Override
	protected String getContextTypeId() {
		return VdmPpUiPluginConstants.CONTEXT_TYPE_ID;
	}

	@Override
	protected ScriptTemplateAccess getTemplateAccess() {
		return null;
	}

}
