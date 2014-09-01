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

import org.overture.ide.debug.core.dbgp.IDbgpFeature;

public class DbgpFeature implements IDbgpFeature
{
	private final boolean supported;

	private final String name;

	private final String value;

	public DbgpFeature(boolean supported, String name, String value)
	{
		this.supported = supported;
		this.name = name;
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	public String getName()
	{
		return name;
	}

	public boolean isSupported()
	{
		return supported;
	}

	public String toString()
	{
		return "DbgpFeature (name: " + name + "; value: " + value //$NON-NLS-1$ //$NON-NLS-2$
				+ "; supported: " + supported + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
