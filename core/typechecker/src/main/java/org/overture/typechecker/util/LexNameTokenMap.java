package org.overture.typechecker.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;

public class LexNameTokenMap<V> implements Map<ILexNameToken, V>, Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1122692848887584905L;


	static class LexNameTokenEntry<V> implements Map.Entry<ILexNameToken, V>
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
		return getKey()+"="+getValue();
		}
		
	}
	
	static class LexNameTokenWrapper implements Serializable
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
	
	public V put(ILexNameToken key, V value) 
	{
		return map.put(new LexNameTokenWrapper(key), value);		
	};
	
	public V get(Object key)
	{
		if(key instanceof ILexNameToken)
		{
			return map.get(new LexNameTokenWrapper((ILexNameToken) key));
		}
		return map.get(key);
	}
	
	public Set<Entry<ILexNameToken, V>> entrySet()
	{
		Set<Entry<ILexNameToken, V>> result = new HashSet<Entry<ILexNameToken,V>>();
		
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
			sb.append( iterator.next());
			if(iterator.hasNext())
			{
				sb.append("\n");
			}
			
		}
		return sb.toString();
	}
}
