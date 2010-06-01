package org.overture.ide.debug.core.model.eval;

import org.eclipse.debug.core.DebugException;
import org.overture.ide.debug.core.model.IVdmThread;
import org.overture.ide.debug.core.model.IVdmValue;

public interface IVdmEvaluationResult {
	/**
	 * Returns the value representing the result of the evaluation, or
	 * <code>null</code> if the associated evaluation failed. If the
	 * associated evaluation failed, there will be problems, or an exception in
	 * this result.
	 * 
	 * @return the resulting value, possibly <code>null</code>
	 */
	IVdmValue getValue();

	/**
	 * Returns whether the evaluation had any problems or if an exception
	 * occurred while performing the evaluation.
	 * 
	 * @return whether there were any problems.
	 * @see #getErrors()
	 * @see #getException()
	 */
	boolean hasErrors();

	/**
	 * Returns an array of problem messages. Each message describes a problem
	 * that occurred while compiling the snippet.
	 * 
	 * @return compilation error messages, or an empty array if no errors
	 *         occurred
	 */
	String[] getErrorMessages();

	/**
	 * Returns the snippet that was evaluated.
	 * 
	 * @return The string code snippet.
	 */
	String getSnippet();

	/**
	 * Returns any exception that occurred while performing the evaluation or
	 * <code>null</code> if an exception did not occur. The exception will be
	 * a debug exception or a debug exception that wrappers a JDI exception that
	 * indicates a problem communicating with the target or with actually
	 * performing some action in the target.
	 * 
	 * @return The exception that occurred during the evaluation
	 * @see org.eclipse.debug.core.DebugException
	 */
	DebugException getException();

	/**
	 * Returns the thread in which the evaluation was performed.
	 * 
	 * @return the thread in which the evaluation was performed
	 */
	IVdmThread getThread();
}
