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
package org.overture.ide.debug.core.dbgp.internal.utils;

import java.io.UnsupportedEncodingException;

import org.overture.ide.debug.core.VdmDebugPlugin;

public class Base64Helper
{

	/**
	 * Encoding of the data
	 */
	private static final String DATA_ENCODING = "UTF-8"; //$NON-NLS-1$

	/**
	 * Encoding of the base64 digits - to be used instead of the default encoding.
	 */
	private static final String BYTE_ENCODING = "ISO-8859-1"; //$NON-NLS-1$

	/**
	 * Empty string constant
	 */
	private static final String EMPTY = ""; //$NON-NLS-1$

	public static String encodeString(String s)
	{
		if (s != null && s.length() != 0)
		{
			try
			{
				final byte[] encode = Base64.encode(s.getBytes(DATA_ENCODING));
				return new String(encode, BYTE_ENCODING);
			} catch (UnsupportedEncodingException e)
			{
				VdmDebugPlugin.log(e);
			}
		}
		return EMPTY;
	}

	public static String decodeString(String base64)
	{
		if (base64 != null && base64.length() != 0)
		{
			try
			{
				final byte[] bytes = base64.getBytes(BYTE_ENCODING);
				final int length = discardWhitespace(bytes);
				if (length > 0)
				{
					final int decodedLength = Base64.decodeInlplace(bytes, length);
					return new String(bytes, 0, decodedLength, DATA_ENCODING);
				}
			} catch (UnsupportedEncodingException e)
			{
				VdmDebugPlugin.log(e);
			}
		}
		return EMPTY;
	}

	/**
	 * Discards any whitespace from a base-64 encoded block. The base64 data in responses could be chunked in the
	 * multiple lines, so we need to remove extra whitespaces. The bytes are copied in-place and the length of the
	 * actual data bytes is returned.
	 * 
	 * @param bytes
	 * @return
	 */
	private static int discardWhitespace(byte[] data)
	{
		final int length = data.length;
		int i = 0;
		while (i < length)
		{
			byte c = data[i++];
			if (c == (byte) ' ' || c == (byte) '\n' || c == (byte) '\r'
					|| c == (byte) '\t')
			{
				int count = i - 1;
				while (i < length)
				{
					c = data[i++];
					if (c != (byte) ' ' && c != (byte) '\n' && c != (byte) '\r'
							&& c != (byte) '\t')
					{
						data[count++] = c;
					}
				}
				return count;
			}
		}
		return length;
	}

	public static String encodeBytes(byte[] bytes)
	{
		return new String(Base64.encode(bytes));
	}

	public static byte[] decodeBytes(String base64)
	{
		return Base64.decode(base64.getBytes());
	}
}
