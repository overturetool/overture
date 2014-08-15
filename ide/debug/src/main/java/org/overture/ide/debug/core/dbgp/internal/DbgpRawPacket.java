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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.osgi.util.NLS;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.IDbgpRawPacket;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlParser;
import org.w3c.dom.Document;

public class DbgpRawPacket implements IDbgpRawPacket
{

	protected static int readPacketSize(InputStream input) throws IOException
	{
		int size = 0;
		for (;;)
		{
			int b = input.read();
			if (b == -1)
			{
				throw new IOException();
			}
			if (b == 0)
			{
				break;
			}
			if (b >= '0' && b <= '9')
			{
				size = size * 10 + b - '0';
			} else
			{
				final String msg = NLS.bind("invalidCharInPacketSize", Integer.toString(b));
				VdmDebugPlugin.logWarning(msg);
				throw new IOException(msg);
			}
		}
		if (size == 0)
		{
			throw new IOException("zeroPacketSize");
		}
		return size;
	}

	protected static byte[] readPacketXml(InputStream input, int size)
			throws IOException
	{
		byte[] bytes = new byte[size];

		int offset = 0;
		int n;
		while (offset < size
				&& (n = input.read(bytes, offset, size - offset)) != -1)
		{
			offset += n;
		}

		if (offset != size)
		{
			throw new IOException("cantReadPacketBody");
		}

		if (input.read() != 0)
		{
			throw new IOException("noTerminationByte");
		}

		return bytes;
	}

	public static DbgpRawPacket readPacket(InputStream input)
			throws IOException
	{
		int size = readPacketSize(input);
		byte[] xml = readPacketXml(input, size);
		return new DbgpRawPacket(size, xml);
	}

	private final int size;

	private final byte[] xml;

	protected DbgpRawPacket(int size, byte[] xml)
	{
		this.size = size;
		this.xml = xml;
	}

	public int getSize()
	{
		return size;
	}

	public byte[] getXml()
	{
		return xml;
	}

	public Document getParsedXml() throws DbgpException
	{
		return DbgpXmlParser.parseXml(xml);
	}

	public String toString()
	{
		return "DbgpPacket (" + size + " bytes) " + xml; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getPacketAsString()
	{
		try
		{
			return new String(xml, "ASCII"); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e)
		{
			return new String(xml);
		}
	}
}
