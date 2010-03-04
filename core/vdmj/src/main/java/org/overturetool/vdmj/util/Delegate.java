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

package org.overturetool.vdmj.util;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.patterns.IdentifierPattern;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.values.Value;

public class Delegate implements Serializable
{
	private static final long serialVersionUID = 1L;
	private final String name;
	private final DefinitionList definitions;

	public Delegate(String name, DefinitionList definitions)
	{
		this.name = name;
		this.definitions = definitions;
	}

	private boolean delegateChecked = false;
	private Class<?> delegateClass = null;
	private Map<String, Method> delegateMethods = null;
	private Map<String, LexNameList> delegateArgs = null;

	public boolean hasDelegate()
	{
		if (!delegateChecked)
		{
			delegateChecked = true;

			try
			{
				String classname = name.replace('_', '.');
				delegateClass = ClassLoader.getSystemClassLoader().loadClass(classname);
				delegateMethods = new HashMap<String, Method>();
				delegateArgs = new HashMap<String, LexNameList>();
			}
			catch (ClassNotFoundException e)
			{
				// Fine
			}
		}

		return (delegateClass != null);
	}

	public Object newInstance()
	{
		try
		{
			return delegateClass.newInstance();
		}
		catch (NullPointerException e)
		{
			throw new InternalException(63,
				"No delegate class found: " + name);
		}
		catch (InstantiationException e)
		{
			throw new InternalException(54,
				"Cannot instantiate native object: " + e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			throw new InternalException(55,
				"Cannot access native object: " + e.getMessage());
		}
	}

	private Method getDelegateMethod(String title)
	{
		Method m = delegateMethods.get(title);

		if (m == null)
		{
			PatternList plist = null;
			String mname = title.substring(0, title.indexOf('('));

			for (Definition d: definitions)
			{
				if (d.name.name.equals(mname))
				{
    	 			if (d.isOperation())
    	 			{
    	 				if (d instanceof ExplicitOperationDefinition)
    	 				{
    	 					ExplicitOperationDefinition e = (ExplicitOperationDefinition)d;
    	 					plist = e.parameterPatterns;
    	 				}
    	 				else
    	 				{
    	 					ImplicitOperationDefinition e = (ImplicitOperationDefinition)d;
    	 					plist = e.getParamPatternList();
    	 				}

    	 				break;
    	 			}
    	 			else if (d.isFunction())
    	 			{
    	 				if (d instanceof ExplicitFunctionDefinition)
    	 				{
    	 					ExplicitFunctionDefinition e = (ExplicitFunctionDefinition)d;
    	 					plist = e.paramPatternList.get(0);
    	 				}
    	 				else
    	 				{
    	 					ImplicitFunctionDefinition e = (ImplicitFunctionDefinition)d;
    	 					plist = e.getParamPatternList().get(0);
    	 				}

    	 				break;
    	 			}
				}
			}

			LexNameList anames = new LexNameList();
			List<Class<?>> ptypes = new Vector<Class<?>>();

			if (plist != null)
			{
				for (Pattern p: plist)
				{
					if (p instanceof IdentifierPattern)
					{
						IdentifierPattern ip = (IdentifierPattern)p;
						anames.add(ip.name);
						ptypes.add(Value.class);
					}
					else
					{
						throw new InternalException(56,
							"Native method cannot use pattern arguments: " + title);
					}
				}

				delegateArgs.put(title, anames);
			}
			else
			{
				throw new InternalException(57, "Native member not found: " + title);
			}

			try
			{
				Class<?>[] array = new Class<?>[0];
				m = delegateClass.getMethod(mname, ptypes.toArray(array));

				if (!m.getReturnType().equals(Value.class))
				{
					throw new InternalException(58,
						"Native method does not return Value: " + m);
				}
			}
			catch (SecurityException e)
			{
				throw new InternalException(60,
					"Cannot access native method: " + e.getMessage());
			}
			catch (NoSuchMethodException e)
			{
				throw new InternalException(61,
					"Cannot find native method: " + e.getMessage());
			}

			delegateMethods.put(title, m);
		}

		return m;
	}

	public Value invokeDelegate(Object delegateObject, Context ctxt)
	{
		Method m = getDelegateMethod(ctxt.title);

		if ((m.getModifiers() & Modifier.STATIC) == 0 &&
			delegateObject == null)
		{
			throw new InternalException(64,
				"Native method should be static: " + m.getName());
		}

		LexNameList anames = delegateArgs.get(ctxt.title);
		Object[] avals = new Object[anames.size()];
		int a = 0;

		for (LexNameToken arg: anames)
		{
			avals[a++] = ctxt.get(arg);
		}

		try
		{
			return (Value)m.invoke(delegateObject, avals);
		}
		catch (IllegalArgumentException e)
		{
			throw new InternalException(62,
				"Cannot invoke native method: " + e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			throw new InternalException(62,
				"Cannot invoke native method: " + e.getMessage());
		}
		catch (InvocationTargetException e)
		{
			throw new InternalException(59,
				"Failed in native method: " + e.getTargetException().getMessage());
		}
	}

	/**
	 * The Method objects in the delegateMethods map cannot be serialized,
	 * which means that deep copies fail. So here, we clear the map when
	 * serialization occurs. The map is re-build later on demand.
	 */

	private void writeObject(java.io.ObjectOutputStream out)
		throws IOException
	{
		if (delegateMethods != null)
		{
			delegateMethods.clear();
		}

		out.defaultWriteObject();
	}
}
