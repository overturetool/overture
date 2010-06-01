package org.overture.ide.debug.core.model.eval;

public interface IVdmEvaluationListener {
	/**
	 * Notifies this listener that an evaluation has completed, with the given
	 * result.
	 * 
	 * @param result
	 *            The result from the evaluation
	 * @see IEvaluationResult
	 */
	public void evaluationComplete(IVdmEvaluationResult result);
}
