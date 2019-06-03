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

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.statements.PStm;
import org.overture.parser.annotations.ASTAnnotationAdapter;
import org.overture.pog.annotations.POAnnotation;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IProofObligationList;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.annotations.TCAnnotation;

public class NoPOGAnnotation extends ASTAnnotationAdapter implements TCAnnotation, POAnnotation
{
	public NoPOGAnnotation()
	{
		super();
	}
	
	@Override
	public boolean typecheckArgs()
	{
		return false;
	}
	
	/**
	 * Type checker...
	 */

	@Override
	public void tcBefore(PDefinition node, TypeCheckInfo question)
	{
		noArgs();
	}

	@Override
	public void tcBefore(PExp node, TypeCheckInfo question)
	{
		noArgs();
	}

	@Override
	public void tcBefore(PStm node, TypeCheckInfo question)
	{
		noArgs();
	}

	@Override
	public void tcBefore(AModuleModules node, TypeCheckInfo question)
	{
		noArgs();
	}

	@Override
	public void tcBefore(SClassDefinition node, TypeCheckInfo question)
	{
		noArgs();
	}
	
	private void noArgs()
	{
		if (!ast.getArgs().isEmpty())
		{
			TypeChecker.report(6000, "@NoPOG has no arguments", ast.getName().getLocation());
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
	 * POG...
	 */
	
	@Override
	public IProofObligationList poBefore(PDefinition node, IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public void poAfter(PDefinition node, IProofObligationList list, IPOContextStack question)
	{
		list.clear();
	}
	
	@Override
	public IProofObligationList poBefore(PExp node, IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public void poAfter(PExp node, IProofObligationList list, IPOContextStack question)
	{
		list.clear();
	}
	
	@Override
	public IProofObligationList poBefore(PStm node, IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public void poAfter(PStm node, IProofObligationList list, IPOContextStack question)
	{
		list.clear();
	}
	
	@Override
	public IProofObligationList poBefore(AModuleModules node, IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public void poAfter(AModuleModules node, IProofObligationList list, IPOContextStack question)
	{
		list.clear();
	}
	
	@Override
	public IProofObligationList poBefore(SClassDefinition node, IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public void poAfter(SClassDefinition node, IProofObligationList list, IPOContextStack question)
	{
		list.clear();
	}
}
