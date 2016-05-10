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

package org.overture.parser.lex;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Stack;

import org.overture.ast.messages.InternalException;

/**
 * A class to allow arbitrary checkpoints and backtracking while parsing a file.
 */

public class BacktrackInputReader extends Reader
{
	/**
	 * The different types of reader that may be obtained from a {@link BacktrackInputReader}
	 * 
	 * @author pvj
	 */
	public enum ReaderType
	{
		Doc, Docx, Odf, Latex
	};

	/**
	 * Cached empty array for reader data
	 */
	private static final char[] EMPTY_DATA = new char[] {};

	/** A stack of position markers for popping. */
	private Stack<Integer> stack = new Stack<>();

	/** The characters from the file. */
	private final char[] data;

	/** The current read position. */
	private int pos = 0;

	/** The total number of characters in the file. */
	private int max = 0;

	/**
	 * Create an object to read the file name passed with the given charset.
	 * 
	 * @param file
	 *            The filename to open
	 * @param charset
	 */

	public BacktrackInputReader(File file, String charset)
	{
		try
		{
			InputStreamReader isr = readerFactory(file, charset);
			char[] buffer = new char[readerLength(file, isr)];
			max = isr.read(buffer);
			data = Arrays.copyOf(buffer, max);
			pos = 0;
			isr.close();
		} catch (IOException e)
		{
			throw new InternalException(0, e.getMessage());
		}
	}

	/**
	 * Create an object to read the file name passed with the default charset.
	 * 
	 * @param file
	 *            The filename to open
	 */

	public BacktrackInputReader(File file)
	{
		this(file, Charset.defaultCharset().name());
	}

	/**
	 * Create an object to read the string passed. This is used in the interpreter to parse expressions typed in.
	 * 
	 * @param expression
	 * @param charset
	 */

	public BacktrackInputReader(String expression, String charset)
	{
		char[] buf = EMPTY_DATA;
		try
		{
			ByteArrayInputStream is = new ByteArrayInputStream(expression.getBytes(charset));

			InputStreamReader isr = new LatexStreamReader(is, charset);

			buf = new char[expression.length() + 1];
			max = isr.read(buf);
			pos = 0;

			isr.close();
			is.close();
		} catch (IOException e)
		{
			// This can never really happen...
		} finally
		{
			data = buf;
		}
	}

	/**
	 * Create an object to read the string passed. This is used in the interpreter to parse expressions typed in.
	 * 
	 * @param expression
	 * @param charset
	 * @param file
	 * @param streamReaderType
	 */
	public BacktrackInputReader(String expression, String charset, File file,
			ReaderType streamReaderType)
	{
		char[] buf = EMPTY_DATA;
		try
		{
			if (expression.contains("\r\n"))
			{
				expression = expression.replace("\r\n", " \n");
			}

			ByteArrayInputStream is = new ByteArrayInputStream(expression.getBytes(charset));

			InputStreamReader isr = null;

			switch (streamReaderType)
			{
				case Doc:
					isr = new DocStreamReader(new FileInputStream(file), charset);
					break;
				case Docx:
					isr = new DocxStreamReader(new FileInputStream(file));
					break;
				case Odf:
					isr = new ODFStreamReader(new FileInputStream(file));
					break;
				case Latex:
				default:
					isr = new LatexStreamReader(is, charset);
					break;
			}

			if (!expression.contains("\r\n"))
			{
				expression = expression.replace("\n", " \n");
			}

			buf = new char[expression.length() + 1];
			max = isr.read(buf);
			pos = 0;

			isr.close();
			is.close();
		} catch (IOException e)
		{
			e.printStackTrace();
			// This can never really happen...
		} finally
		{
			data = buf;
		}
	}

	/**
	 * Create an InputStreamReader from a File, depending on the filename.
	 * 
	 * @param file
	 * @param charset
	 * @return
	 * @throws IOException
	 */

	public static InputStreamReader readerFactory(File file, String charset)
			throws IOException
	{
		String name = file.getName();

		if (name.toLowerCase().endsWith(".doc"))
		{
			return new DocStreamReader(new FileInputStream(file), charset);
		} else if (name.toLowerCase().endsWith(".docx"))
		{
			return new DocxStreamReader(new FileInputStream(file));
		} else if (name.toLowerCase().endsWith(".odt"))
		{
			return new ODFStreamReader(new FileInputStream(file));
		} else
		{
			return new LatexStreamReader(new FileInputStream(file), charset);
		}
	}

	/**
	 * Calculate the length to allocate for a given file/stream.
	 */

	private int readerLength(File file, InputStreamReader isr)
	{
		String name = file.getName();

		if (name.endsWith(".docx"))
		{
			return ((DocxStreamReader) isr).length();
		} else if (name.endsWith(".odt"))
		{
			return ((ODFStreamReader) isr).length();
		} else
		{
			return (int) (file.length() + 1);
		}
	}

	/**
	 * Create an object to read the string passed with the default charset.
	 * 
	 * @param expression
	 */

	public BacktrackInputReader(String expression)
	{
		this(expression, Charset.defaultCharset().name());
	}

	/**
	 * Push the current location to permit backtracking.
	 */

	public void push()
	{
		stack.push(pos);
	}

	/**
	 * Pop the last location, but do not backtrack to it. This is used when the parser reached a point where a potential
	 * ambiguity has been resolved, and it knows that it will never need to backtrack.
	 */

	public void unpush()
	{
		stack.pop(); // don't set pos though
	}

	/**
	 * Pop the last location and reposition the stream at that position. The state of the stream is such that the next
	 * read operation will return the same character that would have been read immediately after the push() operation
	 * that saved the position.
	 */

	public void pop()
	{
		pos = stack.pop();
	}

	/**
	 * Read one character.
	 * 
	 * @return
	 */

	public char readCh()
	{
		return data.length <= pos || pos == max ? (char) -1 : data[pos++];
	}

	/**
	 * Read characters into the array passed.
	 */

	@Override
	public int read(char[] cbuf, int off, int len)
	{
		int n = 0;

		while (pos != max && n < len)
		{
			cbuf[off + n++] = data[pos++];
		}

		return n == 0 ? -1 : n;
	}

	/**
	 * Close the input stream.
	 */

	@Override
	public void close()
	{
		return; // Stream was closed at the start anyway.
	}

	public int getCurrentRawReadOffset()
	{
		return pos;
	}
}
