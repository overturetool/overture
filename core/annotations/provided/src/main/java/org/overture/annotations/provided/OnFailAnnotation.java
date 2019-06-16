/*******************************************************************************
 *
 *	Copyright (c) 2019 Nick Battle.
 *
 *	Author: Nick Battle
 *
 *	This file is part of Overture
 *
 ******************************************************************************/

package org.overture.annotations.provided;

import java.util.Arrays;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AStringLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.annotations.INAnnotation;
import org.overture.interpreter.eval.ExpressionEvaluator;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.Value;
import org.overture.parser.annotations.ASTAnnotationAdapter;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.annotations.TCAnnotation;

public class OnFailAnnotation extends ASTAnnotationAdapter implements TCAnnotation, INAnnotation
{
	public OnFailAnnotation()
	{
		super();
	}
	
	@Override
	public boolean typecheckArgs()
	{
		return true;	// Check args
	}
	
	/**
	 * Type checker...
	 */

	@Override
	public void tcBefore(PDefinition node, TypeCheckInfo question)
	{
		TypeChecker.report(6005, "@OnFail only applies to expressions", ast.getName().getLocation());
	}

	@Override
	public void tcBefore(PExp node, TypeCheckInfo question)
	{
		checkArgs(node, question);
	}

	@Override
	public void tcBefore(PStm node, TypeCheckInfo question)
	{
		TypeChecker.report(6005, "@OnFail only applies to expressions", ast.getName().getLocation());
	}

	@Override
	public void tcBefore(AModuleModules node, TypeCheckInfo question)
	{
		TypeChecker.report(6005, "@OnFail only applies to expressions", ast.getName().getLocation());
	}

	@Override
	public void tcBefore(SClassDefinition node, TypeCheckInfo question)
	{
		TypeChecker.report(6005, "@OnFail only applies to expressions", ast.getName().getLocation());
	}

	public void checkArgs(INode node, TypeCheckInfo question)
	{
		if (ast.getArgs().isEmpty())
		{
			TypeChecker.report(6008, "@OnFail must srart with a string argument", ast.getName().getLocation());
		}
		else if (!(ast.getArgs().get(0) instanceof AStringLiteralExp))
		{
			TypeChecker.report(6008, "@OnFail must start with a string argument", ast.getName().getLocation());
		}
		else
		{
			AStringLiteralExp str = (AStringLiteralExp)ast.getArgs().get(0);
			String format = str.getValue().getValue();
			
			try
			{
				// Try to format with string arguments to check they are all %s (up to 20)
				Object[] args = new String[20];
				Arrays.fill(args, "A string");
				String.format(format, args);
			}
			catch (IllegalArgumentException e)
			{
				TypeChecker.report(6008, "@OnFail must use %[arg$][width]s conversions", ast.getName().getLocation());
			}
		}
	}

	@Override
	public void tcAfter(PDefinition node, TypeCheckInfo question)
	{
		// Nothing
	}

	@Override
	public void tcAfter(PExp node, TypeCheckInfo question)
	{
		// Nothing
	}

	@Override
	public void tcAfter(PStm node, TypeCheckInfo question)
	{
		// Nothing
	}

	@Override
	public void tcAfter(AModuleModules node, TypeCheckInfo question)
	{
		// Nothing
	}

	@Override
	public void tcAfter(SClassDefinition node, TypeCheckInfo question)
	{
		// Nothing
	}
	
	/**
	 * Interpreter...
	 */

	@Override
	public void inBefore(PStm node, Context ctxt) throws AnalysisException
	{
		// Nothing
	}

	@Override
	public void inAfter(PStm node, Value value, Context ctxt)
	{
		// Nothing
	}

	@Override
	public void inBefore(PExp node, Context ctxt) throws AnalysisException
	{
		// Nothing
	}

	@Override
	public void inAfter(PExp node, Value value, Context ctxt) throws AnalysisException
	{
		if (!value.boolValue(ctxt))		// ONLY if we failed
		{
			printArgs(ctxt);
		}
	}

	private void printArgs(Context ctxt) throws AnalysisException
	{
		Object[] values = new Value[ast.getArgs().size() - 1];
		ExpressionEvaluator eval = new ExpressionEvaluator();
		
		for (int p=1; p < ast.getArgs().size(); p++)
		{
			values[p-1] = ast.getArgs().get(p).apply(eval, ctxt);
		}
		
		AStringLiteralExp fmt = (AStringLiteralExp)ast.getArgs().get(0);
		System.out.printf(fmt.getValue().getValue(), values);
	}
}
