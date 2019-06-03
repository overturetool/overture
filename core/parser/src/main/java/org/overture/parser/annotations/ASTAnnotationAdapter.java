/*******************************************************************************
 *
 *	Copyright (c) 2019 Nick Battle.
 *
 *	Author: Nick Battle
 *
 *	This file is part of Overture
 *
 ******************************************************************************/

package org.overture.parser.annotations;

import java.util.List;
import java.util.Vector;

import org.overture.ast.annotations.Annotation;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.statements.PStm;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ClassReader;
import org.overture.parser.syntax.DefinitionReader;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ModuleReader;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.syntax.StatementReader;

/**
 * The default implementation of the annotation parser in ASTAnnotation. Most user
 * annotations will extend this class.
 */
public class ASTAnnotationAdapter extends Annotation implements ASTAnnotation
{
	public ASTAnnotationAdapter()
	{
		super();
	}

	protected void parseException(String message, ILexLocation location) throws LexException
	{
		System.err.println("Malformed @Annotation: " + message + " " + location);
		throw new LexException(0, "Malformed @Annotation: " + message, (LexLocation) location);
	}
	
	/**
	 * The default parse for annotations looks for an optional list of expressions in
	 * round brackets. This method can be overridden in particular annotations if the
	 * default syntax is not appropriate. 
	 */
	public List<PExp> parse(LexTokenReader ltr) throws LexException, ParserException
	{
		List<PExp> args = new Vector<PExp>();
		
		if (ltr.nextToken().is(VDMToken.BRA))
		{
			if (ltr.nextToken().isNot(VDMToken.KET))
			{
				ExpressionReader er = new ExpressionReader(ltr);
				args.add(er.readExpression());
		
				while (ltr.getLast().is(VDMToken.COMMA))
				{
					ltr.nextToken();
					args.add(er.readExpression());
				}
			}
	
			if (ltr.getLast().isNot(VDMToken.KET))
			{
				parseException("Malformed @Annotation", ltr.getLast().getLocation());
			}
		}
		
		return args;
	}
	

	@Override
	public void astBefore(DefinitionReader reader)
	{
		// Do nothing
	}

	@Override
	public void astBefore(StatementReader reader)
	{
		// Do nothing
	}

	@Override
	public void astBefore(ExpressionReader reader)
	{
		// Do nothing
	}

	@Override
	public void astBefore(ModuleReader reader)
	{
		// Do nothing
	}

	@Override
	public void astBefore(ClassReader reader)
	{
		// Do nothing
	}

	@Override
	public void astAfter(DefinitionReader reader, PDefinition def)
	{
		// Do nothing
	}

	@Override
	public void astAfter(StatementReader reader, PStm stmt)
	{
		// Do nothing
	}

	@Override
	public void astAfter(ExpressionReader reader, PExp exp)
	{
		// Do nothing
	}

	@Override
	public void astAfter(ModuleReader reader, AModuleModules module)
	{
		// Do nothing
	}

	@Override
	public void astAfter(ClassReader reader, SClassDefinition clazz)
	{
		// Do nothing
	}
}
