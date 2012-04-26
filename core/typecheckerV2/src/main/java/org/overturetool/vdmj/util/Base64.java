/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overturetool.vdmj.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class to manage base64 encoding and decoding.
 */

public class Base64
{
	/**
	 * Return the 6-bit value corresponding to a base64 encoded char. If the
	 * character is the base64 padding character, '=', -1 is returned.
	 *
	 * @param c
	 *            The character to decode
	 * @return The decoded 6-bit value, or -1 for padding.
	 */

	private static int b64decode(char c)
	{
		int i = c;

		if (i >= 'A' && i <= 'Z')
		{
			return i - 'A';
		}

		if (i >= 'a' && i <= 'z')
		{
			return i - 'a' + 26;
		}

		if (i >= '0' && i <= '9')
		{
			return i - '0' + 52;
		}

		if (i == '+')
		{
			return 62;
		}

		if (i == '/')
		{
			return 63;
		}

		return -1;		// padding '='
	}

	/**
	 * Encode a 6-bit quantity as a base64 character.
	 *
	 * @param b	The 6-bit quantity.
	 * return	The character.
	 */

	private static char b64encode(int b)
	{
		if (b >= 0 && b <= 25)
		{
			return (char)('A' + b);
		}

		if (b>=26 && b<= 51)
		{
			return (char)('a' + b - 26);
		}

		if (b >= 52 && b <= 61)
		{
			return (char)('0' + b - 52);
		}

		if (b == 62)
		{
			return '+';
		}

		if (b == 63)
		{
			return '/';
		}

		return '?';
	}

	/**
	 * Base64 decode a string into a byte array.
	 *
	 * @param text
	 *            The encoded base64 text.
	 * @return a byte array of the decoded data.
	 * @throws Exception
	 */

	public static byte[] decode(String text) throws Exception
	{
		if (text.length()%4 != 0)
		{
			throw new Exception("Base64 not a multiple of 4 bytes");
		}

		byte[] result = new byte[text.length()/4 * 3];
		int p = 0;

		for (int i=0; i<text.length();)
		{
			if (text.charAt(i) == '\n')
			{
				continue;
			}

			int b1 = b64decode(text.charAt(i++));
			int b2 = b64decode(text.charAt(i++));
			int b3 = b64decode(text.charAt(i++));
			int b4 = b64decode(text.charAt(i++));

			if (b4 >= 0)
			{
				int three = b4 | (b3 << 6) | (b2 << 12) | (b1 << 18);
				result[p++] = (byte)((three & 0xff0000) >> 16);
				result[p++] = (byte)((three & 0xff00) >> 8);
				result[p++] = (byte)(three & 0xff);
			}
			else if (b3 >= 0)
			{
				int two = (b3 << 6) | (b2 << 12) | (b1 << 18);
				result[p++] = (byte)((two & 0xff0000) >> 16);
				result[p++] = (byte)((two & 0xff00) >> 8);
			}
			else
			{
				int one = (b2 << 12) | (b1 << 18);
				result[p++] = (byte)((one & 0xff0000) >> 16);
			}
		}

		byte[] output = new byte[p];
		System.arraycopy(result, 0, output, 0, p);

		return output;
	}

	/**
	 * Base64 encode a byte array.
	 *
	 * @param data	the data to encode.
	 * @return a StringBuffer containing the encoded lines.
	 */

	public static StringBuffer encode(byte[] data)
	{
		int rem = data.length % 3;
		int num = data.length / 3;
		StringBuffer result = new StringBuffer();
		int p=0;
		int c=0;

		for (int i=0; i<num; i++)
		{
			int b1 = (data[p] & 0xfc) >> 2;
			int b2 = ((data[p] & 0x03) << 4) | ((data[p+1] & 0xf0) >> 4);
			int b3 = ((data[p+1] & 0x0f) << 2) | (data[p+2] & 0xc0) >> 6;
			int b4 = (data[p+2] & 0x3f);

			result.append(b64encode(b1));
			result.append(b64encode(b2));
			result.append(b64encode(b3));
			result.append(b64encode(b4));

			p += 3;
			c += 4;
		}

		switch (rem)
		{
			case 0:
				break;

			case 1:
			{
				int b1 = (data[p] & 0xfc) >> 2;
				int b2 = (data[p] & 0x03) << 4;

				result.append(b64encode(b1));
				result.append(b64encode(b2));
				result.append('=');
				result.append('=');
				break;
			}

			case 2:
			{
				int b1 = (data[p] & 0xfc) >> 2;
				int b2 = ((data[p] & 0x03) << 4) | ((data[p+1] & 0xf0) >> 4);
				int b3 = (data[p+1] & 0x0f) << 2;

				result.append(b64encode(b1));
				result.append(b64encode(b2));
				result.append(b64encode(b3));
				result.append('=');
				break;
			}
		}

		return result;
	}

	public static void main(String[] args) throws Exception
	{
		BufferedReader bir = new BufferedReader(new InputStreamReader(System.in));
		String charset = Charset.defaultCharset().name();
		System.out.println("Default charset = " + charset);
		Pattern pattern = Pattern.compile("(\\w+)\\s*?(.*)?$");

		while (true)
		{
			System.out.print("> ");
			String line = bir.readLine();
			Matcher m = pattern.matcher(line);

			if (!m.matches())
			{
    			System.out.println("[encode|decode] <string>");
    			System.out.println("charset <name>");
    			System.out.println("quit");
    			continue;
			}

			String cmd = m.group(1);
			String data = m.groupCount() == 2 ? m.group(2) : "";

    		if (cmd.equals("decode"))
    		{
    			try
				{
					System.out.println(new String(decode(data)));
				}
				catch (Exception e)
				{
					System.out.println("Oops! " + e.getMessage());
				}
    		}
    		else if (cmd.equals("encode"))
    		{
    			System.out.println(encode(data.getBytes(charset)));
    		}
    		else if (cmd.equals("charset"))
    		{
    			charset = data;
    			System.out.println("Charset now " + charset);
    		}
    		else if (cmd.equals("quit"))
    		{
    			break;
    		}
		}

		bir.close();
	}
}
