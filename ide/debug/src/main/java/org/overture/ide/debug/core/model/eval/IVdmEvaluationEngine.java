package org.overture.ide.debug.core.model.eval;

import org.overture.ide.debug.core.model.IVdmDebugTarget;
import org.overture.ide.debug.core.model.IVdmStackFrame;

public interface IVdmEvaluationEngine {
	IVdmDebugTarget getVdmDebugTarget();

	IVdmEvaluationResult syncEvaluate(String snippet, IVdmStackFrame frame);

	void asyncEvaluate(String snippet, IVdmStackFrame frame,
			IVdmEvaluationListener listener);

	void dispose();
}
