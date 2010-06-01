package org.overture.ide.debug.core.model.internal.eval;

import org.eclipse.debug.core.DebugException;
import org.overture.ide.debug.core.model.IVdmThread;
import org.overture.ide.debug.core.model.IVdmValue;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationResult;

public class FailedVdmEvaluationResult implements IVdmEvaluationResult {
	private final IVdmThread thread;
	private final String snippet;
	private DebugException exception;
	private String[] messages;

	public FailedVdmEvaluationResult(IVdmThread thread, String snippet,
			DebugException exception) {
		this.thread = thread;
		this.snippet = snippet;
		this.exception = exception;
	}

	public FailedVdmEvaluationResult(IVdmThread thread, String snippet,
			String[] messages) {
		this.thread = thread;
		this.snippet = snippet;
		this.messages = messages;
	}

	public FailedVdmEvaluationResult(IVdmThread thread, String snippet,
			DebugException exception, String[] messages) {
		this.thread = thread;
		this.snippet = snippet;
		this.exception = exception;
		this.messages = messages;
	}

	public boolean hasErrors() {
		return true;
	}

	public String[] getErrorMessages() {
		if (messages != null) {
			return messages;
		} else if (exception != null) {
			return new String[] { exception.getMessage() };
		}

		return new String[0];
	}

	public DebugException getException() {
		return exception;
	}

	public String getSnippet() {
		return snippet;
	}

	public IVdmThread getThread() {
		return thread;
	}

	public IVdmValue getValue() {
		return null;
	}
}
