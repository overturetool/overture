/*
 * #%~
 * The VDM Type Checker
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
package org.overture.typechecker.util;

import java.io.Serializable;

import org.overture.ast.intf.lex.ILexNameToken;

class LexNameTokenWrapper implements Serializable
{

	/**
		 * 
		 */
	private static final long serialVersionUID = -5420007432629328108L;
	public ILexNameToken token;

	public LexNameTokenWrapper(ILexNameToken token)
	{
		this.token = token;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof LexNameTokenWrapper)
		{
			return HackLexNameToken.isEqual(this.token, ((LexNameTokenWrapper) obj).token);
		}

		return super.equals(obj);
	}

	@Override
	public int hashCode()
	{
		return this.token.hashCode();
	}

	@Override
	public String toString()
	{
		return token.toString();
	}
}
