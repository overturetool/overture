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

public class SeqType extends Type
{
	private static final long serialVersionUID = 1L;
	public Type seqof;
	public final boolean empty;

	public SeqType(LexLocation location, Type type)
	{
		super(location);
		this.seqof = type;
		this.empty = false;
	}

	public SeqType(LexLocation location)
	{
		super(location);
		this.seqof = new UnknownType(location);
		this.empty = true;
	}

	@Override
	public boolean narrowerThan(AccessSpecifier accessSpecifier)
	{
		return seqof.narrowerThan(accessSpecifier);
	}

	@Override
	public boolean isSeq()
	{
		return true;
	}

	@Override
	public SeqType getSeq()
	{
		return this;
	}

	@Override
	public void unResolve()
	{
		if (!resolved) return; else { resolved = false; }
		seqof.unResolve();
	}

	@Override
	public Type typeResolve(Environment env, TypeDefinition root)
	{
		if (resolved) return this; else { resolved = true; }

		try
		{
			seqof = seqof.typeResolve(env, root);
			if (root != null) root.infinite = false;	// Could be empty
			return this;
		}
		catch (TypeCheckException e)
		{
			unResolve();
			throw e;
		}
	}

	@Override
	public Type polymorph(LexNameToken pname, Type actualType)
	{
		return new SeqType(location, seqof.polymorph(pname, actualType));
	}

	@Override
	public String toDisplay()
	{
		return empty ? "[]" : "seq of (" + seqof + ")";
	}

	@Override
	public boolean equals(Object other)
	{
		while (other instanceof BracketType)
		{
			other = ((BracketType)other).type;
		}

		if (other instanceof SeqType)
		{
			SeqType os = (SeqType)other;
			// NB. Empty sequence is the same type as any sequence
			return empty || os.empty ||	seqof.equals(os.seqof);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return empty ? 0 : seqof.hashCode();
	}
}
