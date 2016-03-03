package org.overture.codegen.runtime;

import java.io.Serializable;
/**
 * This interface contains only one method that every generated class should implement 
 * when the concurrency features are selected for generation.
 * Returns a boolean value which is the result of the evaluation of the corresponding permission predicate generated
 *
 * @param A number which represents the constant that is assigned to each class method.
 * 
 * @author gkanos
 *
 */
public interface EvaluatePP extends Serializable
{
	public Boolean evaluatePP(final Number fnr);
}
