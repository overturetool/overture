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

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.annotations.INAnnotation;
import org.overture.interpreter.eval.ExpressionEvaluator;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.Value;
import org.overture.parser.annotations.ASTAnnotationAdapter;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.annotations.TCAnnotation;

public class TraceAnnotation extends ASTAnnotationAdapter implements TCAnnotation, INAnnotation
{
	public TraceAnnotation()
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
		TypeChecker.report(6005, "@Trace only applies to expressions and statements", ast.getName().getLocation());
	}

	@Override
	public void tcBefore(PExp node, TypeCheckInfo question)
	{
		checkArgs(node);
	}

	@Override
	public void tcBefore(PStm node, TypeCheckInfo question)
	{
		checkArgs(node);
	}

	@Override
	public void tcBefore(AModuleModules node, TypeCheckInfo question)
	{
		TypeChecker.report(6005, "@Trace only applies to expressions and statements", ast.getName().getLocation());
	}

	@Override
	public void tcBefore(SClassDefinition node, TypeCheckInfo question)
	{
		TypeChecker.report(6005, "@Trace only applies to expressions and statements", ast.getName().getLocation());
	}

	public void checkArgs(INode node)
	{
		for (PExp arg: ast.getArgs())
		{
			if (!(arg instanceof AVariableExp))
			{
				TypeChecker.report(6006, "@Trace argument must be an identifier", arg.getLocation());
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
		printArgs(ctxt);
	}

	@Override
	public void inAfter(PStm node, Value value, Context ctxt)
	{
		// Nothing
	}

	@Override
	public void inBefore(PExp node, Context ctxt) throws AnalysisException
	{
		printArgs(ctxt);
	}

	@Override
	public void inAfter(PExp node, Value value, Context ctxt)
	{
		// Nothing
	}

	private void printArgs(Context ctxt) throws AnalysisException
	{
		if (ast.getArgs().isEmpty())
		{
			Console.err.println("Trace: " + ast.getName().getLocation());
		}
		else
		{
			ExpressionEvaluator eval = new ExpressionEvaluator();

			for (PExp arg: ast.getArgs())
			{
				Value v = arg.apply(eval, ctxt);
				Console.err.println("Trace: " + ast.getName().getLocation() + ", " + arg + " = " + v);
			}
		}
	}
}
