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

package org.overturetool.vdmj.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.VDMJ;
import org.overturetool.vdmj.lex.LexLocation;

/**
 * A class to hold a source file for source debug output.
 */

public class SourceFile
{
	public final File filename;
	public List<String> lines = new Vector<String>();

	public SourceFile(File filename) throws IOException
	{
		this.filename = filename;
		
		BufferedReader br = new BufferedReader(
			new InputStreamReader(
				new FileInputStream(filename), VDMJ.filecharset));

		String line = br.readLine();

		while (line != null)
		{
			lines.add(line);
			line = br.readLine();
		}

		br.close();
	}

	public String getLine(int n)
	{
		if (n < 1 || n > lines.size())
		{
			return "~";
		}

		return lines.get(n-1);
	}

	public int getCount()
	{
		return lines.size();
	}

	public void printCoverage(PrintWriter out)
	{
		List<Integer> hitlist = LexLocation.getHitList(filename);
		List<Integer> srclist = LexLocation.getSourceList(filename);

		int hitcount = 0;
		int srccount = 0;
		boolean supress = false;

		out.println("Test coverage for " + filename + ":\n");

		for (int lnum = 1; lnum <= lines.size(); lnum++)
		{
			String line = lines.get(lnum - 1);

			if (line.startsWith("\\begin{vdm_al}"))
			{
				supress = false;
				continue;
			}
			else if (line.startsWith("\\end{vdm_al}") ||
					 line.startsWith("\\section") ||
					 line.startsWith("\\subsection") ||
					 line.startsWith("\\document") ||
					 line.startsWith("%"))
			{
				supress = true;
				continue;
			}

			if (srclist.contains(lnum))
			{
				srccount++;

				if (hitlist.contains(lnum))
				{
					out.println("+ " + line);
					hitcount++;
				}
				else
				{
					out.println("- " + line);
				}
			}
			else
			{
				if (!supress)
				{
					out.println("  " + line);
				}
			}
		}

		out.println("\nCoverage = " +
			(srccount == 0 ? 0 : (100 * hitcount / srccount)) + "%");
	}
}
