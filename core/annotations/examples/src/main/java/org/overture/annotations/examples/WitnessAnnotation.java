/*******************************************************************************
 *
 *	Copyright (c) 2019 Nick Battle.
 *
 *	Author: Nick Battle
 *
 *	This file is part of Overture.
 *
 ******************************************************************************/

package org.overture.annotations.examples;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.annotations.INAnnotation;
import org.overture.interpreter.eval.ExpressionEvaluator;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.Value;
import org.overture.parser.annotations.ASTAnnotationAdapter;

/**
 * A test of an annotation with an instance init function.
 */
public class WitnessAnnotation extends ASTAnnotationAdapter implements INAnnotation
{
	public WitnessAnnotation()
	{
		super();
	}
	
	public boolean typecheckArgs()
	{
		return true;
	}
	
	protected void doInit(Object[] args)
	{
		Context ctxt = (Context)args[0];
		ExpressionEvaluator eval = new ExpressionEvaluator();

		for (PExp arg: ast.getArgs())
		{
			try
			{
				arg.apply(eval, ctxt);
			}
			catch (AnalysisException e)
			{
				// Ignore
			}
		}
	}

	@Override
	public void inBefore(PStm node, Context ctxt) throws AnalysisException
	{
	}

	@Override
	public void inBefore(PExp node, Context ctxt) throws AnalysisException
	{
	}

	@Override
	public void inAfter(PStm node, Value value, Context ctxt) throws AnalysisException
	{
	}

	@Override
	public void inAfter(PExp node, Value value, Context ctxt) throws AnalysisException
	{
	}
}
