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

package org.overturetool.vdmj.modules;

import java.io.Serializable;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.typechecker.TypeChecker;

/**
 * The parent class of all export declarations.
 */

abstract public class Export implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** The textual location of the export statement. */
	public final LexLocation location;

	/**
	 * Create an export declaration at the given location.
	 */

	public Export(LexLocation location)
	{
		this.location = location;
	}

	@Override
	abstract public String toString();

	/** Create a new definition for the export declaration. */
	abstract public DefinitionList getDefinition();

	/** Lookup the actual definition of the export declaration. */
	abstract public DefinitionList getDefinition(DefinitionList actualDefs);

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#report
	 */

	public void report(int number, String msg)
	{
		TypeChecker.report(number, msg, location);
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
