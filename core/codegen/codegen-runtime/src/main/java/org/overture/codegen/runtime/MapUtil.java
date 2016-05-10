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
import java.util.Map;
import java.util.Set;

public class MapUtil
{
	public static VDMMap map()
	{
		return new VDMMap();
	}

	public static Maplet[] toMaplets(Object map)
	{
		validateMap(map, "toMaplets");

		VDMMap vdmMap = (VDMMap) map;

		Maplet[] maplets = new Maplet[vdmMap.size()];

		int nextIndex = 0;
		for (Object o : vdmMap.entrySet())
		{
			Object val = ((Map.Entry) o).getValue();

			maplets[nextIndex++] = new Maplet(((Map.Entry) o).getKey(), val);
		}

		return maplets;
	}

	public static Object get(Object map, Object key)
	{
		validateMap(map, "map read");

		VDMMap vdmMap = (VDMMap) map;

		Object value = vdmMap.get(key);
		if (value != null)
		{
			return value;
		} else
		{
			// The key may map to null
			if (vdmMap.containsKey(key))
			{
				// The key is there
				return null;
			} else
			{
				throw new IllegalArgumentException("No such key in map: " + key);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static VDMSet dom(Object map)
	{
		validateMap(map, "map domain");

		VDMMap vdmMap = (VDMMap) map;

		VDMSet set = SetUtil.set();
		set.addAll(vdmMap.keySet());

		return set;
	}

	@SuppressWarnings("unchecked")
	public static VDMSet rng(Object map)
	{
		validateMap(map, "map range");

		VDMMap vdmMap = (VDMMap) map;

		VDMSet set = SetUtil.set();
		set.addAll(vdmMap.values());

		return set;
	}

	@SuppressWarnings("unchecked")
	public static VDMMap munion(Object left, Object right)
	{
		validateMaps(left, right, "map union");

		VDMMap mapLeft = (VDMMap) left;
		VDMMap mapRight = (VDMMap) right;

		VDMMap result = map();

		result.putAll(mapLeft);

		putAll(result, mapRight);

		return result;
	}

	@SuppressWarnings("unchecked")
	public static void mapAdd(Object map, Object maplet)
	{
		if (!(map instanceof VDMMap))
		{
			throw new IllegalArgumentException("Expected " + map + " to be a " + VDMMap.class.getSimpleName());
		}

		if (!(maplet instanceof Maplet))
		{
			throw new IllegalArgumentException("Expected " + maplet + " to be a " + Maplet.class.getSimpleName());
		}

		VDMMap vdmMap = (VDMMap) map;
		Maplet vdmMaplet = (Maplet) maplet;

		vdmMap.put(vdmMaplet.getLeft(), vdmMaplet.getRight());
	}

	@SuppressWarnings("unchecked")
	public static VDMMap override(Object left, Object right)
	{
		validateMaps(left, right, "map override");

		VDMMap mapLeft = (VDMMap) left;
		VDMMap mapRight = (VDMMap) right;

		VDMMap result = map();

		result.putAll(mapLeft);
		result.putAll(mapRight);

		return result;
	}

	public static VDMMap merge(Object setOfMaps)
	{
		final String MAP_MERGE = "map merge";

		SetUtil.validateSet(setOfMaps, MAP_MERGE);

		VDMSet vdmSetOfMaps = (VDMSet) setOfMaps;

		VDMMap result = map();

		for (Object map : vdmSetOfMaps)
		{
			validateMap(map, MAP_MERGE);

			VDMMap vdmMap = (VDMMap) map;

			putAll(result, vdmMap);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static VDMMap domResTo(Object dom, Object map)
	{
		final String MAP_DOM_RESTRICT_TO = "map domain restrict to";

		SetUtil.validateSet(dom, MAP_DOM_RESTRICT_TO);
		validateMap(map, MAP_DOM_RESTRICT_TO);

		VDMSet vdmDom = (VDMSet) dom;
		VDMMap vdmMap = (VDMMap) map;

		VDMMap result = map();

		for (Object key : vdmDom)
		{
			if (vdmMap.containsKey(key))
			{
				Object value = vdmMap.get(key);
				result.put(key, value);
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static VDMMap domResBy(Object dom, Object map)
	{
		final String MAP_DOM_RESTRICT_BY = "map domain restrict by";

		SetUtil.validateSet(dom, MAP_DOM_RESTRICT_BY);
		validateMap(map, MAP_DOM_RESTRICT_BY);

		VDMSet vdmDom = (VDMSet) dom;
		VDMMap vdmMap = (VDMMap) map;

		VDMMap result = map();

		for (Object o : vdmMap.entrySet())
		{
			if (!vdmDom.contains(((Map.Entry) o).getKey()))
			{
				Object value = ((Map.Entry) o).getValue();
				result.put(((Map.Entry) o).getKey(), value);
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static VDMMap rngResTo(Object map, Object rng)
	{
		final String MAP_RANGE_RESTRICT_TO = "map range restrict to";

		validateMap(map, MAP_RANGE_RESTRICT_TO);
		SetUtil.validateSet(rng, MAP_RANGE_RESTRICT_TO);

		VDMMap vdmMap = (VDMMap) map;
		VDMSet vdmRng = (VDMSet) rng;

		VDMMap result = map();

		@SuppressWarnings("rawtypes")
		Set dom = vdmMap.keySet();

		for (Object o : vdmMap.entrySet())
		{
			Object value = ((Map.Entry) o).getValue();

			if (vdmRng.contains(value))
			{
				result.put(((Map.Entry) o).getKey(), value);
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static VDMMap rngResBy(Object map, Object rng)
	{
		final String MAP_RANGE_RESTRICT_BY = "map range restrict by";

		validateMap(map, MAP_RANGE_RESTRICT_BY);
		SetUtil.validateSet(rng, MAP_RANGE_RESTRICT_BY);

		VDMMap vdmMap = (VDMMap) map;
		VDMSet vdmRng = (VDMSet) rng;

		VDMMap result = map();

		for (Object o : vdmMap.entrySet())
		{
			Object value = ((Map.Entry) o).getValue();

			if (!vdmRng.contains(value))
			{
				result.put(((Map.Entry) o).getKey(), value);
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static VDMMap inverse(Object map)
	{
		validateMap(map, "map inverse");

		VDMMap vdmMap = (VDMMap) map;

		VDMMap result = map();

		if (vdmMap.size() == 0)
		{
			return result;
		}

		@SuppressWarnings("rawtypes")
		Set keysSet = vdmMap.keySet();
		@SuppressWarnings("rawtypes")
		LinkedList keyList = new LinkedList(keysSet);

		Object firstKey = keyList.get(0);
		Object firstValue = vdmMap.get(firstKey);
		result.put(firstValue, firstKey);

		for (int i = 1; i < keyList.size(); i++)
		{
			Object nextKey = keyList.get(i);
			Object nextValue = vdmMap.get(nextKey);

			if (result.containsKey(nextKey))
			{
				throw new IllegalArgumentException("Cannot invert non-injective map");
			} else
			{
				result.put(nextValue, nextKey);
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static VDMMap map(Maplet... elements)
	{
		if (elements == null)
		{
			throw new IllegalArgumentException("Cannot instantiate map from null");
		}

		VDMMap map = map();

		if (elements.length == 0)
		{
			return map;
		} else
		{
			Maplet firstElement = elements[0];
			map.put(firstElement.getLeft(), firstElement.getRight());
		}

		for (int i = 1; i < elements.length; i++)
		{
			Maplet maplet = elements[i];

			Object mapletKey = maplet.getLeft();
			Object mapletValue = maplet.getRight();

			if (map.containsKey(mapletKey))
			{
				Object mapValue = map.get(mapletKey);

				if (differentValues(mapletValue, mapValue))
					throw new IllegalArgumentException("Duplicate keys that have different values are not allowed");
			}

			map.put(mapletKey, mapletValue);
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	private static void putAll(VDMMap to, VDMMap from)
	{
		@SuppressWarnings("rawtypes")
		Set fromKeys = from.keySet();

		for (Object o : from.entrySet())
		{
			Object fromVal = ((Map.Entry) o).getValue();

			if (to.containsKey(((Map.Entry) o).getKey()))
			{
				Object toVal = to.get(((Map.Entry) o).getKey());
				if (differentValues(toVal, fromVal))
					throw new IllegalAccessError("Duplicate keys that have different values are not allowed");
			}

			to.put(((Map.Entry) o).getKey(), fromVal);
		}
	}

	static void validateMap(Object arg, String operator)
	{
		if (!(arg instanceof VDMMap))
		{
			throw new IllegalArgumentException(operator + " is only supported for " + VDMMap.class.getName() + ". Got "
					+ arg);
		}
	}

	private static void validateMaps(Object left, Object right, String operator)
	{
		if (!(left instanceof VDMMap) || !(right instanceof VDMMap))
		{
			throw new IllegalArgumentException(operator + " is only supported for " + VDMMap.class.getName() + ". Got "
					+ left + " and " + right);
		}
	}

	private static boolean differentValues(Object leftVal, Object rightVal)
	{
		return (leftVal == null && rightVal != null) || (leftVal != null && !leftVal.equals(rightVal));
	}
}
