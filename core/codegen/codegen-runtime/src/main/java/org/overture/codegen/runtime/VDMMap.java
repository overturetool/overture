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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("rawtypes")
public class VDMMap extends LinkedHashMap implements ValueType
{
	private static final long serialVersionUID = -3288711341768577550L;

	@SuppressWarnings("unchecked")
	public VDMMap copy()
	{
		VDMMap mapClone = new VDMMap();

		Iterator iterator = this.entrySet().iterator();

		while (iterator.hasNext())
		{
			Map.Entry entry = (Map.Entry) iterator.next();

			Object key = entry.getKey();
			Object value = entry.getValue();

			if (key instanceof ValueType)
			{
				key = ((ValueType) key).copy();
			}

			if (value instanceof ValueType)
			{
				value = ((ValueType) value).copy();
			}

			mapClone.put(key, value);
		}

		return mapClone;
	}

	@Override
	public String toString()
	{
		Set entries = this.entrySet();
		Iterator iterator = entries.iterator();

		if (!iterator.hasNext())
		{
			return "{|->}";
		}

		StringBuilder sb = new StringBuilder();
		sb.append('{');

		for (;;)
		{
			Object next = iterator.next();

			if (!(next instanceof Map.Entry))
			{
				continue;
			}

			Map.Entry entry = (Map.Entry) next;

			Object key = entry.getKey();
			Object value = entry.getValue();

			sb.append(key == this ? "(this Collection)" : Utils.toString(key));
			sb.append(" |-> ");
			sb.append(value == this ? "(this Collection)"
					: Utils.toString(value));

			if (!iterator.hasNext())
			{
				return sb.append('}').toString();
			}

			sb.append(", ");
		}
	}
}
