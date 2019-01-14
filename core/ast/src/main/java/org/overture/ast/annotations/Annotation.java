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

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * This is the base class for all Annotation implementations. Implementations further
 * implement interfaces (optionally) that are specific to certain analyses, like TCAnnotation for
 * the type checker. The base class just provides access to the AST node, which contains
 * the name of the annotation, and any arguments given.
 */
public abstract class Annotation
{
	protected final PAnnotation ast;
	private static final Set<Class<?>> declared = new HashSet<Class<?>>(); 

	protected Annotation(PAnnotation ast)
	{
		this.ast = ast;
		declared.add(this.getClass());
	}

	public static void init()
	{
		for (Class<?> clazz: declared)
		{
			try
			{
				Method doInit = clazz.getMethod("doInit", (Class<?>[])null);
				doInit.invoke(null, (Object[])null);
			}
			catch (Throwable e)
			{
				throw new RuntimeException(clazz.getSimpleName() + ":" + e);
			}
		}
	}
	
	public static void doInit()
	{
		// Nothing by default
	}

	@Override
	public String toString()
	{
		return "@" + ast.getName() + (ast.getArgs().isEmpty() ? "" : "(" + ast.getArgs() + ")");
	}
	
	public boolean typecheckArgs()
	{
		return true;		// By default, type check the args
	}
}
