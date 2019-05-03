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
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * This is the base class for all Annotation implementations. Implementations further
 * implement interfaces (optionally) that are specific to certain analyses, like TCAnnotation for
 * the type checker. The base class just provides access to the AST node, which contains
 * the name of the annotation, and any arguments given.
 */
public abstract class Annotation
{
	protected PAnnotation ast = null;
	private static final Set<Class<?>> declared = new HashSet<Class<?>>(); 
	private static final List<Annotation> instances = new Vector<Annotation>(); 

	protected Annotation()
	{
		declared.add(this.getClass());
		instances.add(this);
	}
	
	public void setAST(PAnnotation ast)
	{
		this.ast = ast;
	}

	public static void init(Class<?> impl, Object... args)	// eg. pass TCAnnotation.class
	{
		for (Class<?> clazz: declared)
		{
			if (impl.isAssignableFrom(clazz))
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
		
		for (Annotation annotation: instances)
		{
			if (impl.isAssignableFrom(annotation.getClass()))
			{
				annotation.doInit(args);
			}			
		}
	}
	
	public static void doInit()
	{
		// Nothing by default
	}

	protected void doInit(Object[] args)
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
