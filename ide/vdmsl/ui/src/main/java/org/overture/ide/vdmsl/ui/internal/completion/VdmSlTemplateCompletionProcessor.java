package org.overture.ide.vdmsl.ui.internal.completion;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.overture.ide.ui.completion.templates.VdmTemplateCompletionProcessor;
import org.overture.ide.vdmsl.ui.VdmSlUiPluginConstants;

public class VdmSlTemplateCompletionProcessor  extends VdmTemplateCompletionProcessor{

	public VdmSlTemplateCompletionProcessor(ScriptContentAssistInvocationContext context) {
		super(context);
	}

	@Override
	protected String getContextTypeId() {
		return VdmSlUiPluginConstants.CONTEXT_TYPE_ID;
	}

	@Override
	protected ScriptTemplateAccess getTemplateAccess() {
		return VdmSlTemplateAccess.getInstance();
	}

}
