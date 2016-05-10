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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.overture.ast.intf.lex.ILexNameToken;

public class LexNameTokenMap<V> implements Map<ILexNameToken, V>, Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1122692848887584905L;

	private final HashMap<LexNameTokenWrapper, V> map = new HashMap<>();

	public V put(ILexNameToken key, V value)
	{
		return map.put(new LexNameTokenWrapper(key), value);
	};

	public V get(Object key)
	{
		if (key instanceof ILexNameToken)
		{
			return map.get(new LexNameTokenWrapper((ILexNameToken) key));
		}
		return map.get(key);
	}

	public Set<Entry<ILexNameToken, V>> entrySet()
	{
		Set<Entry<ILexNameToken, V>> result = new HashSet<>();

		for (Entry<LexNameTokenWrapper, V> lexNameTokenEntry : map.entrySet())
		{
			result.add(new LexNameTokenEntry<>(lexNameTokenEntry));
		}

		return result;
	}

	public int size()
	{
		return this.map.size();
	}

	public boolean isEmpty()
	{
		return this.map.isEmpty();
	}

	public boolean containsKey(Object key)
	{
		if (key instanceof ILexNameToken)
		{
			return this.map.containsKey(new LexNameTokenWrapper((ILexNameToken) key));
		}
		return false;
	}

	public boolean containsValue(Object value)
	{
		return this.map.containsValue(value);
	}

	public V remove(Object key)
	{
		if (key instanceof ILexNameToken)
		{
			return this.map.remove(new LexNameTokenWrapper((ILexNameToken) key));
		}
		return null;
	}

	public void putAll(Map<? extends ILexNameToken, ? extends V> m)
	{
		for (Entry<? extends ILexNameToken, ? extends V> item : m.entrySet())
		{
			put(item.getKey(), item.getValue());
		}

	}

	public void clear()
	{
		this.map.clear();
	}

	public Set<ILexNameToken> keySet()
	{
		Set<ILexNameToken> result = new HashSet<>();

		for (LexNameTokenWrapper item : this.map.keySet())
		{
			result.add(item.token);
		}

		return result;
	}

	public Collection<V> values()
	{
		return this.map.values();
	}

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for (Iterator<Entry<ILexNameToken, V>> iterator = entrySet().iterator(); iterator.hasNext();)
		{
			sb.append(iterator.next());
			if (iterator.hasNext())
			{
				sb.append("\n");
			}

		}
		return sb.toString();
	}
}
