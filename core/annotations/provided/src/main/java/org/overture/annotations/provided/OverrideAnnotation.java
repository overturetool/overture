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
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;
import org.overture.config.Settings;
import org.overture.parser.annotations.ASTAnnotationAdapter;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.annotations.TCAnnotation;

public class OverrideAnnotation extends ASTAnnotationAdapter implements TCAnnotation
{
	public OverrideAnnotation()
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
		check(node);
	}

	@Override
	public void tcBefore(PExp node, TypeCheckInfo question)
	{
		check(node);
	}

	@Override
	public void tcBefore(PStm node, TypeCheckInfo question)
	{
		check(node);
	}

	@Override
	public void tcBefore(AModuleModules node, TypeCheckInfo question)
	{
		check(node);
	}

	@Override
	public void tcBefore(SClassDefinition node, TypeCheckInfo question)
	{
		check(node);
	}

	public void check(INode node)
	{
		if (Settings.dialect == Dialect.VDM_SL)
		{
			TypeChecker.report(6001, "@Override not available in VDM-SL", ast.getName().getLocation());
		}
		
		if (!ast.getArgs().isEmpty())
		{
			TypeChecker.report(6002, "@Override has no arguments", ast.getName().getLocation());
		}
		
		if (node instanceof SFunctionDefinition)
		{
			SFunctionDefinition func = (SFunctionDefinition)node;
			checkdef(func.getName(), func.getClassDefinition());
		}
		else if (node instanceof SOperationDefinition)
		{
			SOperationDefinition op = (SOperationDefinition)node;
			checkdef(op.getName(), op.getClassDefinition());
		}
		else
		{
			TypeChecker.report(6003, "@Override must be a function or operation", ast.getName().getLocation());
		}
	}
	
	private void checkdef(ILexNameToken name, SClassDefinition def)
	{
		if (def != null)
		{
			boolean found = false;
			
			for (PDefinition indef: def.getAllInheritedDefinitions())
			{
				if (indef.getName().getModifiedName(def.getName().getName()).equals(name))
				{
					found = true;
					break;
				}
			}
			
			if (!found)
			{
				TypeChecker.report(6004, "Definition does not @Override superclass", name.getLocation());
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
}
