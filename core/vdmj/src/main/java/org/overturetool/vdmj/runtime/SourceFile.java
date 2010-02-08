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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overturetool.vdmj.VDMJ;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexTokenReader;

/**
 * A class to hold a source file for source debug output.
 */

public class SourceFile
{
	public final File filename;
	public List<String> lines = new Vector<String>();
	public final boolean hasVdm_al;

	public SourceFile(File filename) throws IOException
	{
		this.filename = filename;

		BufferedReader br = new BufferedReader(
			new InputStreamReader(
				new FileInputStream(filename), VDMJ.filecharset));

		String line = br.readLine();
		boolean vdm_al = false;

		while (line != null)
		{
			if (line.startsWith("\\begin{vdm_al}"))
			{
				vdm_al = true;
			}

			lines.add(line);
			line = br.readLine();
		}

		hasVdm_al = vdm_al;
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

	public void printLatexCoverage(PrintWriter out, boolean headers)
	{
		Map<Integer, List<LexLocation>> hits =
					LexLocation.getMissLocations(filename);

		if (headers)
		{
    		out.println("\\documentclass[a4paper]{article}");
    		out.println("\\input{overture}");
    		out.println("\\begin{document}");
		}

		if (!hasVdm_al)
		{
			out.println("\\begin{vdm_al}");
		}

		boolean endDocFound = false;

		for (int lnum = 1; lnum <= lines.size(); lnum++)
		{
			String line = lines.get(lnum - 1);

			if (line.contains("\\end{document}"))
			{
				endDocFound = true;
				break;
			}

			String spaced = detab(line, LexTokenReader.TABSTOP);
			List<LexLocation> list = hits.get(lnum);
			out.println(markup(spaced, list));
		}

		if (!hasVdm_al)
		{
			out.println("\\end{vdm_al}");
		}

		out.println("\\bigskip");
		out.println("\\begin{tabular}{|l|r|r|}");
		out.println("\\hline");
		out.println("Function or operation & Coverage & Calls \\\\");
		out.println("\\hline");
		out.println("\\hline");

		long total = 0;

		LexNameList spans = LexLocation.getSpanNames(filename);
		Collections.sort(spans);

		for (LexNameToken name: spans)
		{
			long calls = LexLocation.getSpanCalls(name);
			total += calls;

			out.println(latexQuote(name.toString()) + " & " +
				LexLocation.getSpanPercent(name) + "\\% & " +
				calls + " \\\\");
			out.println("\\hline");
		}

		out.println("\\hline");
		out.println(latexQuote(filename.getName()) +
			" & " + LexLocation.getHitPercent(filename) +
			"\\% & " + total + " \\\\");

		out.println("\\hline");
		out.println("\\end{tabular}");

		if (headers || endDocFound)
		{
			out.println("\\end{document}");
		}
	}

	private String markup(String line, List<LexLocation> list)
    {
		if (list == null)
		{
			return line;
		}
		else
		{
			StringBuilder sb = new StringBuilder();
			int p = 0;

			for (LexLocation m: list)
			{
				int start = m.startPos - 1;
				int end = m.startLine == m.endLine ? m.endPos - 1 : line.length();

				if (start >= p)		// Backtracker produces duplicate tokens
				{
    				sb.append(line.substring(p, start));
    				sb.append("!\\notcovered{");
    				sb.append(latexQuote(line.substring(start, end)));
    				sb.append("}!");	//\u00A3");

    				p = end;
				}
			}

			sb.append(line.substring(p));
			return sb.toString();
		}
    }

	private String latexQuote(String s)
	{
		// Latex specials: \# \$ \% \^{} \& \_ \{ \} \~{}

		return s.
			replace("#", "\\#").
			replace("$", "\\$").
			replace("%", "\\%").
			replace("&", "\\&").
			replace("_", "\\_").
			replace("{", "\\{").
			replace("}", "\\}").
			replace("^", "\\^{}").
			replace("~", "\\~{}");
	}

	private static String detab(String s, int tabstop)
	{
		StringBuilder sb = new StringBuilder();
		int p = 0;

		for (int i=0; i<s.length(); i++)
		{
			char c = s.charAt(i);

			if (c == '\t')
			{
				int n = tabstop - p % tabstop;

				for (int x=0; x < n; x++)
				{
					sb.append(' ');
				}

				p += n;
			}
			else
			{
				sb.append(c);
				p++;
			}
		}

		return sb.toString();
	}

	public void writeCoverage(PrintWriter out)
	{
        for (LexLocation l: LexLocation.getSourceLocations(filename))
        {
        	if (l.hits > 0)
        	{
        		out.println("+" + l.startLine +
        			" " + l.startPos + "-" + l.endPos + "=" + l.hits);
        	}
        }
	}
}
