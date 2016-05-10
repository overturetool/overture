/*
 * #%~
 * The VDM Type Checker
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.typechecker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class TypeCheckInfo
{
	public final ITypeCheckerAssistantFactory assistantFactory;
	/*
	 * Added by RWL The Context allows us to resolve the following: (AnotherChecker,AnotherQuestion) ->
	 * (OvertureChecker,OvertureQuestion) -> (AnotherChecker, OvertureQuestion) Now the AnotherChecker is aware that
	 * only a OvertureQuestion is available here, however it may need its AnotherQuestion, right, to resolve that we
	 * have this context: (AnotherChecker,AnotherQuestion) -> Create Overture Question with AnotherQuestion in its
	 * context -> (OverutreChecker,OvertureQuestion) work as it should (AnotherChecker,OverTureQuestion) -> Get from the
	 * context the earlier AnotherQuestion. Other things can be added to the context and it may be used for any purpose.
	 * It is not used by Overture. (Remove this comment it is starts getting used by overture !)
	 */
	private static final Map<Class<?>, Object> context = new HashMap<>();

	public static void clearContext()
	{
		context.clear();
	}

	@SuppressWarnings("unchecked")
	private static <T> Stack<T> lookupListForType(Class<T> clz)
	{
		Object o = context.get(clz);
		if (o instanceof List<?>)
		{
			List<?> list = List.class.cast(o);
			if (list.size() > 0)
			{
				Object first = list.get(0);
				if (first != null && clz.isInstance(first))
				{
					return (Stack<T>) list;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the value associated with key. Implementation detail: Notice the map is shared between all instances.
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 */
	public <T> T contextGet(Class<T> key)
	{
		synchronized (TypeCheckInfo.class)
		{
			Stack<T> contextStack = lookupListForType(key);
			if (contextStack != null)
			{
				return contextStack.peek();
			}

		}
		return null;
	}

	/**
	 * Associates the given key with the given value.
	 * 
	 * @param <T>
	 * @param key
	 * @param value
	 */
	public <T> void contextSet(Class<T> key, T value)
	{
		synchronized (TypeCheckInfo.class)
		{
			Stack<T> contextStack = lookupListForType(key);
			if (contextStack == null)
			{
				contextStack = new Stack<>();
				context.put(key, contextStack);
			}
			contextStack.push(value);
		}
	}

	/**
	 * Returns the value associated with key, and removes that binding from the context.
	 * 
	 * @param <T>
	 * @param key
	 * @return value
	 */
	public <T> T contextRem(Class<T> key)
	{
		synchronized (TypeCheckInfo.class)
		{
			Stack<T> contextStack = lookupListForType(key);
			if (contextStack != null)
			{
				return contextStack.pop();
			}
		}
		return null;
	}

	public final Environment env;
	public NameScope scope;
	public LinkedList<PType> qualifiers;
	public final PType constraint; // expressions
	public final PType returnType; // statements

	public TypeCheckInfo(ITypeCheckerAssistantFactory assistantFactory,
			Environment env, NameScope scope, LinkedList<PType> qualifiers,
			PType constraint, PType returnType)
	{
		this.assistantFactory = assistantFactory;
		this.env = env;
		this.scope = scope;
		this.qualifiers = qualifiers;
		this.constraint = constraint;
		this.returnType = returnType;
	}

	public TypeCheckInfo(ITypeCheckerAssistantFactory assistantFactory,
			Environment env, NameScope scope, LinkedList<PType> qualifiers)
	{
		this(assistantFactory, env, scope, qualifiers, null, null);
	}

	public TypeCheckInfo(ITypeCheckerAssistantFactory assistantFactory,
			Environment env, NameScope scope)
	{
		this(assistantFactory, env, scope, null, null, null);
	}

	public TypeCheckInfo(ITypeCheckerAssistantFactory assistantFactory,
			Environment env)
	{
		this(assistantFactory, env, null, null, null, null);
	}

	public TypeCheckInfo()
	{
		this(null, null, null, null, null, null);
	}

	@Override
	public String toString()
	{
		return "Scope: " + scope + "\n" + env;
	}

	public TypeCheckInfo newConstraint(PType newConstraint)
	{
		TypeCheckInfo info = new TypeCheckInfo(assistantFactory, env, scope, qualifiers, newConstraint, returnType);
		return info;
	}

	public TypeCheckInfo newScope(NameScope newScope)
	{
		TypeCheckInfo info = new TypeCheckInfo(assistantFactory, env, newScope, qualifiers, constraint, returnType);
		return info;
	}

	public TypeCheckInfo newScope(List<PDefinition> definitions)
	{
		return newScope(definitions, scope);
	}

	public TypeCheckInfo newInfo(Environment newEnv)
	{
		TypeCheckInfo info = new TypeCheckInfo(assistantFactory, newEnv, scope, qualifiers, constraint, returnType);
		return info;
	}

	public TypeCheckInfo newInfo(Environment newEnv, NameScope newScope)
	{
		TypeCheckInfo info = new TypeCheckInfo(assistantFactory, newEnv, newScope, qualifiers, constraint, returnType);
		return info;
	}

	public TypeCheckInfo newScope(List<PDefinition> definitions,
			NameScope newScope)
	{
		Environment newEnv = new FlatCheckedEnvironment(assistantFactory, definitions, env, newScope);
		TypeCheckInfo info = new TypeCheckInfo(assistantFactory, newEnv, newScope);
		return info;
	}
}
