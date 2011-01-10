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

package org.overturetool.vdmj.lex;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.overturetool.vdmj.VDMJ;

/**
 * A class to read an XML encoded VDM file, such as .docx or ODF
 */

abstract public class XMLStreamReader extends InputStreamReader
{
	protected String fileText = null;
	private final static int ARRAYCHUNK = 10000;

	public XMLStreamReader(InputStream in, String partName, String charsetName)
		throws IOException
	{
		super(in, charsetName);
		ZipInputStream zis = new ZipInputStream(in);
		ZipEntry ze = zis.getNextEntry();

		while (ze != null)
		{
			if (ze.getName().equals(partName))
			{
				byte[] bytes = new byte[ARRAYCHUNK];
				int space = ARRAYCHUNK;
				int p = 0;
				int r = 0;

				do
				{
					r = zis.read(bytes, p, 1000);

					if (r > 0)
					{
						p += r;

    					if (space - p < 1000)
    					{
    						space += ARRAYCHUNK;
    						bytes = Arrays.copyOf(bytes, space);
    					}
					}
				}
				while (r > 0);

				fileText = despace(new String(bytes, 0, p, VDMJ.filecharset));
				break;
			}

			ze = zis.getNextEntry();
		}

		zis.close();
	}

	protected final static String MARKER = "%%VDM%%";

	@Override
	public int read(char[] array) throws IOException
	{
		int start = fileText.indexOf(MARKER);
		int ap = 0;

		while (start > 0)
		{
			start += MARKER.length();
			char[] clean = new char[fileText.length() - start];
			int end = fileText.indexOf(MARKER, start);
			boolean capturing = true;
			int cp = 0;

			for (int p=start; p<end; p++)
			{
				char c = fileText.charAt(p);

				if (capturing)
				{
					if (c == '<')
					{
						capturing = false;
					}
					else
					{
						clean[cp++] = c;
					}
				}
				else
				{
					if (c == '>')
					{
						capturing = true;
					}
				}
			}

			String fixed = dequote(new String(clean, 0, cp));
			char[] chars = fixed.toCharArray();
			System.arraycopy(chars, 0, array, ap, chars.length);
			ap += chars.length;

			start = fileText.indexOf(MARKER, end+1);
		}

		return ap;
	}

	public int length()
	{
		return fileText.length();
	}

	abstract protected String despace(String in);

	protected String dequote(String in)
	{
		return in
    		.replaceAll("&amp;", "&")
    		.replaceAll("&lt;", "<")
    		.replaceAll("&gt;", ">")
    		.replaceAll("&quot;", "\\\"");
	}
}
