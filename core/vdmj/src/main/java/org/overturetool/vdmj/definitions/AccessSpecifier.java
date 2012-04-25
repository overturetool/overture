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

package org.overturetool.vdmj.definitions;

import java.io.Serializable;

import org.overturetool.vdmj.lex.Token;

/**
 * A class to represent a [static] public/private/protected specifier.
 */

public class AccessSpecifier implements Serializable
{
	private static final long serialVersionUID = 1L;

	public final static AccessSpecifier DEFAULT =
		new AccessSpecifier(false, false, Token.PRIVATE);

	public final boolean isStatic;
	public final boolean isAsync;
	public final Token access;

	public AccessSpecifier(boolean isStatic, boolean isAsync, Token access)
	{
		this.isStatic = isStatic;
		this.isAsync = isAsync;
		this.access = access;
	}

	public AccessSpecifier getStatic(boolean asStatic)
	{
		return new AccessSpecifier(asStatic, isAsync, access);
	}

	public boolean narrowerThan(AccessSpecifier other)
	{
		return narrowerThan(other.access);
	}

	public boolean narrowerThan(Token other)
	{
		switch (access)
		{
			case PRIVATE:
				return other != Token.PRIVATE;

			case PROTECTED:
				return other == Token.PUBLIC;

			case PUBLIC:
			default:
				return false;
		}
	}

	@Override
	public String toString()
	{
		return (isAsync? "async " : "") + (isStatic ? "static " : "") + access;
	}

	public String ifSet(String sep)
	{
		return (this == DEFAULT) ? "" : (toString() + sep);
	}
}
