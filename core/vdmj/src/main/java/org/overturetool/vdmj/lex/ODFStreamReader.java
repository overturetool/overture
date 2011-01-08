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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.overturetool.vdmj.VDMJ;

/**
 * A class to read an ODF encoded VDM file.
 */

public class ODFStreamReader extends InputStreamReader
{
	private String fileText = null;

	public ODFStreamReader(InputStream in, String charsetName)
		throws IOException
	{
		super(in, charsetName);
		ZipInputStream zis = new ZipInputStream(in);
		ZipEntry ze = zis.getNextEntry();

		while (ze != null)
		{
			if (ze.getName().equals("content.xml"))
			{
				byte[] bytes = new byte[1000000];
				int p = 0;
				int asize = zis.read(bytes);
				
				while (asize >= 0)
				{
					p += asize;
					asize = zis.read(bytes, p, 1000);
				}
				
				fileText = despace(new String(bytes, 0, p, VDMJ.filecharset));
				break;
			}

			ze = zis.getNextEntry();
		}

		zis.close();
	}

	@Override
	public int read(char[] array) throws IOException
	{
		int start = fileText.indexOf("%%VDM%%");
		int cp = 0;

		if (start > 0)
		{
			start += 7;
			char[] clean = new char[fileText.length() - start];
			int end = fileText.indexOf("%%VDM%%", start+1);
			boolean capturing = true;

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
			char[] bytes = fixed.toCharArray();
			cp = bytes.length;
			System.arraycopy(bytes, 0, array, 0, cp);
		}

		return cp;
	}
	
	private static String despace(String in)
	{
		int n = 1;
		String replace = "";
	
		while (in.indexOf("<text:s text:c") > 0)
		{
			String pattern = "<text:s text:c=\"" + (n++) + "\"/>";
			replace = replace + " ";
			
			if (in.indexOf(pattern) > 0)
			{
				in = in.replaceAll(pattern, replace);
			}
		}
		
		return in
    		.replaceAll("<text:tab/>", "\t")
			.replaceAll("<text:s/>", " ")
    		.replaceAll("</text:p>", "\n")
			.replaceAll("<text:p [^/>]+/>", "\n");
	}

	private static String dequote(String in)
	{
		return in
    		.replaceAll("&amp;", "&")
    		.replaceAll("&lt;", "<")
    		.replaceAll("&gt;", ">")
    		.replaceAll("&quot;", "\\\"");
	}
}
