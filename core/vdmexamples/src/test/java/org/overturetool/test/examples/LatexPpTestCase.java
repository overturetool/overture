/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overturetool.test.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import org.overturetool.test.framework.results.Result;
import org.overturetool.test.util.FileUtils;
import org.overturetool.test.util.ProcessConsolePrinter;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.LatexSourceFile;
import org.overturetool.vdmj.runtime.SourceFile;

public class LatexPpTestCase extends TypeCheckPpTestCase
{
	final static String OUTPUT_FOLDER_NAME = "latex";
	final String PROJECT_INCLUDE_MODEL_FILES = "%PROJECT_INCLUDE_MODEL_FILES";
	final String TITLE = "%TITLE";
	final String AUTHOR = "%AUTHOR";

	private static final CharSequence styleName = "VDM_PP";
	private String languageStyleFolder = "pp";
	private String documentFileName;
	private Set<String> includes = new HashSet<String>();

	public LatexPpTestCase()
	{
	}

	public LatexPpTestCase(File file)
	{
		super(file);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void test() throws Exception
	{
		if (mode == ContentModed.None)
		{
			return;
		}

		Result<ClassList> res = typeCheck();
		if (res.errors.size() > 0)
		{
//			fail("Type check errors");
			compareResults(res, "latex.result");
			return;
		}

		File output = new File(file, "output");
		if (!output.exists())
		{
			output.mkdirs();
		}

		ClassInterpreter interpreter = new ClassInterpreter(res.result);
		interpreter.init(null);

		String overturesty = FileUtils.readFile("/latex/overture.sty");

		overturesty = overturesty.replace("OVERTURE_LANGUAGE", styleName);

		String overturelanguagedef = FileUtils.readFile("/latex/"
				+ languageStyleFolder + "/overturelanguagedef.sty");

		FileUtils.writeFile(overturesty, new File(output, "/overture.sty"));
		FileUtils.writeFile(overturelanguagedef, new File(output, "/overturelanguagedef.sty"));

		for (File f : interpreter.getSourceFiles())
		{
			SourceFile sf = interpreter.getSourceFile(f);
			File texFile = new File(output, sf.filename.getName() + ".tex");
			includes.add(texFile.getAbsolutePath());
			try
			{
				PrintWriter pw = new PrintWriter(texFile);
				// new LatexSourceFile(sf).printCoverage(pw, false, true, true);
				new LatexSourceFile(sf).print(pw, false, true, false, false);
				pw.flush();
				pw.close();

			} catch (FileNotFoundException e)
			{

				e.printStackTrace();
			}
		}
		String documentName = "";
		// if (alternativeDocumentFileName == null
		// || alternativeDocumentFileName.length() == 0)
		documentName = saveDocument(output, getName().substring(0,getName().indexOf(' ')).trim(), getReadme().getTexAuthor(), output);
		// else
		// documentName = alternativeDocumentFileName;
		Process p = Runtime.getRuntime().exec("pdflatex -halt-on-error "
				+ documentName, null, output);
		
		
		ProcessConsolePrinter p1 = new ProcessConsolePrinter(new File(output,  "Err.txt"), p.getInputStream());
		p1.start();

		for (int i = 0; i < 10; i++)
		{
			try
			{
				if (p.exitValue() != 0)
				{
					fail("Could not finish pdflatex");
					break;
				}
			} catch (Exception e)
			{
				Thread.sleep(1000);
			}
		}
		
		assertFalse(new File(output,documentName+"pdf").exists());
		
		try{
			p.destroy();
		}catch(Exception e)
		{
			
		}
		try{
			p1.stop();
		}catch(Exception e)
		{
			
		}
	}

	// compareResults(res.warnings, res.errors, res.result, "interpreter.result");

	public String saveDocument(File projectRoot, String name, String author,
			File latexRoot) throws IOException
	{
		String document = FileUtils.readFile("/latex/document.tex");
		documentFileName = name + ".tex";
		StringBuilder sb = new StringBuilder();
		String title = "Coverage Report: " + name;

		for (String path : includes)
		{

			String includeName = path;
			includeName = includeName.substring(0, includeName.lastIndexOf('.'));
			includeName = includeName.substring(0, includeName.lastIndexOf('.'));
			String tmp = includeName.replace('\\', '/');
			includeName = tmp.substring(tmp.lastIndexOf('/') + 1);

			sb.append("\n" + "\\section{" + latexQuote(includeName) + "}");

			if (path.contains(latexRoot.getAbsolutePath()))
			{
				path = path.substring(latexRoot.getAbsolutePath().length());
				// sb.append("\n" + "\\input{" + (".." + path).replace('\\',
				// '/')
				// + "}");
				sb.append("\n" + "\\input{"
						+ (path).replace('\\', '/').substring(1, path.length())
						+ "}");
			} else
				sb.append("\n" + "\\input{" + path.replace('\\', '/') + "}");

		}
		document = document.replace(TITLE, latexQuote(title)).replace(PROJECT_INCLUDE_MODEL_FILES, sb.toString());

		if (author != null && author.trim().length() != 0)
			document = document.replace(AUTHOR, "\\author{"
					+ latexQuote(author) + "}");

		FileUtils.writeFile(document, new File(latexRoot, documentFileName));
		return documentFileName;
	}

	public static String latexQuote(String s)
	{
		// Latex specials: \# \$ \% \^{} \& \_ \{ \} \~{} \\

		return s.replace("\\", "\\textbackslash ").replace("#", "\\#").replace("$", "\\$").replace("%", "\\%").replace("&", "\\&").replace("_", "\\_").replace("{", "\\{").replace("}", "\\}").replace("~", "\\~").replaceAll("\\^{1}", "\\\\^{}");
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_PP;
	}
}
