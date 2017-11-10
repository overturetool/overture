/*
 * #%~
 * org.overture.ide.plugins.latex
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
package org.overture.ide.plugins.latex.utility;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
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

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.CoverageUtil;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameList;
import org.overture.config.Settings;
import org.overture.interpreter.runtime.SourceFile;
import org.overture.parser.config.Properties;

public class XetexSourceFile extends SourceFile
{
	private static final String CURLY_BRACKET_VDM_AL = "{vdm_al}";
	private static final String CURLY_BRACKET_VDM_SL = "{vdmsl}";
	private static final String CURLY_BRACKET_VDM_PP = "{vdmpp}";
	private static final String CURLY_BRACKET_VDM_RT = "{vdmrt}";
	private static final String BEGIN = "\\begin";
	private static final String END = "\\end";
	public List<String> rawLines = new Vector<String>();
	public final boolean hasVdm_al;
	// The argument to lstset is: escapeinside={(*@}{@*)}
	public final String LST_ESCAPE_BEGIN = "(*@";
	public final String LST_ESCAPE_END = "@*)";
	private static final String sectionFont = "{\\jmsg{";
	private static final String docFont = "{\\jmsm{";
	private static final String fontEnd = "}}";

	public boolean useJPNFont;

	public XetexSourceFile(SourceFile source) throws IOException
	{
		this(source.filename, source.charset);
	}

	public XetexSourceFile(File filename, String charset) throws IOException
	{
		super(filename, charset);

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), charset));

		String line = br.readLine();
		boolean vdm_al = false;

		while (line != null)
		{
			if (line.startsWith(BEGIN + CURLY_BRACKET_VDM_AL))
			{
				vdm_al = true;
			}

			rawLines.add(line);
			line = br.readLine();
		}

		hasVdm_al = vdm_al;
		br.close();
	}

	public void printCoverage(PrintWriter out, boolean headers, CoverageUtil coverageUtil)
	{
		printCoverage(out, headers, false, true, coverageUtil);
	}

	public void printCoverage(PrintWriter out, boolean headers,
			boolean modelOnly, boolean includeCoverageTable, CoverageUtil coverageUtil)
	{
		print(out, headers, modelOnly, includeCoverageTable, true, coverageUtil);
	}

	public void print(PrintWriter out, boolean headers, boolean modelOnly,
			boolean includeCoverageTable, boolean markCoverage, CoverageUtil coverageUtil)
	{
		Map<Integer, List<ILexLocation>> hits = coverageUtil.getMissLocations(filename);
		
		if (headers)
		{
			out.println("\\documentclass[a4paper]{article}");
			out.println("\\usepackage{longtable}");
			// out.println("\\input{times}");
			// out.println("\\input{graphicx}");
			out.println("\\usepackage[color]{vdmlisting}");
			out.println("\\usepackage{fullpage}");
			out.println("\\usepackage{hyperref}");
			out.println("%JPNFONT\\usepackage{fontspec}"); // added by his 2013/10/08
			out.println("%JPNFONT\\setmainfont{Times New Roman}"); // added by his 2013/10/24
			out.println("%JPNFONT\\setmonofont[Scale=0.9]{Courier New}"); // added by his 2013/10/24
			out.println("%JPNFONT\\newfontfamily\\jmsm[Scale=0.92]{MS Mincho}"); // added by his 2013/10/24
			out.println("%JPNFONT\\newfontfamily\\jmsg[Scale=0.92]{MS Gothic}"); // added by his 2013/10/24
			out.println("\\begin{document}");
			out.println("\\title{}");
			out.println("\\author{}");
		}

		// move here
		boolean endDocFound = false;
		boolean inVdmAlModelTag = false;
		useJPNFont = checkFont("MS Gothic");

		if (!hasVdm_al)
		{
			out.println(BEGIN + getListingEnvironment());
			inVdmAlModelTag = true; // added
		}

		// boolean endDocFound = false;
		// boolean inVdmAlModelTag = false;
		// useJPNFont = checkFont("MS Gothic");
		LexNameList spans = coverageUtil.getSpanNames(filename);

		for (int lnum = 1; lnum <= rawLines.size(); lnum++)
		{
			for (ILexNameToken name : spans)
			{
				if (name.getLocation().getStartLine() == lnum)
				{
					out.println(LST_ESCAPE_BEGIN);
					out.println("\\label{" + latexLabel(name.getName()) + ":"
							+ name.getLocation().getStartLine() + "}");
					out.println(LST_ESCAPE_END);
				}
			}
			String line = rawLines.get(lnum - 1);

			if (line.contains("\\end{document}"))
			{
				endDocFound = true;
				break;
			}

			if (line.contains(BEGIN + CURLY_BRACKET_VDM_AL))
			{
				inVdmAlModelTag = true;
			}

			// if (line.contains("\\subsection{")) // added by his 2013/10/16
			// {
			// inVdmAlModelTag = false;
			// }

			if (hasVdm_al && modelOnly && !inVdmAlModelTag)
			{
				// continue;
			}

			String spaced = detab(line, Properties.parser_tabstop);
			spaced = spaced.replace(BEGIN + CURLY_BRACKET_VDM_AL, BEGIN
					+ getListingEnvironment()).replace(END
					+ CURLY_BRACKET_VDM_AL, END + getListingEnvironment());

			if (markCoverage)
			{
				// List<LexLocation> list = hits.get(lnum);
				// out.println(markup(spaced, list));
				if (inVdmAlModelTag)
				{
					List<ILexLocation> list = hits.get(lnum);
					out.println(markup(spaced, list));
				} else
				{
					// List<LexLocation> list = hits.get(lnum);

					if (spaced.contains("\\subsection{")
							|| spaced.contains("\\subsubsection{"))
					{
						spaced = utfIncludeCheck(spaced, false);
						out.println(spaced.replace(docFont, sectionFont));
					} else
					{
						out.println(utfIncludeCheck(spaced, false));
					}
				}
			} else
			{
				out.println(spaced);
			}

			if (line.contains(END + CURLY_BRACKET_VDM_AL))
			{
				inVdmAlModelTag = false;
			}
		}

		if (!hasVdm_al)
		{
			out.println(END + getListingEnvironment());
			inVdmAlModelTag = false; // added
		}

		if (includeCoverageTable)
		{
			out.println("\\bigskip");
			out.println(createCoverageTable(coverageUtil));
		}
		if (headers || endDocFound)
		{
			out.println("\\end{document}");
		}
	}

	private String getListingEnvironment()
	{
		switch (Settings.dialect)
		{
			case VDM_PP:
				return CURLY_BRACKET_VDM_PP;
			case VDM_RT:
				return CURLY_BRACKET_VDM_RT;
			case VDM_SL:
				return CURLY_BRACKET_VDM_SL;
			case CML:
			default:
				return CURLY_BRACKET_VDM_AL;
		}
	}

	private String createCoverageTable(CoverageUtil coverageUtil)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\\begin{longtable}{|l|r|r|r|}" + "\n");
		sb.append("\\hline" + "\n");
		sb.append("Function or operation & Line & Coverage & Calls \\\\" + "\n");
		sb.append("\\hline" + "\n");
		sb.append("\\hline" + "\n");

		long total = 0;

		LexNameList spans = coverageUtil.getSpanNames(filename);
		Collections.sort(spans);

		for (ILexNameToken name : spans)
		{
			long calls = coverageUtil.getSpanCalls(name);
			total += calls;

			sb.append("\\hyperref[" + latexLabel(name.getName()) + ":"
					+ name.getLocation().getStartLine() + "]{"
					+ utfIncludeCheck(latexQuote(name.toString()), false)
					+ "}" + " & " + name.getLocation().getStartLine() + " & "
					+ coverageUtil.getSpanPercent(name) + "\\% & " + calls
					+ " \\\\" + "\n");
			sb.append("\\hline" + "\n");
		}

		sb.append("\\hline" + "\n");
		sb.append(latexQuote(filename.getName()) + " & & "
				+ coverageUtil.getHitPercent(filename) + "\\% & " + total
				+ " \\\\" + "\n");

		sb.append("\\hline" + "\n");
		sb.append("\\end{longtable}" + "\n");
		return sb.toString();
	}

	private String markup(String line, List<ILexLocation> list)
	{
		if (list == null)
		{
			// return line;
			return utfIncludeCheck(line, true);
		} else
		{
			StringBuilder sb = new StringBuilder();
			int p = 0;

			for (ILexLocation m : list)
			{
				int start = m.getStartPos() - 1;
				int end = m.getStartLine() == m.getEndLine() ? m.getEndPos() - 1
						: line.length();

				if (start >= p) // Backtracker produces duplicate tokens
				{
					// sb.append(line.substring(p, start));
					sb.append(utfIncludeCheck(line.substring(p, start), true));
					sb.append(LST_ESCAPE_BEGIN + "\\vdmnotcovered{");
					// String temp = utfIncludeCheck(latexQuote(line.substring(start, end)), false);
					// if(temp.charAt(temp.length()-1)==' ') temp=temp.substring(0, temp.length()-1);
					sb.append(utfIncludeCheck(latexQuote(line.substring(start, end)), false)); // modified by his
					sb.append("}" + LST_ESCAPE_END); // \u00A3");

					p = end;
				}
			}

			// sb.append(line.substring(p));
			sb.append(utfIncludeCheck(line.substring(p), true));
			return sb.toString();
		}
	}

	private String latexQuote(String s)
	{
		// Latex specials: \# \$ \% \^{} \& \_ \{ \} \~{} \\

		return s.replace("\\", "\\textbackslash ").replace("#", "\\#").replace("$", "\\$").replace("%", "\\%").replace("&", "\\&").replace("_", "\\_").replace("{", "\\{").replace("}", "\\}").replace("~", "\\~").replaceAll("\\^{1}", "\\\\^{}");
	}

	// add by his 2013/10/08
	private String utfIncludeCheck(String line, Boolean addatsign)
	{
		String checked = "";
		boolean start = false;

		for (int i = 0; i < line.length(); i++)
		{
			if (isOneByte(line.substring(i, i + 1)))
			{
				if (start)
				{
					start = false;
					checked += (addatsign ? useJPNFont ? fontEnd
							+ LST_ESCAPE_END : "" : fontEnd)
							+ line.substring(i, i + 1);
				} else
				{
					checked += line.substring(i, i + 1);
				}
			} else
			{
				if (!start)
				{
					checked += (addatsign ? useJPNFont ? LST_ESCAPE_BEGIN : ""
							: "")
							+ (useJPNFont ? docFont : "")
							+ line.substring(i, i + 1);
					start = true;
				} else
				{
					checked += line.substring(i, i + 1);
				}
			}
		}
		if (start)
		{
			checked += addatsign ? useJPNFont ? fontEnd + LST_ESCAPE_END : ""
					: fontEnd;
		}
		return checked;
	}

	private boolean isOneByte(String a_String)
	{
		boolean result = false;
		try
		{
			byte[] code = a_String.getBytes("UTF-8");
			result = code.length == 1 ? true : false;
		} catch (IOException ex)
		{
		}
		return result;
	}

	private boolean checkFont(String FontName)
	{
		boolean checked = false;

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		Font fonts[] = ge.getAllFonts();

		for (int i = 0; i < fonts.length; i++)
		{
			if (fonts[i].getName().toString().equals(FontName))
			{
				checked = true;
				break;
			}
		}
		return checked;
	}
	
	private String latexLabel(String s)
	{
		// Latex specials: \# \$ \% \^{} \& \_ \{ \} \~{} \\
		return s.replace("\\", ":").replace("#", ":").replace("$", ":").replace("%", ":").replace("&", ":").replace("_", ":").replace("{", ":").replace("}", ":").replace("~", ":").replaceAll("\\^{1}", ":");
	}
}
