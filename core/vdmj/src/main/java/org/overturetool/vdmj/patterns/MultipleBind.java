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

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

/**
 * The parent class of {@link MultipleSetBind} and {@link MultipleTypeBind}.
 */

public abstract class MultipleBind implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** The textual location of the bind. */
	public final LexLocation location;
	/** The list of patterns for this bind. */
	public final PatternList plist;

	/**
	 * Create a multiple bind with the given pattern list. The location is
	 * taken from the location of the first pattern in the list.
	 */

	public MultipleBind(PatternList plist)
	{
		this.plist = plist;
		this.location = plist.get(0).location;
	}

	/** Return this one bind as a list of {@link MultipleBind}. */
	public List<MultipleBind> getMultipleBindList()
	{
		List<MultipleBind> list = new Vector<MultipleBind>();
		list.add(this);
		return list;
	}

	/** Perform a type check of the bind. */
	abstract public Type typeCheck(Environment base, NameScope scope);

	/**
	 * Get a list of definitions for the variables in the pattern list.
	 *
	 * @param type The type of the bind.
	 * @return A list of definitions for all the patterns' variables.
	 */

	public DefinitionList getDefinitions(Type type, NameScope scope)
	{
		DefinitionList defs = new DefinitionList();

		for (Pattern p: plist)
		{
			defs.addAll(p.getDefinitions(type, scope));
		}

		return defs;
	}

	/** Get a list of all the possible values to bind the variables to. */ 
	abstract public ValueList getBindValues(Context ctxt) throws ValueException;

	/** Get a list of POs. */
	abstract public ProofObligationList getProofObligations(POContextStack ctxt);

	/** Return a list of all values read by the bind evaluation. */
	abstract public ValueList getValues(Context ctxt);


	public Type getPossibleType()
	{
		return plist.getPossibleType(location);
	}

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#abort
	 */

	public Value abort(ValueException ve)
	{
		throw new ContextException(ve, location);
	}
}
