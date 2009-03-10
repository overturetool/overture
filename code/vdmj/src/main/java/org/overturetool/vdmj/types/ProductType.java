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

public class ProductType extends Type
{
	private static final long serialVersionUID = 1L;
	public TypeList types = null;

	public ProductType(LexLocation location, Type a, Type b)
	{
		super(location);
		types = new TypeList();
		types.add(a);
		types.add(b);
	}

	public ProductType(LexLocation location, TypeList types)
	{
		super(location);
		this.types = types;
	}

	@Override
	public boolean narrowerThan(AccessSpecifier accessSpecifier)
	{
		for (Type t: types)
		{
			if (t.narrowerThan(accessSpecifier))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public Type polymorph(LexNameToken pname, Type actualType)
	{
		TypeList polytypes = new TypeList();

		for (Type type: types)
		{
			polytypes.add(type.polymorph(pname, actualType));
		}

		return new ProductType(location, polytypes);
	}

	@Override
	public boolean isProduct()
	{
		return true;
	}

	@Override
	public boolean isProduct(int n)
	{
		return n == 0 || types.size() == n;
	}

	@Override
	public ProductType getProduct()
	{
		return this;
	}

	@Override
	public ProductType getProduct(int n)
	{
		return n == 0 || types.size() == n ? this : null;
	}

	@Override
	public void unResolve()
	{
		for (Type t: types)
		{
			t.unResolve();
		}

		resolved = false;
	}

	@Override
	public ProductType typeResolve(Environment env, TypeDefinition root)
	{
		if (resolved) return this; else { resolved = true; }

		try
		{
			TypeList fixed = new TypeList();

			for (Type t: types)
			{
				Type rt = t.typeResolve(env, root);
				fixed.add(rt);
			}

			types = fixed;
			return this;
		}
		catch (TypeCheckException e)
		{
			unResolve();
			throw e;
		}
	}

	@Override
	public String toDisplay()
	{
		return Utils.listToString("(", types, " * ", ")");
	}

	@Override
	public boolean equals(Object other)
	{
		while (other instanceof BracketType)
		{
			other = ((BracketType)other).type;
		}

		if (other instanceof ProductType)
		{
			ProductType pother = (ProductType)other;
			return this.types.equals(pother.types);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return types.hashCode();
	}
}
