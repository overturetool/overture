package org.overture.ide.debug.core.model.eval;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IErrorReportingExpression;
import org.eclipse.debug.core.model.IValue;

public class EvaluatedVdmExpression extends PlatformObject implements
		IErrorReportingExpression {
	public static String[] getErrorMessages(IVdmEvaluationResult result) {
		if (result == null) {
			return new String[0];
		}
		String messages[] = result.getErrorMessages();
		if (messages.length > 0) {
			return messages;
		}
		DebugException exception = result.getException();
		if (exception != null) {
			return new String[] { exception.getMessage() };
		}
		return new String[0];
	}

	private final IVdmEvaluationResult result;

	public EvaluatedVdmExpression(IVdmEvaluationResult result) {
		if (result == null) {
			throw new IllegalArgumentException();
		}

		this.result = result;
	}

	public String[] getErrorMessages() {
		return getErrorMessages(result);
	}

	public boolean hasErrors() {
		return result.hasErrors();
	}

	public IDebugTarget getDebugTarget() {
		return result.getThread().getDebugTarget();
	}

	public String getExpressionText() {
		return result.getSnippet();
	}

	public IValue getValue() {
		return result.getValue();
	}

	public ILaunch getLaunch() {
		return getDebugTarget().getLaunch();
	}

	public String getModelIdentifier() {
		return getDebugTarget().getModelIdentifier();
	}

	public void dispose() {

	}
}
