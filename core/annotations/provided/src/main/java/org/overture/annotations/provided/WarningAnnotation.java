/*******************************************************************************
 *
 *	Copyright (c) 2022 Nick Battle.
 *
 *	Author: Alessandro Pezzoni, Nick Battle
 *
 *	This file is part of Overture.
 *
 *******************************************************************************/

package org.overture.annotations.provided;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.statements.PStm;
import org.overture.parser.annotations.ASTAnnotationAdapter;
import org.overture.parser.messages.VDMWarning;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.annotations.TCAnnotation;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class WarningAnnotation extends ASTAnnotationAdapter implements TCAnnotation
{
	public WarningAnnotation()
	{
		super();
	}

	@Override
	public boolean typecheckArgs()
	{
		return true; // Check args
	}

	private int warningCount = 0;
	private Set<Long> suppressed;

	@Override
	public void tcBefore(PDefinition node, TypeCheckInfo question)
	{
		preCheck();
	}

	@Override
	public void tcBefore(PExp node, TypeCheckInfo question)
	{
		preCheck();
	}

	@Override
	public void tcBefore(PStm node, TypeCheckInfo question)
	{
		preCheck();
	}

	@Override
	public void tcBefore(AModuleModules node, TypeCheckInfo question)
	{
		preCheck();
	}

	@Override
	public void tcBefore(SClassDefinition node, TypeCheckInfo question)
	{
		preCheck();
	}

	private void preCheck()
	{
		LinkedList<PExp> args = ast.getArgs();

		if (args.isEmpty())
		{
			TypeChecker.report(6010, "@Warning must have one or more numeric arguments", ast.getName().getLocation());
		}

		warningCount = TypeChecker.getWarningCount();
		suppressed = new HashSet<Long>();

		for (PExp arg : args)
		{
			if (!(arg instanceof AIntLiteralExp))
			{
				TypeChecker.report(6010, "@Warning arguments must be warning number literals", arg.getLocation());
			}
			else
			{
				AIntLiteralExp w = (AIntLiteralExp) arg;
				suppressed.add(w.getValue().getValue());
			}
		}
	}

	@Override
	public void tcAfter(PDefinition node, TypeCheckInfo question)
	{
		postCheck();
	}

	@Override
	public void tcAfter(PExp node, TypeCheckInfo question)
	{
		postCheck();
	}

	@Override
	public void tcAfter(PStm node, TypeCheckInfo question)
	{
		postCheck();
	}

	@Override
	public void tcAfter(AModuleModules node, TypeCheckInfo question)
	{
		postCheck();
	}

	@Override
	public void tcAfter(SClassDefinition node, TypeCheckInfo question)
	{
		postCheck();
	}

	private void postCheck()
	{
		Iterator<VDMWarning> witer = TypeChecker.getWarnings().iterator();

		for (int i = 0; i < warningCount; i++)
		{
			witer.next(); // skip previous warnings
		}

		while (witer.hasNext())
		{
			VDMWarning w = witer.next();

			if (suppressed.contains((long) w.number))
			{
				witer.remove();
			}
		}
	}
	
	@Override
	public void doClose()
	{
		Iterator<VDMWarning> witer = TypeChecker.getWarnings().iterator();
		int myLine = ast.getName().getLocation().getStartLine();
		File myFile = ast.getName().getLocation().getFile();

		while (witer.hasNext())
		{
			VDMWarning w = witer.next();

			if (w.location.getStartLine() == myLine + 1 &&
				w.location.getFile().equals(myFile) &&
				suppressed.contains((long)w.number))
			{
				// Warning is on the line after the one we annotated, so remove it
				witer.remove();
			}
		}
	}
}
