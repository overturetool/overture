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

package org.overturetool.vdmj.patterns;

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.ValueList;


public class TypeBind extends Bind
{
	private static final long serialVersionUID = 1L;
	public Type type;

	public TypeBind(Pattern pattern, Type type)
	{
		super(pattern.location, pattern);
		this.type = type;
	}

	public void typeResolve(Environment env)
	{
		type = type.typeResolve(env, null);
	}

	@Override
	public List<MultipleBind> getMultipleBindList()
	{
		PatternList plist = new PatternList();
		plist.add(pattern);
		List<MultipleBind> mblist = new Vector<MultipleBind>();
		mblist.add(new MultipleTypeBind(plist, type));
		return mblist;
	}

	@Override
	public String toString()
	{
		return pattern + ":" + type;
	}

	@Override
	public ValueList getBindValues(Context ctxt)
	{
		return type.getAllValues();
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		return new ProofObligationList();
	}

	@Override
	public ValueList getValues(Context ctxt)
	{
		return new ValueList();
	}
}
