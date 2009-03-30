/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
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
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.TypeCheckException;

public class BracketType extends Type
{
	private static final long serialVersionUID = 1L;

	public Type type;

	public BracketType(LexLocation location, Type type)
	{
		super(location);
		this.type = type;
	}

	@Override
	public Type isType(String typename)
	{
		return type.isType(typename);
	}

	@Override
	public boolean isType(Class<? extends Type> typeclass)
	{
		return type.isType(typeclass);
	}

	@Override
	public void unResolve()
	{
		if (!resolved) return; else { resolved = false; }
		type.unResolve();
	}

	@Override
	public Type typeResolve(Environment env, TypeDefinition root)
	{
		if (resolved) return type; else { resolved = true; }

		try
		{
			do
			{
				type = type.typeResolve(env, root);
			}
			while (type instanceof BracketType);

			type = type.typeResolve(env, root);
			return type;
		}
		catch (TypeCheckException e)
		{
			unResolve();
			throw e;
		}
	}

	@Override
	public boolean narrowerThan(AccessSpecifier accessSpecifier)
	{
		return type.narrowerThan(accessSpecifier);
	}

	@Override
	public String toDisplay()
	{
		return "(" + type + ")";
	}

	@Override
	public boolean isUnion()
	{
		return type.isUnion();
	}

	@Override
	public boolean isSeq()
	{
		return type.isSeq();
	}

	@Override
	public boolean isSet()
	{
		return type.isSet();
	}

	@Override
	public boolean isMap()
	{
		return type.isMap();
	}

	@Override
	public boolean isRecord()
	{
		return type.isRecord();
	}

	@Override
	public boolean isNumeric()
	{
		return type.isNumeric();
	}

	@Override
	public boolean isProduct()
	{
		return type.isProduct();
	}

	@Override
	public boolean isProduct(int n)
	{
		return type.isProduct(n);
	}

	@Override
	public boolean isFunction()
	{
		return type.isFunction();
	}

	@Override
	public boolean isOperation()
	{
		return type.isOperation();
	}

	@Override
	public UnionType getUnion()
	{
		return type.getUnion();
	}

	@Override
	public SeqType getSeq()
	{
		return type.getSeq();
	}

	@Override
	public SetType getSet()
	{
		return type.getSet();
	}

	@Override
	public MapType getMap()
	{
		return type.getMap();
	}

	@Override
	public RecordType getRecord()
	{
		return type.getRecord();
	}

	@Override
	public NumericType getNumeric()
	{
		return type.getNumeric();
	}

	@Override
	public ProductType getProduct()
	{
		return type.getProduct();
	}

	@Override
	public ProductType getProduct(int n)
	{
		return type.getProduct(n);
	}

	@Override
	public FunctionType getFunction()
	{
		return type.getFunction();
	}

	@Override
	public OperationType getOperation()
	{
		return type.getOperation();
	}

	@Override
	public boolean equals(Object other)
	{
		return type.equals(other);
	}

	@Override
	public int hashCode()
	{
		return type.hashCode();
	}
}
