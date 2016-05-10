package org.overture.interpreter.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 * Caching class used for custom serialization of context serialization of transient values
 * 
 * @author kel
 */
class CloneTrancientMemoryCache<T>
{
	static class EntryPair<K, V> implements Map.Entry<K, V>
	{
		public K key;
		public V value;

		public EntryPair(K key, V value)
		{
			this.key = key;
			this.value = value;
		}

		@Override
		public K getKey()
		{
			return key;
		}

		@Override
		public V getValue()
		{
			return value;
		}

		@Override
		public V setValue(V value)
		{
			return this.value = value;
		}

		@Override
		public String toString()
		{
			return key + " -> " + value;
		}
	}

	Map<Integer, EntryPair<Integer, T>> map = new HashMap<>();

	public synchronized int store(T o)
	{
		Integer key = System.identityHashCode(o);
		EntryPair<Integer, T> val = map.get(key);
		if (val == null)
		{
			val = new EntryPair<>(1, o);
			map.put(key, val);
		} else
		{
			val.key++;
		}
		return key;
	}

	public synchronized T load(Integer key)
	{
		EntryPair<Integer, T> val = map.get(key);
		if (val == null)
		{
			throw new RuntimeException("Not possible to decode cashed key");
		} else
		{
			val.key--;
		}

		if (val.key == 0)
		{
			map.remove(key);
		}
		return val.value;
	}
}
