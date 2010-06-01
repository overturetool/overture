package org.overture.ide.debug.core.model.internal.eval;

import org.eclipse.debug.core.DebugException;
import org.overture.ide.debug.core.model.IVdmThread;
import org.overture.ide.debug.core.model.IVdmValue;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationResult;
import org.overture.ide.debug.utils.CharOperation;

public class VdmEvaluationResult implements IVdmEvaluationResult {
	private final IVdmThread thread;
	private final String snippet;
	private final IVdmValue value;

	public VdmEvaluationResult(IVdmThread thread, String snippet,
			IVdmValue value) {
		this.thread = thread;
		this.value = value;
		this.snippet = snippet;
	}

	public String getSnippet() {
		return snippet;
	}

	public IVdmValue getValue() {
		return value;
	}

	public IVdmThread getThread() {
		return thread;
	}

	public String[] getErrorMessages() {
		return CharOperation.NO_STRINGS;
	}

	public DebugException getException() {
		return null;
	}

	public boolean hasErrors() {
		return false;
	}
}
