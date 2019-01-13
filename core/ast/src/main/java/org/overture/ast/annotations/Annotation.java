/*******************************************************************************
 *
 *	Copyright (c) 2019 Nick Battle.
 *
 *	Author: Nick Battle
 *
 *	This file is part of Overture
 *
 ******************************************************************************/

package org.overture.ast.annotations;

/**
 * This is the base class for all Annotation implementations. Implementations further
 * implement interfaces (optionally) that are specific to certain analyses, like TCAnnotation for
 * the type checker. The base class just provides access to the AST node, which contains
 * the name of the annotation, and any arguments given.
 */
public abstract class Annotation
{
	protected final PAnnotation ast;

	protected Annotation(PAnnotation ast)
	{
		this.ast = ast;
	}

	@Override
	public String toString()
	{
		return "@" + ast.getName() + (ast.getArgs().isEmpty() ? "" : "(" + ast.getArgs() + ")");
	}
}
