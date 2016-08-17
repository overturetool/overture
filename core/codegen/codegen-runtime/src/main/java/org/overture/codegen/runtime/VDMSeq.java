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

import java.util.ArrayList;
import java.util.Iterator;

@SuppressWarnings("rawtypes")
public class VDMSeq extends ArrayList implements ValueType
{
	private static final long serialVersionUID = 5083307947808060044L;

	@SuppressWarnings("unchecked")
	public VDMSeq copy()
	{
		VDMSeq seqClone = new VDMSeq();

		for (Object element : this)
		{
			if (element instanceof ValueType)
			{
				element = ((ValueType) element).copy();
			}

			seqClone.add(element);
		}

		return seqClone;
	}

	@Override
	public String toString()
	{
		Iterator iterator = this.iterator();

		if (!iterator.hasNext())
		{
			return "[]";
		}

		boolean seqOfChar = true;

		while (iterator.hasNext())
		{
			Object element = iterator.next();
			if (!(element instanceof Character))
			{
				seqOfChar = false;
				break;
			}
		}

		if (seqOfChar)
		{
			StringBuilder sb = new StringBuilder();

			iterator = this.iterator();

			while (iterator.hasNext())
			{
				Object element = iterator.next();

				// Do not use Utils.toString(..) to avoid single quotes
				// around the chars
				sb.append(element);
			}

			return sb.toString();

		} else
		{
			iterator = this.iterator();

			StringBuilder sb = new StringBuilder();

			sb.append('[');

			Object element = iterator.next();
			sb.append(element == this ? "(this Collection)"
					: Utils.toString(element));

			while (iterator.hasNext())
			{
				element = iterator.next();
				sb.append(", ");
				sb.append(element == this ? "(this Collection)"
						: Utils.toString(element));
			}

			sb.append(']');

			return sb.toString();
		}
	}
}
