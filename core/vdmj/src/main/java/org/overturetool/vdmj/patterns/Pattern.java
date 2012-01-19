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
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.Value;

/**
 * The parent type of all patterns.
 */

public abstract class Pattern implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** The textual location of the pattern. */
	public final LexLocation location;

	/** A flag to prevent recursive type resolution problems. */
	boolean resolved = false;

	/**
	 * Create a pattern at the given location.
	 */

	public Pattern(LexLocation location)
	{
		this.location = location;
	}

	@Override
	abstract public String toString();

	/**
	 * Resolve any types that the pattern may use by looking up the type
	 * names in the environment passed.
	 *
	 * @param env The environment to resolve types.
	 */

	public void typeResolve(Environment env)
	{
		resolved = true;
	}

	/**
	 * Clear the recursive type resolution flag. This is a deep clear,
	 * used when recovering from type resolution errors.
	 */

	public void unResolve()
	{
		resolved = false;
	}

	/** Get a list of definitions for the pattern's variables. */
	abstract public DefinitionList getDefinitions(Type type, NameScope scope);

	/** Get a name/value pair list for the pattern's variables. */
	public NameValuePairList getNamedValues(Value expval, Context ctxt)	throws PatternMatchException
	{
		List<IdentifierPattern> ids = findIdentifiers();

		// Go through the list of IDs, marking duplicate names as constrained. This is
		// because we have to permute sets that contain duplicate variables, so that
		// we catch permutations that match constrained values of the variable from
		// elsewhere in the pattern.

		int count = ids.size();

		for (int i=0; i<count; i++)
		{
			LexNameToken iname = ids.get(i).name;

			for (int j=i+1; j<count; j++)
			{
				if (iname.equals(ids.get(j).name))
				{
					ids.get(i).setConstrained(true);
					ids.get(j).setConstrained(true);
				}
			}
		}

		List<NameValuePairList> all = getAllNamedValues(expval, ctxt);
		return all.get(0);		// loose choice here!
	}

	/** Return a list of the contained IdentifierPatterns */
	protected List<IdentifierPattern> findIdentifiers()
	{
		return new Vector<IdentifierPattern>();		// Most have none
	}

	/** Get a name/value pair list for the pattern's variables. */
	protected abstract List<NameValuePairList> getAllNamedValues(Value expval, Context ctxt)
		throws PatternMatchException;

	/** Get the type(s) that could match this pattern. */
	abstract public Type getPossibleType();

	/** Test whether the pattern can match the type passed */
	public boolean matches(Type type)
	{
		return TypeComparator.compatible(getPossibleType(), type);
	}

	/** Get a list of the pattern's variable names. */
	public LexNameList getVariableNames()
	{
		return new LexNameList();	// Most are empty
	}

	/**
	 * @return The "length" of the pattern (eg. sequence and set patterns).
	 */

	public int getLength()
	{
		return 1;	// Most only identify one member
	}

	/**
	 * @return True if the pattern has constraints, such that matching
	 * values should be permuted, where necessary, to find a match.
	 */

	public boolean isConstrained()
	{
		return true;
	}

	/**
	 * An expression that matches the pattern. This is used in
	 * PO generation when parameter patterns have to be passed to pre/post
	 * conditions as arguments. The result is almost the same as toString(),
	 * except for IgnorePatterns, which produce "don't care" variables.
	 *
	 * @return An expression, being a value that matches the pattern.
	 */

	abstract public Expression getMatchingExpression();

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#report
	 */

	public void report(int number, String msg)
	{
		TypeChecker.report(number, msg, location);
	}

	/**
	 * Throw a PatternMatchException with the given message.
	 * @throws PatternMatchException
	 */

	public void patternFail(int number, String msg) throws PatternMatchException
	{
		throw new PatternMatchException(number, msg, location);
	}

	/**
	 * Throw a PatternMatchException with a message from the ValueException.
	 * @throws PatternMatchException
	 */

	public Value patternFail(ValueException ve) throws PatternMatchException
	{
		throw new PatternMatchException(ve.number, ve.getMessage(), location);
	}

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#detail
	 */

	public void detail(String tag, Object obj)
	{
		TypeChecker.detail(tag, obj);
	}

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#detail2
	 */

	public void detail2(String tag1, Object obj1, String tag2, Object obj2)
	{
		TypeChecker.detail2(tag1, obj1, tag2, obj2);
	}
}
