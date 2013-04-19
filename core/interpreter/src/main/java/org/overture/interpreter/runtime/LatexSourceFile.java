package org.overture.interpreter.runtime;

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

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameList;
import org.overture.config.Settings;
import org.overture.parser.config.Properties;

public class LatexSourceFile extends SourceFile
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

	public LatexSourceFile(SourceFile source) throws IOException
	{
		this(source.filename, source.charset);
	}

	public LatexSourceFile(File filename, String charset) throws IOException
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

	public void printCoverage(PrintWriter out, boolean headers)
	{
		printCoverage(out, headers, false, true);
	}

	public void printCoverage(PrintWriter out, boolean headers,
			boolean modelOnly, boolean includeCoverageTable)
	{
		print(out, headers, modelOnly, includeCoverageTable, true);
	}

	public void print(PrintWriter out, boolean headers, boolean modelOnly,
			boolean includeCoverageTable, boolean markCoverage)
	{
		Map<Integer, List<LexLocation>> hits = LexLocation.getMissLocations(filename);

		if (headers)
		{
			out.println("\\documentclass[a4paper]{article}");
			out.println("\\usepackage{longtable}");
			// out.println("\\input{times}");
			// out.println("\\input{graphicx}");
			out.println("\\usepackage[color]{vdmlisting}");
			out.println("\\usepackage{fullpage}");
			out.println("\\usepackage{hyperref}");
			out.println("\\begin{document}");
			out.println("\\title{}");
			out.println("\\author{}");
		}

		if (!hasVdm_al)
		{
			out.println(BEGIN + getListingEnvironment());
		}

		boolean endDocFound = false;
		boolean inVdmAlModelTag = false;

		for (int lnum = 1; lnum <= rawLines.size(); lnum++)
		{
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

			if (hasVdm_al && modelOnly && !inVdmAlModelTag)
			{
				continue;
			}

			String spaced = detab(line, Properties.parser_tabstop);
			spaced = spaced.replace(BEGIN + CURLY_BRACKET_VDM_AL, BEGIN
					+ getListingEnvironment()).replace(END + CURLY_BRACKET_VDM_AL, END
							+ getListingEnvironment());

			if (markCoverage)
			{
				List<LexLocation> list = hits.get(lnum);
				out.println(markup(spaced, list));
			} else
			{
				out.println(spaced);
			}

			if (line.contains(END + getListingEnvironment()))
			{
				inVdmAlModelTag = false;
			}
		}

		if (!hasVdm_al)
		{
			out.println(END + getListingEnvironment());
		}

		if (includeCoverageTable)
		{
			out.println("\\bigskip");
			out.println(createCoverageTable());
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

	private String createCoverageTable()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\\begin{longtable}{|l|r|r|}" + "\n");
		sb.append("\\hline" + "\n");
		sb.append("Function or operation & Coverage & Calls \\\\" + "\n");
		sb.append("\\hline" + "\n");
		sb.append("\\hline" + "\n");

		long total = 0;

		LexNameList spans = LexLocation.getSpanNames(filename);
		Collections.sort(spans);

		for (ILexNameToken name : spans)
		{
			long calls = LexLocation.getSpanCalls(name);
			total += calls;

			sb.append(latexQuote(name.toString()) + " & "
					+ LexLocation.getSpanPercent(name) + "\\% & " + calls
					+ " \\\\" + "\n");
			sb.append("\\hline" + "\n");
		}

		sb.append("\\hline" + "\n");
		sb.append(latexQuote(filename.getName()) + " & "
				+ LexLocation.getHitPercent(filename) + "\\% & " + total
				+ " \\\\" + "\n");

		sb.append("\\hline" + "\n");
		sb.append("\\end{longtable}" + "\n");
		return sb.toString();
	}

	private String markup(String line, List<LexLocation> list)
	{
		if (list == null)
		{
			return line;
		} else
		{
			StringBuilder sb = new StringBuilder();
			int p = 0;

			for (LexLocation m : list)
			{
				int start = m.startPos - 1;
				int end = m.startLine == m.endLine ? m.endPos - 1
						: line.length();

				if (start >= p) // Backtracker produces duplicate tokens
				{
					sb.append(line.substring(p, start));
					sb.append(LST_ESCAPE_BEGIN + "\\vdmnotcovered{");
					sb.append(latexQuote(line.substring(start, end)));
					sb.append("}" + LST_ESCAPE_END); // \u00A3");

					p = end;
				}
			}

			sb.append(line.substring(p));
			return sb.toString();
		}
	}

	private String latexQuote(String s)
	{
		// Latex specials: \# \$ \% \^{} \& \_ \{ \} \~{} \\

		return s.replace("\\", "\\textbackslash ").replace("#", "\\#").replace("$", "\\$").replace("%", "\\%").replace("&", "\\&").replace("_", "\\_").replace("{", "\\{").replace("}", "\\}").replace("~", "\\~").replaceAll("\\^{1}", "\\\\^{}");
	}

}
