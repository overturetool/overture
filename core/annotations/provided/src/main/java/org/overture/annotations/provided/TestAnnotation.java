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
import org.overture.interpreter.annotations.INAnnotation;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.Value;
import org.overture.parser.annotations.ASTAnnotationAdapter;
import org.overture.parser.syntax.ClassReader;
import org.overture.parser.syntax.DefinitionReader;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ModuleReader;
import org.overture.parser.syntax.StatementReader;
import org.overture.pog.annotations.POAnnotation;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IProofObligationList;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.annotations.TCAnnotation;

public class TestAnnotation extends ASTAnnotationAdapter implements TCAnnotation, INAnnotation, POAnnotation
{
	public TestAnnotation()
	{
		super();
	}
	
	public static void doInit()
	{
		System.out.printf("doInit TestAnnotation\n");
	}
	
	@Override
	public boolean typecheckArgs()
	{
		return false;	// Allow any expressions
	}
	
	/**
	 * Parser... 
	 */

	@Override
	public void astBefore(DefinitionReader reader)
	{
		System.out.printf("astBefore %s\n", reader.getClass().getSimpleName(), this);
	}

	@Override
	public void astBefore(StatementReader reader)
	{
		System.out.printf("astBefore %s\n", reader.getClass().getSimpleName(), this);
	}

	@Override
	public void astBefore(ExpressionReader reader)
	{
		System.out.printf("astBefore %s\n", reader.getClass().getSimpleName(), this);
	}

	@Override
	public void astBefore(ModuleReader reader)
	{
		System.out.printf("astBefore %s\n", reader.getClass().getSimpleName(), this);
	}

	@Override
	public void astBefore(ClassReader reader)
	{
		System.out.printf("astBefore %s\n", reader.getClass().getSimpleName(), this);
	}

	@Override
	public void astAfter(DefinitionReader reader, PDefinition def)
	{
		System.out.printf("astAfter %s\n", reader.getClass().getSimpleName(), def.getClass().getSimpleName(), this);
	}

	@Override
	public void astAfter(StatementReader reader, PStm stmt)
	{
		System.out.printf("astAfter %s %s %s\n", reader.getClass().getSimpleName(), stmt.getClass().getSimpleName(), this);
	}

	@Override
	public void astAfter(ExpressionReader reader, PExp exp)
	{
		System.out.printf("astAfter %s %s %s\n", reader.getClass().getSimpleName(), exp.getClass().getSimpleName(), this);
	}

	@Override
	public void astAfter(ModuleReader reader, AModuleModules module)
	{
		System.out.printf("astAfter %s %s %s\n", reader.getClass().getSimpleName(), module.getClass().getSimpleName(), this);
	}

	@Override
	public void astAfter(ClassReader reader, SClassDefinition clazz)
	{
		System.out.printf("astAfter %s %s %s\n", reader.getClass().getSimpleName(), clazz.getClass().getSimpleName(), this);
	}
	
	/**
	 * Type checker...
	 */

	@Override
	public void tcBefore(PDefinition node, TypeCheckInfo question)
	{
		System.out.printf("tcBefore %s %s\n", node.getClass().getSimpleName(), this);
	}

	@Override
	public void tcBefore(PExp node, TypeCheckInfo question)
	{
		System.out.printf("tcBefore %s %s\n", node.getClass().getSimpleName(), this);
	}

	@Override
	public void tcBefore(PStm node, TypeCheckInfo question)
	{
		System.out.printf("tcBefore %s %s\n", node.getClass().getSimpleName(), this);
	}

	@Override
	public void tcBefore(AModuleModules node, TypeCheckInfo question)
	{
		System.out.printf("tcBefore %s %s\n", node.getClass().getSimpleName(), this);
	}

	@Override
	public void tcBefore(SClassDefinition node, TypeCheckInfo question)
	{
		System.out.printf("tcBefore %s %s\n", node.getClass().getSimpleName(), this);
	}

	@Override
	public void tcAfter(PDefinition node, TypeCheckInfo question)
	{
		System.out.printf("tcAfter %s %s\n", node.getClass().getSimpleName(), this);
	}

	@Override
	public void tcAfter(PExp node, TypeCheckInfo question)
	{
		System.out.printf("tcAfter %s %s\n", node.getClass().getSimpleName(), this);
	}

	@Override
	public void tcAfter(PStm node, TypeCheckInfo question)
	{
		System.out.printf("tcAfter %s %s\n", node.getClass().getSimpleName(), this);
	}

	@Override
	public void tcAfter(AModuleModules node, TypeCheckInfo question)
	{
		System.out.printf("tcAfter %s %s\n", node.getClass().getSimpleName(), this);
	}

	@Override
	public void tcAfter(SClassDefinition node, TypeCheckInfo question)
	{
		System.out.printf("tcAfter %s %s\n", node.getClass().getSimpleName(), this);
	}

	/**
	 * Interpreter...
	 */
	
	@Override
	public void inBefore(PStm node, Context ctxt)
	{
		System.out.printf("inBefore %s %s\n", node.getClass().getSimpleName(), this);
	}

	@Override
	public void inAfter(PStm node, Value value, Context ctxt)
	{
		System.out.printf("inAfter %s %s\n", node.getClass().getSimpleName(), this);
	}

	@Override
	public void inBefore(PExp node, Context ctxt)
	{
		System.out.printf("inBefore %s %s\n", node.getClass().getSimpleName(), this);
	}

	@Override
	public void inAfter(PExp node, Value value, Context ctxt)
	{
		System.out.printf("inAfter %s %s\n", node.getClass().getSimpleName(), this);
	}
	
	/**
	 * POG...
	 */

	@Override
	public IProofObligationList poBefore(PDefinition node, IPOContextStack question)
	{
		System.out.printf("poBefore %s %s\n", node.getClass().getSimpleName(), this);
		return new ProofObligationList();
	}

	@Override
	public void poAfter(PDefinition node, IProofObligationList list, IPOContextStack question)
	{
		System.out.printf("poAfter %s %s\n", node.getClass().getSimpleName(), this);
	}
	
	@Override
	public IProofObligationList poBefore(PExp node, IPOContextStack question)
	{
		System.out.printf("poBefore %s %s\n", node.getClass().getSimpleName(), this);
		return new ProofObligationList();
	}

	@Override
	public void poAfter(PExp node, IProofObligationList list, IPOContextStack question)
	{
		System.out.printf("poAfter %s %s\n", node.getClass().getSimpleName(), this);
	}
	
	@Override
	public IProofObligationList poBefore(PStm node, IPOContextStack question)
	{
		System.out.printf("poBefore %s %s\n", node.getClass().getSimpleName(), this);
		return new ProofObligationList();
	}

	@Override
	public void poAfter(PStm node, IProofObligationList list, IPOContextStack question)
	{
		System.out.printf("poAfter %s %s\n", node.getClass().getSimpleName(), this);
	}
	
	@Override
	public IProofObligationList poBefore(AModuleModules node, IPOContextStack question)
	{
		System.out.printf("poBefore %s %s\n", node.getClass().getSimpleName(), this);
		return new ProofObligationList();
	}

	@Override
	public void poAfter(AModuleModules node, IProofObligationList list, IPOContextStack question)
	{
		System.out.printf("poAfter %s %s\n", node.getClass().getSimpleName(), this);
	}
	
	@Override
	public IProofObligationList poBefore(SClassDefinition node, IPOContextStack question)
	{
		System.out.printf("poBefore %s %s\n", node.getClass().getSimpleName(), this);
		return new ProofObligationList();
	}

	@Override
	public void poAfter(SClassDefinition node, IProofObligationList list, IPOContextStack question)
	{
		System.out.printf("poAfter %s %s\n", node.getClass().getSimpleName(), this);
	}
}
