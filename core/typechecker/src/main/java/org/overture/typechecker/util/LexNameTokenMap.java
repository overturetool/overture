package org.overture.typechecker.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.typechecker.LexNameTokenAssistant;

public class LexNameTokenMap<V> implements Map<ILexNameToken, V>, Serializable
{

	
	protected final LexNameTokenAssistant lnt;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1122692848887584905L;


	
	public LexNameTokenMap(LexNameTokenAssistant lnt)
	{
		super();
		this.lnt = lnt;
	}



	

	private final HashMap<LexNameTokenWrapper, V> map = new HashMap<LexNameTokenWrapper, V>();

	public V put(ILexNameToken key, V value)
	{
		return map.put(new LexNameTokenWrapper(key, lnt), value);
	};

	public V get(Object key)
	{
		if (key instanceof ILexNameToken)
		{
			return map.get(new LexNameTokenWrapper((ILexNameToken) key, lnt));
		}
		return map.get(key);
	}

	public Set<Entry<ILexNameToken, V>> entrySet()
	{
		Set<Entry<ILexNameToken, V>> result = new HashSet<Entry<ILexNameToken, V>>();

		for (Entry<LexNameTokenWrapper, V> lexNameTokenEntry : map.entrySet())
		{
			result.add(new LexNameTokenEntry<V>(lexNameTokenEntry));
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
			return this.map.containsKey(new LexNameTokenWrapper((ILexNameToken) key,lnt));
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
			return this.map.remove(new LexNameTokenWrapper((ILexNameToken) key,lnt));
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
		Set<ILexNameToken> result = new HashSet<ILexNameToken>();

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
