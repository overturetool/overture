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

package org.overture.interpreter.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.*;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameList;
import org.overture.interpreter.VDMJ;
import org.overture.parser.config.Properties;
import org.overture.parser.lex.BacktrackInputReader;
<<<<<<< Updated upstream
=======
import org.overture.prettyprinter.PrettyPrinterEnv;
import org.overture.prettyprinter.PrettyPrinterVisitor;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sun.awt.PeerEvent;
import org.overture.interpreter.runtime.Context;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
>>>>>>> Stashed changes

/**
 * A class to hold a source file for source debug output.
 */


public class SourceFile {
    public final File filename;
    public final String charset;
    public List<String> lines = new Vector<String>();

    private final static String HTMLSTART = "<p class=MsoNormal style='text-autospace:none'><span style='font-size:10.0pt; font-family:\"Courier New\"; color:black'>";
    private final static String HTMLEND = "</span></p>";

    public SourceFile(File filename) throws IOException {
        this(filename, VDMJ.filecharset);
    }

    public SourceFile(File filename, String charset) throws IOException {
        this.filename = filename;
        this.charset = charset;
        BufferedReader br = new BufferedReader(new BacktrackInputReader(filename, charset));

        String line = br.readLine();

        while (line != null) {
            lines.add(line);
            line = br.readLine();
        }

        br.close();
    }

    public String getLine(int n) {
        if (n < 1 || n > lines.size()) {
            return "~";
        }

        return lines.get(n - 1);
    }

    public int getCount() {
        return lines.size();
    }

    public void printSource(PrintWriter out) {
        for (String line : lines) {
            out.println(line);
        }
    }

    public void printCoverage(PrintWriter out) {
        List<Integer> hitlist = LexLocation.getHitList(filename);
        List<Integer> srclist = LexLocation.getSourceList(filename);

        int hitcount = 0;
        int srccount = 0;
        boolean supress = false;

        out.println("Test coverage for " + filename + ":\n");

        for (int lnum = 1; lnum <= lines.size(); lnum++) {
            String line = lines.get(lnum - 1);

            // TODO remove this filtering it can never happen because of the new scanner
            if (line.startsWith("\\begin{vdm_al}")) {
                supress = false;
                continue;
            } else if (line.startsWith("\\end{vdm_al}")
                    || line.startsWith("\\section")
                    || line.startsWith("\\subsection")
                    || line.startsWith("\\document") || line.startsWith("%")) {
                supress = true;
                continue;
            }

            if (srclist.contains(lnum)) {
                srccount++;

                if (hitlist.contains(lnum)) {
                    out.println("+ " + line);
                    hitcount++;
                } else {
                    out.println("- " + line);
                }
            } else {
                if (!supress) {
                    out.println("  " + line);
                }
            }
        }

        out.println("\nCoverage = "
                + (srccount == 0 ? 0
                : (float) (1000 * hitcount / srccount) / 10) + "%");
    }

    public void printWordCoverage(PrintWriter out) {
        Map<Integer, List<LexLocation>> hits = LexLocation.getMissLocations(filename);

        out.println("<html>");
        out.println("<head>");
        out.println("<meta http-equiv=Content-Type content=\"text/html; charset="
                + VDMJ.filecharset + "\">");
        out.println("<meta name=Generator content=\"Microsoft Word 11 (filtered)\">");
        out.println("<title>" + filename.getName() + "</title>");
        out.println("<style>");
        out.println("<!--");
        out.println("p.MsoNormal, li.MsoNormal, div.MsoNormal");
        out.println("{margin:0in; margin-bottom:.0001pt; font-size:12.0pt; font-family:\"Times New Roman\";}");
        out.println("h1");
        out.println("{margin-top:12.0pt; margin-right:0in; margin-bottom:3.0pt; margin-left:0in; page-break-after:avoid; font-size:16.0pt; font-family:Arial;}");
        out.println("@page Section1");
        out.println("{size:8.5in 11.0in; margin:1.0in 1.25in 1.0in 1.25in;}");
        out.println("div.Section1");
        out.println("{page:Section1;}");
        out.println("-->");
        out.println("</style>");
        out.println("</head>");
        out.println("<body lang=EN-GB>");
        out.println("<div class=Section1>");

        out.println("<h1 align=center style='text-align:center'>"
                + filename.getName() + "</h1>");
        out.println(htmlLine());
        out.println(htmlLine());

        LexNameList spans = LexLocation.getSpanNames(filename);

        for (int lnum = 1; lnum <= lines.size(); lnum++) {
            for (ILexNameToken name : spans) {
                if (name.getLocation().getStartLine() == lnum) {
                    out.println("<a name=\"" + name.getName() + ":"
                            + name.getLocation().getStartLine() + "\" />");

                }
            }

            String line = lines.get(lnum - 1);
            String spaced = detab(line, Properties.parser_tabstop);
            List<LexLocation> list = hits.get(lnum);
            out.println(markupHTML(spaced, list));
        }

        out.println(htmlLine());
        out.println(htmlLine());
        out.println(htmlLine());

        out.println("<div align=center>");
        out.println("<table class=MsoNormalTable border=0 cellspacing=0 cellpadding=0 width=\"60%\" style='width:60.0%;border-collapse:collapse'>");
        out.println(rowHTML(true, "Function or Operation", "Line", "Coverage", "Calls"));

        long total = 0;

        Collections.sort(spans);

        for (ILexNameToken name : spans) {
            long calls = LexLocation.getSpanCalls(name);
            total += calls;

            out.println(rowHTML(false, "<a href=\"#" + name.getName() + ":"
                    + name.getLocation().getStartLine() + "\">"
                    + htmlQuote(name.toString()) + "</a>", name.getLocation().getStartLine()
                    + "", Float.toString(LexLocation.getSpanPercent(name))
                    + "%", Long.toString(calls)));
        }

        out.println(rowHTML(true, htmlQuote(filename.getName()), "", Float.toString(LexLocation.getHitPercent(filename))
                + "%", Long.toString(total)));

        out.println("</table>");
        out.println("</div>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    private String htmlLine() {
        return "<p class=MsoNormal>&nbsp;</p>";
    }

    private String rowHTML(boolean emph, String name, String lineNumber,
                           String coverage, String calls) {
        StringBuilder sb = new StringBuilder();
        String b1 = emph ? "<b>" : "";
        String b2 = emph ? "</b>" : "";
        String bg = emph ? "background:#D9D9D9;" : "";

        sb.append("<tr>\n");

        sb.append("<td width=\"50%\" valign=top style='width:50.0%;border:solid windowtext 1.0pt;"
                + bg + "padding:0in 0in 0in 0in'>\n");
        sb.append("<p class=MsoNormal>" + b1 + name + b2 + "</p>\n");
        sb.append("</td>\n");

        sb.append("<td width=\"10%\" valign=top style='width:25.0%;border:solid windowtext 1.0pt;"
                + bg + "padding:0in 0in 0in 0in'>\n");
        sb.append("<p class=MsoNormal align=right style='text-align:right'>"
                + b1 + lineNumber + b2 + "</p>\n");
        sb.append("</td>\n");

        sb.append("<td width=\"20%\" valign=top style='width:25.0%;border:solid windowtext 1.0pt;"
                + bg + "padding:0in 0in 0in 0in'>\n");
        sb.append("<p class=MsoNormal align=right style='text-align:right'>"
                + b1 + coverage + b2 + "</p>\n");
        sb.append("</td>\n");

        sb.append("<td width=\"20%\" valign=top style='width:25.0%;border:solid windowtext 1.0pt;"
                + bg + "padding:0in 0in 0in 0in'>\n");
        sb.append("<p class=MsoNormal align=right style='text-align:right'>"
                + b1 + calls + b2 + "</p>\n");
        sb.append("</td>\n");

        sb.append("</tr>\n");

        return sb.toString();
    }

    private String markupHTML(String line, List<LexLocation> list) {
        if (line.isEmpty()) {
            return htmlLine();
        }

        StringBuilder sb = new StringBuilder(HTMLSTART);
        int p = 0;

        if (list != null) {
            for (LexLocation m : list) {
                int start = m.startPos - 1;
                int end = m.startLine == m.endLine ? m.endPos - 1
                        : line.length();

                if (start >= p) // Backtracker produces duplicate tokens
                {
                    sb.append(htmlQuote(line.substring(p, start)));
                    sb.append("<span style='color:red'>");
                    sb.append(htmlQuote(line.substring(start, end)));
                    sb.append("</span>");

                    p = end;
                }
            }
        }

        sb.append(htmlQuote(line.substring(p)));
        sb.append(HTMLEND);
        return sb.toString();
    }

    private String htmlQuote(String s) {
        return s.replaceAll("&", "&amp;").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    protected static String detab(String s, int tabstop) {
        StringBuilder sb = new StringBuilder();
        int p = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '\t') {
                int n = tabstop - p % tabstop;

                for (int x = 0; x < n; x++) {
                    sb.append(' ');
                }

                p += n;
            } else {
                sb.append(c);
                p++;
            }
        }

        return sb.toString();
    }


<<<<<<< Updated upstream
    public void writeCoverage(PrintWriter out) {
=======
    public CoverageToXML writeCoverage2(final Interpreter interpreter) {
        final CoverageToXML ctx = new CoverageToXML();
        Context ctxt
        if (interpreter instanceof ClassInterpreter) {
            ClassInterpreter ci = (ClassInterpreter) interpreter;

            for (final SClassDefinition cdef : ci.getClasses()) {
                try {
                    cdef.apply(new DepthFirstAnalysisAdaptor() {

                        @Override
                        public void caseAIfStm(AIfStm node) throws AnalysisException {
                            ctx.if_statement(node);
                        }
                    });
                } catch (AnalysisException e) {
                    e.printStackTrace();
                }
            }
        }
        return ctx;
    }

    public void writeCoverage(PrintWriter out,Interpreter interpreter) {
>>>>>>> Stashed changes
        for (LexLocation l : LexLocation.getSourceLocations(filename)) {
            if (l.hits > 0) {
                out.println("+" + l.startLine + " " + l.startPos + "-"
                        + l.endPos + "=" + l.hits);
            }
        }
    }


}
