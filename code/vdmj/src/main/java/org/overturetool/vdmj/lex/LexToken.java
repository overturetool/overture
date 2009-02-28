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

package org.overturetool.vdmj.lex;

import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.typechecker.TypeChecker;

/**
 * The parent class for all lexical token types.
 */

abstract public class LexToken
{
	/** The textual location of the token. */
	public final LexLocation location;
	/** The basic type of the token. */
	public final Token type;

	/**
	 * Create a token of the given type at the given location.
	 *
	 * @param location	The location of the token.
	 * @param type		The basic type of the token.
	 */

	public LexToken(LexLocation location, Token type)
	{
		this.location = location;
		this.type = type;
	}

	/**
	 * Test whether this token is a given basic type.
	 *
	 * @param ttype	The type to test.
	 * @return	True if this is of that type.
	 */

	public boolean is(Token ttype)
	{
		return this.type == ttype;
	}

	/**
	 * Test whether this token is not a given basic type.
	 *
	 * @param ttype	The type to test.
	 * @return	True if this is not of that type.
	 */

	public boolean isNot(Token ttype)
	{
		return this.type != ttype;
	}

	@Override
	public String toString()
	{
		return type.toString();
	}

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#report
	 */

	public void report(int number, String msg)
	{
		TypeChecker.report(number, msg, location);
	}

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#warning
	 */

	public void warning(int number, String msg)
	{
		TypeChecker.warning(number, msg, location);
	}

	/**
	 * This is used when a problem would be an error if the type it applies
	 * to is unambiguous, or a warning otherwise.
	 * @param serious True if this should be an error
	 * @param number The error number.
	 * @param msg The problem.
	 */

	public void concern(boolean serious, int number, String msg)
	{
		if (serious)
		{
			TypeChecker.report(number, msg, location);
		}
		else
		{
			TypeChecker.warning(number, msg, location);
		}
	}

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#abort
	 */

	public void abort(int number, String msg, Context ctxt)
	{
		throw new ContextException(number, msg, location, ctxt);
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
