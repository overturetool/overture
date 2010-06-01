package org.overture.ide.debug.core.model.internal;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IWatchExpressionResult;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationResult;

public class VdmWatchExpressionResult implements IWatchExpressionResult {

	private final IVdmEvaluationResult result;

	public VdmWatchExpressionResult(IVdmEvaluationResult result) {
		this.result = result;
	}

	public String[] getErrorMessages() {
		return result.getErrorMessages();
	}

	public DebugException getException() {
		return result.getException();
	}

	public String getExpressionText() {
		return result.getSnippet();
	}

	public IValue getValue() {
		return result.getValue();
	}

	public boolean hasErrors() {
		return result.hasErrors();
	}
}
