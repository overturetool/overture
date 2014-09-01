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

import java.util.Map;

import org.overture.ast.intf.lex.ILexNameToken;

class LexNameTokenEntry<V> implements Map.Entry<ILexNameToken, V>
{

	Map.Entry<LexNameTokenWrapper, V> wrapped;

	public LexNameTokenEntry(Map.Entry<LexNameTokenWrapper, V> wrapped)
	{
		this.wrapped = wrapped;
	}

	public ILexNameToken getKey()
	{
		return wrapped.getKey().token;
	}

	public V getValue()
	{
		return wrapped.getValue();
	}

	public V setValue(V value)
	{
		return wrapped.setValue(value);
	}

	@Override
	public String toString()
	{
		return getKey() + "=" + getValue();
	}

}
