package org.overture.ide.ui.completion.templates;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.templates.ScriptTemplateCompletionProcessor;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;

public abstract class VdmTemplateCompletionProcessor extends ScriptTemplateCompletionProcessor {

	private static char[] IGNORE = new char[] {'.'};
	
	public VdmTemplateCompletionProcessor(ScriptContentAssistInvocationContext context) {
		super(context);
	}

	@Override
	abstract protected String getContextTypeId();

	@Override
	protected abstract ScriptTemplateAccess getTemplateAccess();
	
	@Override
	protected char[] getIgnore() {
		return IGNORE;
	}

}
