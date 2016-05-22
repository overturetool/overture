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

package org.overture.typechecker;

import java.util.List;
import java.util.Vector;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;

/**
 * A type checking exception.
 */

@SuppressWarnings("serial")
public class TypeCheckException extends RuntimeException
{
	public final ILexLocation location;
	public final INode node;
	public List<TypeCheckException> extras = null;

	public TypeCheckException(String msg, ILexLocation location, INode node)
	{
		super(msg);
		this.location = location;
		this.node = node;
	}
	
	public void addExtra(TypeCheckException e)
	{
		if (extras == null)
		{
			extras = new Vector<TypeCheckException>();
		}
		
		extras.add(e);
		
		if (e.extras != null)
		{
			extras.addAll(e.extras);
		}
	}
}
