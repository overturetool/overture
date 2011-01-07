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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * A class to read .doc encoded VDM files.
 */

public class DocStreamReader extends InputStreamReader
{
	public DocStreamReader(InputStream in, String charsetName)
		throws UnsupportedEncodingException
	{
		super(in, charsetName);
	}

	@Override
	public int read(char[] array) throws IOException
	{
		BufferedReader br = new BufferedReader(this);
		boolean capturing = false;
		int pos = 0;
		String line = br.readLine();

		while (line != null)
		{
			if (line.endsWith("%%VDM%%"))
			{
				capturing = !capturing;
			}
			else
			{
				if (capturing)
				{
					line.getChars(0, line.length(), array, pos);
					pos += line.length();
					array[pos++] = '\n';
				}
			}

			line = br.readLine();
		}

		br.close();
		return pos;
	}

	public static void main(String[] args) throws IOException
	{
		File file = new File(args[0]);
		char[] data = new char[(int)file.length() + 1];
		InputStreamReader isr =	new DocStreamReader(new FileInputStream(file), "ascii");
		int pos = isr.read(data);
		isr.close();

		System.out.print(new String(data, 0, pos));
	}
}
