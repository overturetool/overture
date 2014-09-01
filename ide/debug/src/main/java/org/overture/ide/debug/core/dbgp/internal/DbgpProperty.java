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
package org.overture.ide.debug.core.dbgp.internal;

import org.overture.ide.debug.core.dbgp.IDbgpProperty;

public class DbgpProperty implements IDbgpProperty
{

	private final String address;

	private final String name;

	private final String fullName;

	private final String type;

	private final String value;

	private final boolean constant;

	private final int childrenCount;

	private final IDbgpProperty[] availableChildren;

	private final boolean hasChildren;

	private final String key;

	private int page;

	private int pageSize;

	public DbgpProperty(String name, String fullName, String type,
			String value, int childrenCount, boolean hasChildren,
			boolean constant, String key, String address,
			IDbgpProperty[] availableChildren, int page, int pageSize)
	{
		this.name = name;
		this.fullName = fullName;
		this.type = type;
		this.value = value;
		this.address = address;
		this.childrenCount = childrenCount;
		this.availableChildren = availableChildren;
		this.hasChildren = hasChildren;
		this.constant = constant;
		this.key = key;
		this.page = page;
		this.pageSize = pageSize;
	}

	public String getEvalName()
	{
		return fullName;
	}

	public String getName()
	{
		return name;
	}

	public String getType()
	{
		return type;
	}

	public String getValue()
	{
		return value;
	}

	public boolean hasChildren()
	{
		return hasChildren;
	}

	public int getChildrenCount()
	{
		return childrenCount;
	}

	public IDbgpProperty[] getAvailableChildren()
	{
		return (IDbgpProperty[]) availableChildren.clone();
	}

	public boolean isConstant()
	{
		return constant;
	}

	public String toString()
	{
		return "DbgpProperty (Name: " + name + "; Full name: " + fullName //$NON-NLS-1$ //$NON-NLS-2$
				+ "; Type: " + type + "; Value: " + value + " Address: " + address + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	public String getKey()
	{
		return key;
	}

	public int getPage()
	{
		return page;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public String getAddress()
	{
		return address;
	}
}
