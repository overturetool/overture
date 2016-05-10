/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.interpreter.util;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.ArrayList;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.messages.InternalException;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ExitException;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class Delegate implements Serializable
{
	private static final long serialVersionUID = 1L;
	private final String name;
	private List<PDefinition> definitions;

	public Delegate(String name, List<PDefinition> definitions)
	{
		this.name = name;
		this.definitions = definitions;
	}

	private boolean delegateChecked = false;
	private Class<?> delegateClass = null;
	private Map<String, Method> delegateMethods = null;
	private Map<String, LexNameList> delegateArgs = null;

	public boolean hasDelegate(ITypeCheckerAssistantFactory assistantFactory)
	{
		if (!delegateChecked)
		{
			delegateChecked = true;

			try
			{
				String classname = name.replace('_', '.');
				delegateClass = this.getClass().getClassLoader().loadClass(classname);
				delegateMethods = new HashMap<String, Method>();
				delegateArgs = new HashMap<String, LexNameList>();
				definitions = assistantFactory.createPDefinitionListAssistant().singleDefinitions(definitions);
			} catch (ClassNotFoundException e)
			{
				// Fine
			}
		}

		return delegateClass != null;
	}

	public Object newInstance()
	{
		try
		{
			return delegateClass.newInstance();
		} catch (NullPointerException e)
		{
			throw new InternalException(63, "No delegate class found: " + name);
		} catch (InstantiationException e)
		{
			throw new InternalException(54, "Cannot instantiate native object: "
					+ e.getMessage());
		} catch (IllegalAccessException e)
		{
			throw new InternalException(55, "Cannot access native object: "
					+ e.getMessage());
		}
	}

	// gkanos:added parameters to pass the context as argument.
	private Method getDelegateMethod(String title, Context ctxt)
	{
		Method m = delegateMethods.get(title);

		if (m == null)
		{
			List<PPattern> plist = null;
			String mname = title.substring(0, title.indexOf('('));

			// FIXME: this is to handle inheritance in the same way as VDMJ did. See CSV and IO, where the subclass
			// declared methods is in the tail of the list
			List<PDefinition> defs = new ArrayList<PDefinition>(definitions);
			Collections.reverse(defs);

			for (PDefinition d : defs)
			{
				if (d.getName().getName().equals(mname))
				{
					if (ctxt.assistantFactory.createPDefinitionAssistant().isOperation(d))
					{
						if (d instanceof AExplicitOperationDefinition)
						{
							AExplicitOperationDefinition e = (AExplicitOperationDefinition) d;
							plist = e.getParameterPatterns();
						} else if (d instanceof AImplicitOperationDefinition)
						{
							AImplicitOperationDefinition e = (AImplicitOperationDefinition) d;
							plist = ctxt.assistantFactory.createAImplicitOperationDefinitionAssistant().getParamPatternList(e);
						}

						break;
					} else if (ctxt.assistantFactory.createPDefinitionAssistant().isFunction(d))
					{
						if (d instanceof AExplicitFunctionDefinition)
						{
							AExplicitFunctionDefinition e = (AExplicitFunctionDefinition) d;
							plist = e.getParamPatternList().get(0);
						} else if (d instanceof AImplicitFunctionDefinition)
						{
							AImplicitFunctionDefinition e = (AImplicitFunctionDefinition) d;
							plist = ctxt.assistantFactory.createAImplicitFunctionDefinitionAssistant().getParamPatternList(e).get(0);
						}

						break;
					}
				}
			}

			LexNameList anames = new LexNameList();
			List<Class<?>> ptypes = new ArrayList<Class<?>>();

			if (plist != null)
			{
				for (PPattern p : plist)
				{
					if (p instanceof AIdentifierPattern)
					{
						AIdentifierPattern ip = (AIdentifierPattern) p;
						anames.add(ip.getName());
						ptypes.add(Value.class);
					} else
					{
						throw new InternalException(56, "Native method cannot use pattern arguments: "
								+ title);
					}
				}

				delegateArgs.put(title, anames);
			} else
			{
				throw new InternalException(57, "Native member not found: "
						+ title);
			}

			try
			{
				Class<?>[] array = new Class<?>[0];
				m = delegateClass.getMethod(mname, ptypes.toArray(array));

				if (!m.getReturnType().equals(Value.class))
				{
					throw new InternalException(58, "Native method does not return Value: "
							+ m);
				}
			} catch (SecurityException e)
			{
				throw new InternalException(60, "Cannot access native method: "
						+ e.getMessage());
			} catch (NoSuchMethodException e)
			{
				throw new InternalException(61, "Cannot find native method: "
						+ e.getMessage());
			}

			delegateMethods.put(title, m);
		}

		return m;
	}

	public Value invokeDelegate(Object delegateObject, Context ctxt)
	{
		Method m = getDelegateMethod(ctxt.title, ctxt);

		if ((m.getModifiers() & Modifier.STATIC) == 0 && delegateObject == null)
		{
			throw new InternalException(64, "Native method should be static: "
					+ m.getName());
		}

		LexNameList anames = delegateArgs.get(ctxt.title);
		Object[] avals = new Object[anames.size()];
		int a = 0;

		for (ILexNameToken arg : anames)
		{
			avals[a++] = ctxt.get(arg);
		}

		try
		{
			return (Value) m.invoke(delegateObject, avals);
		} catch (IllegalArgumentException e)
		{
			throw new InternalException(62, "Cannot invoke native method: "
					+ e.getMessage());
		} catch (IllegalAccessException e)
		{
			throw new InternalException(62, "Cannot invoke native method: "
					+ e.getMessage());
		} catch (InvocationTargetException e)
		{
			if (e.getTargetException() instanceof ExitException)
			{
				throw (ExitException) e.getTargetException();
			}
			if (e.getTargetException() instanceof ContextException)
			{
				throw (ContextException) e.getTargetException();
			}
			// if(e.getTargetException() instanceof ValueException)
			// {
			// throw (ValueException)e.getTargetException();
			// }
			throw new InternalException(59, "Failed in native method: "
					+ e.getTargetException().getMessage());
		}
	}

	/**
	 * The Method objects in the delegateMethods map cannot be serialized, which means that deep copies fail. So here,
	 * we clear the map when serialization occurs. The map is re-build later on demand.
	 */

	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		if (delegateMethods != null)
		{
			delegateMethods.clear();
		}

		out.defaultWriteObject();
	}
}
