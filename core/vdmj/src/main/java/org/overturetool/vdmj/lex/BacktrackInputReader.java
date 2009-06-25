/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.lex;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Stack;

import org.overturetool.vdmj.messages.InternalException;

/**
 * A class to allow arbitrary checkpoints and backtracking while
 * parsing a file.
 */

public class BacktrackInputReader extends Reader
{
	/** A stack of position markers for popping. */
	private Stack<Integer> stack = new Stack<Integer>();

	/** The characters from the file. */
	private char[] data;

	/** The current read position. */
	private int pos = 0;

	/** The total number of characters in the file. */
	private int max = 0;

	/**
	 * Create an object to read the file name passed with the given charset.
	 *
	 * @param file	The filename to open
	 */

	public BacktrackInputReader(File file, String charset)
	{
		try
		{
			data = new char[(int)file.length() + 1];
			InputStreamReader isr =
				new LatexStreamReader(new FileInputStream(file), charset);
			max = isr.read(data);
			pos = 0;
			isr.close();
		}
		catch (IOException e)
		{
			throw new InternalException(0, e.getMessage());
		}
	}

	/**
	 * Create an object to read the file name passed with the default charset.
	 *
	 * @param file	The filename to open
	 */

	public BacktrackInputReader(File file)
	{
		this(file, Charset.defaultCharset().name());
	}

	/**
	 * Create an object to read the string passed. This is used in the
	 * interpreter to parse expressions typed in.
	 *
	 * @param expression
	 */

	public BacktrackInputReader(String expression)
	{
    	try
        {
    		data = new char[expression.length() + 1];

	        ByteArrayInputStream is =
	        	new ByteArrayInputStream(expression.getBytes());

	        InputStreamReader isr =
	        	new LatexStreamReader(is, Charset.defaultCharset().name());

	        max = isr.read(data);
	        pos = 0;

	        isr.close();
	        is.close();
        }
        catch (IOException e)
        {
	        // This can never really happen...
        }

		// data = expression.toCharArray();
		// max = expression.length();
	}

	/**
	 * Push the current location to permit backtracking.
	 */

	public void push()
	{
		stack.push(pos);
	}

	/**
	 * Pop the last location, but do not backtrack to it. This is used when
	 * the parser reached a point where a potential ambiguity has been resolved,
	 * and it knows that it will never need to backtrack.
	 */

	public void unpush()
	{
		stack.pop();	// don't set pos though
	}

	/**
	 * Pop the last location and reposition the stream at that position. The
	 * state of the stream is such that the next read operation will return
	 * the same character that would have been read immediately after the
	 * push() operation that saved the position.
	 */

	public void pop()
	{
		pos = stack.pop();
	}

	/**
	 * Read one character.
	 */

	public char readCh()
	{
		return (pos == max) ? (char)-1 : data[pos++];
	}

	/**
	 * Read characters into the array passed.
	 */

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		int n = 0;

		while (pos != max && n < len)
		{
			cbuf[off + n++] = data[pos++];
		}

		return (pos == len) ? -1 : n;
	}

	/**
	 * Close the input stream.
	 */

	@Override
	public void close() throws IOException
	{
		return;		// Stream was closed at the start anyway.
	}
}
