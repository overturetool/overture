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
import java.util.LinkedHashSet;

@SuppressWarnings("rawtypes")
public class VDMSet extends LinkedHashSet implements ValueType
{
	private static final long serialVersionUID = 2984495719595419443L;

	@SuppressWarnings("unchecked")
	public VDMSet copy()
	{
		VDMSet setClone = new VDMSet();

		for (Object element : this)
		{
			if (element instanceof ValueType)
			{
				element = ((ValueType) element).copy();
			}

			setClone.add(element);
		}

		return setClone;
	}

	@Override
	public String toString()
	{
		Iterator iterator = this.iterator();

		if (!iterator.hasNext())
		{
			return "{}";
		}

		StringBuilder sb = new StringBuilder();

		sb.append('{');

		for (;;)
		{
			Object element = iterator.next();

			sb.append(element == this ? "(this Collection)"
					: Utils.toString(element));

			if (!iterator.hasNext())
			{
				return sb.append('}').toString();
			}

			sb.append(',').append(' ');
		}
	}
}
