/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.core.model;

public class AtomicVdmType implements IVdmType
{
	private String name;

	public AtomicVdmType(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public boolean isAtomic()
	{
		return true;
	}

	public boolean isComplex()
	{
		return false;
	}

	public boolean isCollection()
	{
		return false;
	}

	public boolean isString()
	{
		return false;
	}

	public String formatDetails(IVdmValue value)
	{
		return formatValue(value);
	}

	public String formatValue(IVdmValue value)
	{
		return value.getRawValue();
	}

	protected void appendInstanceId(IVdmValue value, StringBuffer buffer)
	{
		String id = value.getInstanceId();
		if (id != null)
		{
			buffer.append(" ("); //$NON-NLS-1$
			buffer.append("id");
			buffer.append("="); //$NON-NLS-1$
			buffer.append(id);
			buffer.append(")"); //$NON-NLS-1$
		}
	}
}
