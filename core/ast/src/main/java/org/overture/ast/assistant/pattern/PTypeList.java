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
package org.overture.ast.assistant.pattern;

import java.util.Vector;

import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.types.PType;
import org.overture.ast.util.Utils;

@SuppressWarnings("serial")
public class PTypeList extends Vector<PType>
{

	public PTypeList()
	{
		super();
	}

	public PTypeList(PType act)
	{
		add(act);
	}

	@Override
	public boolean add(PType t)
	{
		return super.add(t);
	}

	public PType getType(ILexLocation location)
	{
		PType result;

		if (this.size() == 1)
		{
			result = iterator().next();
		} else
		{
			result = AstFactory.newAProductType(location, this);
		}

		return result;
	}

	@Override
	public String toString()
	{
		return "(" + Utils.listToString(this) + ")";
	}
}
