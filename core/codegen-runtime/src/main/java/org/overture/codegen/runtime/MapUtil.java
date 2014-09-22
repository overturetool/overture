/*
 * #%~
 * VDM Code Generator Runtime
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
package org.overture.codegen.runtime;

import java.util.LinkedList;
import java.util.Set;


public class MapUtil
{
	public static VDMMap map()
	{
		return new VDMMap();
	}
	
	@SuppressWarnings("unchecked")
	public static VDMMap map(Maplet... elements)
	{
		if(elements == null)
			throw new IllegalArgumentException("Cannot instantiate map from null");

		VDMMap map = map();
		
		if(elements.length == 0)
		{
			return map;
		}
		else
		{
			Maplet firstElement = elements[0];
			map.put(firstElement.getLeft(), firstElement.getRight());
		}
		
		for (int i = 1; i < elements.length; i++)
		{
			Maplet maplet = elements[i];
			
			Object mapletKey = maplet.getLeft();
			Object mapletValue = maplet.getRight();

			if(map.containsKey(mapletKey))
			{
				Object mapValue = map.get(mapletKey);
				
				if (differentValues(mapletValue, mapValue))
					throw new IllegalArgumentException("Duplicate keys that have different values are not allowed");
			}
			
			map.put(mapletKey ,mapletValue);
		}
		
		return map;
	}
	
	private static boolean differentValues(Object leftVal, Object rightVal)
	{
		return (leftVal == null && rightVal != null)
				|| (leftVal != null && !leftVal.equals(rightVal));
	}
	
	@SuppressWarnings("unchecked")
	public static VDMSet dom(VDMMap map)
	{
		if(map == null)
			throw new IllegalArgumentException("Map domain is undefined for null");
		
		VDMSet set = SetUtil.set();
		set.addAll(map.keySet());
			
		return set;
	}
	
	@SuppressWarnings("unchecked")
	public static VDMSet rng(VDMMap map)
	{
		if(map == null)
			throw new IllegalArgumentException("Map range is undefined for null");
		
		VDMSet set = SetUtil.set();
		set.addAll(map.values());
		
		return set;
	}
	
	@SuppressWarnings("unchecked")
	public static VDMMap munion(VDMMap left, VDMMap right)
	{
		if(left == null || right == null)
			throw new IllegalArgumentException("Cannot munion null");
		
		VDMMap result = map();
		
		result.putAll(left);
		
		putAll(result, right);
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public static VDMMap override(VDMMap left, VDMMap right)
	{
		if(left == null || right == null)
			throw new IllegalArgumentException("Cannot override null");
		
		VDMMap result = map();
		
		result.putAll(left);
		result.putAll(right);
		
		return result;
	}
	
	public static VDMMap merge(VDMSet maps)
	{
		if(maps == null)
			throw new IllegalArgumentException("Set of maps to merge cannot be null");
		
		VDMMap result = map();

		for(Object map : maps)
		{
			if(!(map instanceof VDMMap))
				throw new IllegalArgumentException("Only maps can be merged. Got: " + map);
			
			VDMMap vdmMap = (VDMMap) map;
			
			putAll(result, vdmMap);
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static void putAll(VDMMap to, VDMMap from)
	{
		@SuppressWarnings("rawtypes")
		Set fromKeys = from.keySet();

		for(Object fromKey : fromKeys)
		{
			Object fromVal = from.get(fromKey);

			if(to.containsKey(fromKey))
			{
				Object toVal = to.get(fromKey);
				if(differentValues(toVal, fromVal))
					throw new IllegalAccessError("Duplicate keys that have different values are not allowed");
			}
			
			to.put(fromKey, fromVal);		
		}
	}
	
	@SuppressWarnings("unchecked")
	public static VDMMap domResTo(VDMSet domValues, VDMMap map)
	{
		if(domValues == null || map == null)
			throw new IllegalArgumentException("'Domain restrict to' is undefined for null");
		
		VDMMap result = map();
		
		for(Object key : domValues)
		{
			if(map.containsKey(key))
			{
				Object value = map.get(key);
				result.put(key, value);
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static VDMMap domResBy(VDMSet domValues, VDMMap map)
	{
		if(domValues == null || map == null)
			throw new IllegalArgumentException("'Domain restrict by' is undefined for null");
		
		VDMMap result = map();
		
		@SuppressWarnings("rawtypes")
		Set dom = map.keySet();
		
		for(Object key : dom)
		{
			if(!domValues.contains(key))
			{
				Object value = map.get(key);
				result.put(key, value);
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static VDMMap rngResTo(VDMMap map, VDMSet rngValues)
	{
		if(map == null || rngValues == null)
			throw new IllegalArgumentException("'Range restrict to' is undefined for null");
		
		VDMMap result = map();
		
		@SuppressWarnings("rawtypes")
		Set dom = map.keySet();
		
		for(Object key : dom)
		{
			Object value = map.get(key);
			
			if(rngValues.contains(value))
			{
				result.put(key, value);
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static VDMMap rngResBy(VDMMap map, VDMSet rngValues)
	{
		if(map == null || rngValues == null)
			throw new IllegalArgumentException("'Range restrict by' is undefined for null");
		
		VDMMap result = map();
		
		@SuppressWarnings("rawtypes")
		Set dom = map.keySet();
		
		for(Object key : dom)
		{
			Object value = map.get(key);
			
			if(!rngValues.contains(value))
			{
				result.put(key, value);
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static VDMMap inverse(VDMMap map)
	{
		if(map == null)
			throw new IllegalArgumentException("Cannot find the inverse map of null");
		
		VDMMap result = map();
		
		if(map.size() == 0)
		{
			return result;
		}
		
		@SuppressWarnings("rawtypes")
		Set keysSet = map.keySet();
		@SuppressWarnings("rawtypes")
		LinkedList keyList = new LinkedList(keysSet);
		
		Object firstKey = keyList.get(0);
		Object firstValue = map.get(firstKey);
		result.put(firstValue, firstKey);
		
		for (int i = 1; i < keyList.size(); i++)
		{
			Object nextKey = keyList.get(i);
			Object nextValue = map.get(nextKey);
			
			if(result.containsKey(nextKey))
			{
				throw new IllegalArgumentException("Cannot invert non-injective map");
			}
			else
			{
				result.put(nextValue, nextKey);
			}
		}
		
		return result;
	}
	
	public static boolean equals(VDMMap left, VDMMap right)
	{
		if(left == null || right == null)
			throw new IllegalArgumentException("A map cannot be compared to null");
		
		return left.equals(right);
	}
}
