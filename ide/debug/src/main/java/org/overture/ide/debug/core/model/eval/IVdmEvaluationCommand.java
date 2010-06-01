package org.overture.ide.debug.core.model.eval;

import org.overture.ide.debug.core.model.IVdmDebugTarget;


public interface IVdmEvaluationCommand {
	IVdmDebugTarget getVdmDebugTarget();

	IVdmEvaluationResult syncEvaluate();

	void asyncEvaluate(IVdmEvaluationListener listener);

	void dispose();
}
