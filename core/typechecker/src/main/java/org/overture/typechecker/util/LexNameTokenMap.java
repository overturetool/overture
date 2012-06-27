package org.overture.typechecker.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.overture.ast.lex.LexNameToken;

public class LexNameTokenMap<V> implements Map<LexNameToken, V>
{
	
	static class LexNameTokenEntry<V> implements Map.Entry<LexNameToken, V>
	{

		Map.Entry<LexNameTokenWrapper, V> wrapped;
		
		public LexNameTokenEntry(Map.Entry<LexNameTokenWrapper, V> wrapped)
		{
			this.wrapped = wrapped;
		}
		
		public LexNameToken getKey()
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
		return getKey()+"="+getValue();
		}
		
	}
	
	static class LexNameTokenWrapper
	{
		
		public LexNameToken token;

		public LexNameTokenWrapper(LexNameToken token)
		{
			this.token = token;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if(obj instanceof LexNameTokenWrapper)
			{
				return HelpLexNameToken.isEqual(this.token,((LexNameTokenWrapper)obj).token);
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
	
	private final HashMap<LexNameTokenWrapper,V> map = new HashMap<LexNameTokenWrapper, V>();
	
	public V put(LexNameToken key, V value) 
	{
		return map.put(new LexNameTokenWrapper(key), value);		
	};
	
	public V get(Object key)
	{
		if(key instanceof LexNameToken)
		{
			return map.get(new LexNameTokenWrapper((LexNameToken) key));
		}
		return map.get(key);
	}
	
	public Set<Entry<LexNameToken, V>> entrySet()
	{
		Set<Entry<LexNameToken, V>> result = new HashSet<Entry<LexNameToken,V>>();
		
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
		if(key instanceof LexNameToken)
		{
			return this.map.containsKey(new LexNameTokenWrapper((LexNameToken)key));
		}
		return false;
	}

	public boolean containsValue(Object value)
	{
		return this.map.containsValue(value);
	}

	public V remove(Object key)
	{
		if(key instanceof LexNameToken)
		{
			return this.map.remove(new LexNameTokenWrapper((LexNameToken)key));
		}
		return null;
	}

	public void putAll(Map<? extends LexNameToken, ? extends V> m)
	{
		for (Entry<? extends LexNameToken, ? extends V> item : m.entrySet())
		{
			put(item.getKey(), item.getValue());
		}
		
	}

	public void clear()
	{
		this.map.clear();
	}

	public Set<LexNameToken> keySet()
	{
		Set<LexNameToken> result = new HashSet<LexNameToken>();
		
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
		for (Iterator<Entry<LexNameToken, V>> iterator = entrySet().iterator(); iterator.hasNext();)
		{
			sb.append( iterator.next());
			if(iterator.hasNext())
			{
				sb.append("\n");
			}
			
		}
		return sb.toString();
	}
}
