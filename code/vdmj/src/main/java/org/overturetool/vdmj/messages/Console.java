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

package org.overturetool.vdmj.messages;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class Console
{
	/** The charset for the console. */
	public static String charset;

	/** A print writer for stdout that uses a given encoding. */
	public static PrintWriter out;

	/** A print writer for stderr that uses a given encoding. */
	public static PrintWriter err;

	/** A buffered reader for stdin that uses a given encoding. */
	public static BufferedReader in;

	static
	{
		init(Charset.defaultCharset().name());
	}

	public static void setCharset(String cs)
	{
		init(cs);
	}

	private static void init(String cs)
	{
		try
		{
			charset = cs;
			out = new PrintWriter(new OutputStreamWriter(System.out, charset), true);
			err = new PrintWriter(new OutputStreamWriter(System.err, charset), true);
			in = new BufferedReader(new InputStreamReader(System.in, charset));
		}
		catch (UnsupportedEncodingException e)
		{
			System.err.println("Console encoding exception: " + e);
		}
	}
}
