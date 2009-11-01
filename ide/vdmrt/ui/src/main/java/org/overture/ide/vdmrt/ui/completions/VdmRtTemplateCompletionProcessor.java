package org.overture.ide.vdmrt.ui.completions;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.overture.ide.ui.completion.templates.VdmTemplateCompletionProcessor;
import org.overture.ide.vdmrt.ui.VdmRtUiPluginConstants;

public class VdmRtTemplateCompletionProcessor  extends VdmTemplateCompletionProcessor{

	public VdmRtTemplateCompletionProcessor(ScriptContentAssistInvocationContext context) {
		super(context);
	}

	@Override
	protected String getContextTypeId() {
		return VdmRtUiPluginConstants.CONTEXT_TYPE_ID;
	}

	@Override
	protected ScriptTemplateAccess getTemplateAccess() {
		return VdmRtTemplateAccess.getInstance();
	}

}
