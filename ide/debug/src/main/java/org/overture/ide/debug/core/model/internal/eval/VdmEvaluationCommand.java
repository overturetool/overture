package org.overture.ide.debug.core.model.internal.eval;

import org.overture.ide.debug.core.model.IVdmDebugTarget;
import org.overture.ide.debug.core.model.IVdmStackFrame;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationCommand;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationEngine;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationListener;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationResult;

public class VdmEvaluationCommand implements IVdmEvaluationCommand {
	private final IVdmEvaluationEngine engine;
	private final String snippet;
	private final IVdmStackFrame frame;

	public VdmEvaluationCommand(IVdmEvaluationEngine engine,
			String snippet, IVdmStackFrame frame) {
		this.snippet = snippet;
		this.engine = engine;
		this.frame = frame;
	}

	public IVdmDebugTarget getVdmDebugTarget() {
		return engine.getVdmDebugTarget();
	}

	public IVdmEvaluationResult syncEvaluate() {
		return engine.syncEvaluate(snippet, frame);
	}

	public void asyncEvaluate(IVdmEvaluationListener listener) {
		engine.asyncEvaluate(snippet, frame, listener);
	}

	public void dispose() {
		engine.dispose();
	}
}
