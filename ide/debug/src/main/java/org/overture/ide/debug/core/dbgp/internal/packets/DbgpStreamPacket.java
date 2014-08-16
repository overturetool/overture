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
package org.overture.ide.debug.core.dbgp.internal.packets;

import org.w3c.dom.Element;

public class DbgpStreamPacket extends DbgpPacket
{
	private static final String STDERR = "stderr"; //$NON-NLS-1$

	private static final String STDOUT = "stdout"; //$NON-NLS-1$

	private final String type;

	private final String textContent;

	public DbgpStreamPacket(String type, String textContent, Element content)
	{
		super(content);

		if (!STDERR.equalsIgnoreCase(type) && !STDOUT.equalsIgnoreCase(type))
		{
			throw new IllegalArgumentException("invalidTypeValue");
		}

		if (textContent == null)
		{
			throw new IllegalArgumentException("contentCannotBeNull");
		}

		this.type = type;
		this.textContent = textContent;
	}

	public boolean isStdout()
	{
		return STDOUT.equalsIgnoreCase(type);
	}

	public boolean isStderr()
	{
		return STDERR.equalsIgnoreCase(type);
	}

	public String getTextContent()
	{
		return textContent;
	}

	public String toString()
	{
		return "DbgpStreamPacket (Type: " + type + "; Content: " + textContent //$NON-NLS-1$ //$NON-NLS-2$
				+ ";)"; //$NON-NLS-1$
	}
}
