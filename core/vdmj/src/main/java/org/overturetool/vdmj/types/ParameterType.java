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

import java.util.Vector;

import org.overturetool.vdmj.definitions.AccessSpecifier;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;


public class ParameterType extends Type
{
	private static final long serialVersionUID = 1L;
	public final LexNameToken name;

	public ParameterType(LexNameToken name)
	{
		super(name.location);
		this.name = name;
	}

	@Override
	public Type typeResolve(Environment env, TypeDefinition root)
	{
		if (resolved) return this; else resolved = true;

		Definition p = env.findName(name, NameScope.NAMES);

		if (p == null || !(p.getType() instanceof ParameterType))
		{
			report(3433, "Parameter type @" + name + " not defined");
		}

		return this;
	}

	@Override
	public boolean narrowerThan(AccessSpecifier accessSpecifier)
	{
		return false;
	}

	@Override
	public Type isType(String typename)
	{
		return new UnknownType(location);
	}

	@Override
	public boolean isType(Class<? extends Type> typeclass)
	{
		return true;
	}

	@Override
	public boolean isUnknown()
	{
		return true;
	}

	@Override
	public boolean isSeq()
	{
		return true;
	}

	@Override
	public boolean isSet()
	{
		return true;
	}

	@Override
	public boolean isMap()
	{
		return true;
	}

	@Override
	public boolean isRecord()
	{
		return true;
	}

	@Override
	public boolean isClass()
	{
		return true;
	}

	@Override
	public boolean isNumeric()
	{
		return true;
	}

	@Override
	public boolean isProduct()
	{
		return true;
	}

	@Override
	public boolean isProduct(int n)
	{
		return true;
	}

	@Override
	public boolean isFunction()
	{
		return true;
	}

	@Override
	public boolean isOperation()
	{
		return true;
	}

	@Override
	public SeqType getSeq()
	{
		return new SeqType(location);	// empty
	}

	@Override
	public SetType getSet()
	{
		return new SetType(location);	// empty
	}

	@Override
	public MapType getMap()
	{
		return new MapType(location);	// Unknown |-> Unknown
	}

	@Override
	public RecordType getRecord()
	{
		return new RecordType(location, new Vector<Field>());
	}

	@Override
	public ClassType getClassType()
	{
		return new ClassType(location, new ClassDefinition());
	}

	@Override
	public NumericType getNumeric()
	{
		return new RealType(location);
	}

	@Override
	public ProductType getProduct()
	{
		return new ProductType(location, new TypeList());
	}

	@Override
	public ProductType getProduct(int n)
	{
		TypeList tl = new TypeList();

		for (int i=0; i<n; i++)
		{
			tl.add(new UnknownType(location));
		}

		return new ProductType(location, tl);
	}

	@Override
	public Type polymorph(LexNameToken pname, Type actualType)
	{
		return (name.equals(pname)) ? actualType : this;
	}

	@Override
	public boolean equals(Object other)
	{
		return true;	// Runtime dependent - assume OK
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	@Override
	public String toDisplay()
	{
		return "@" + name;
	}
}
