/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.types;

import org.overturetool.vdmj.definitions.AccessSpecifier;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.TypeCheckException;
import org.overturetool.vdmj.util.Utils;

public class FunctionType extends Type
{
	private static final long serialVersionUID = 1L;
	public TypeList parameters;
	public Type result;
	public final boolean partial;

	public FunctionType(LexLocation location,
		boolean partial, TypeList parameters, Type result)
	{
		super(location);
		this.parameters = parameters;
		this.result = result;
		this.partial = partial;
	}

	public FunctionType getPreType()
	{
		FunctionType type =
			new FunctionType(location, false, parameters, new BooleanType(location));
		type.definitions = definitions;
		return type;
	}

	public FunctionType getPostType()
	{
		TypeList params = new TypeList();
		params.addAll(parameters);
		params.add(result);
		FunctionType type =
			new FunctionType(location, false, params, new BooleanType(location));
		type.definitions = definitions;
		return type;
	}

	public FunctionType getCurriedPreType(boolean isCurried)
	{
		if (isCurried && result instanceof FunctionType)
		{
			FunctionType ft = (FunctionType)result;
			FunctionType type = new FunctionType(location,
				false, parameters, ft.getCurriedPreType(isCurried));
			type.definitions = definitions;
			return type;
		}
		else
		{
			return getPreType();
		}
	}

	public FunctionType getCurriedPostType(boolean isCurried)
	{
		if (isCurried && result instanceof FunctionType)
		{
			FunctionType ft = (FunctionType)result;
			FunctionType type = new FunctionType(location,
				false, parameters, ft.getCurriedPostType(isCurried));
			type.definitions = definitions;
			return type;
		}
		else
		{
			return getPostType();
		}
	}

	@Override
	public boolean isFunction()
	{
		return true;
	}

	@Override
	public FunctionType getFunction()
	{
		return this;
	}

	@Override
	public String toDisplay()
	{
		String params = (parameters.isEmpty() ?
						"()" : Utils.listToString(parameters, " * "));
		return "(" + params + (partial ? " -> " : " +> ") + result + ")";
	}

	@Override
	public void unResolve()
	{
		if (!resolved) return; else { resolved = false; }

		for (Type type: parameters)
		{
			type.unResolve();
		}

		result.unResolve();
	}

	@Override
	public FunctionType typeResolve(Environment env, TypeDefinition root)
	{
		if (resolved) return this; else { resolved = true; }

		try
		{
			TypeList fixed = new TypeList();

			for (Type type: parameters)
			{
				fixed.add(type.typeResolve(env, root));
			}

			parameters = fixed;
			result = result.typeResolve(env, root);
			return this;
		}
		catch (TypeCheckException e)
		{
			unResolve();
			throw e;
		}
	}

	@Override
	public boolean equals(Object other)
	{
		other = deBracket(other);

		if (!(other instanceof FunctionType))
		{
			return false;
		}

		FunctionType fo = (FunctionType)other;
		return (partial == fo.partial &&
				result.equals(fo.result) &&
				parameters.equals(fo.parameters));
	}

	@Override
	public int hashCode()
	{
		return parameters.hashCode() + result.hashCode();
	}

	@Override
	public Type polymorph(LexNameToken pname, Type actualType)
	{
		TypeList polyparams = new TypeList();

		for (Type type: parameters)
		{
			polyparams.add(type.polymorph(pname, actualType));
		}

		Type polyresult = result.polymorph(pname, actualType);
		FunctionType type =
			new FunctionType(location, partial, polyparams, polyresult);
		type.definitions = definitions;
		return type;
	}

	@Override
	public boolean narrowerThan(AccessSpecifier accessSpecifier)
	{
		for (Type t: parameters)
		{
			if (t.narrowerThan(accessSpecifier))
			{
				return true;
			}
		}

		return result.narrowerThan(accessSpecifier);
	}
}
