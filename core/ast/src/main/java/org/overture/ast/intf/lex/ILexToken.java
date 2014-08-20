/*
 * #%~
 * The Overture Abstract Syntax Tree
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ast.intf.lex;

import org.overture.ast.lex.VDMToken;
import org.overture.ast.node.INode;

public interface ILexToken extends INode, Comparable<INode>
{

	ILexLocation getLocation();

	VDMToken getType();

	/**
	 * Test whether this token is a given basic type.
	 * 
	 * @param ttype
	 *            The type to test.
	 * @return True if this is of that type.
	 */

	public boolean is(VDMToken ttype);

	/**
	 * Test whether this token is not a given basic type.
	 * 
	 * @param ttype
	 *            The type to test.
	 * @return True if this is not of that type.
	 */

	public boolean isNot(VDMToken ttype);

	ILexToken clone();

}
