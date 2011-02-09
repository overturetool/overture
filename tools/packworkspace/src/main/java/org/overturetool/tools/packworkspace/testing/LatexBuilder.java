package org.overturetool.tools.packworkspace.testing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import org.overturetool.tools.packworkspace.ProjectPacker;
import org.overturetool.tools.packworkspace.latex.FileUtils;
import org.overturetool.tools.packworkspace.testing.ProjectTester.Phase;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.runtime.LatexSourceFile;
import org.overturetool.vdmj.runtime.SourceFile;

public class LatexBuilder
{
	final static String OUTPUT_FOLDER_NAME = "latex";
	final String PROJECT_INCLUDE_MODEL_FILES = "%PROJECT_INCLUDE_MODEL_FILES";
	final String TITLE = "%TITLE";
	final String AUTHOR = "%AUTHOR";
	ProjectPacker project;
	List<String> includes = new Vector<String>();
	File output = null;
	private String documentFileName = "";
	private String alternativeDocumentFileName = "";
	private Process p;
	private static List<Process> processes = new Vector<Process>();
	private static List<ProcessConsolePrinter> processConsolePrinters = new Vector<ProcessConsolePrinter>();

	public static void destroy()
	{
		for (Process p : LatexBuilder.processes)
		{
			if (p != null)
				p.destroy();
		}

		for (Thread t : processConsolePrinters)
		{
			if (t != null)
				t.interrupt();
		}
	}

	public File getLatexDirectory()
	{
		return output;
	}

	public LatexBuilder(ProjectPacker project)
	{
		this.project = project;
	}

	public void build(File reportLocation, Interpreter interpreter,
			String author) throws IOException
	{
		File projectDir = new File(reportLocation, project.getSettings().getName());

		output = new File(projectDir, OUTPUT_FOLDER_NAME);
		if (!output.exists())
			output.mkdirs();

		String languageStyleFolder = "";
		String styleName = "VDM_PP";
		switch (project.getDialect())
		{
			case VDM_PP:
				languageStyleFolder = "pp";
				styleName = "VDM_PP";
				break;
			case VDM_RT:
				languageStyleFolder = "rt";
				styleName = "VDM_RT";
				break;
			case VDM_SL:
				languageStyleFolder = "sl";
				styleName = "VDM_SL";
				break;

		}

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
			addInclude(texFile.getAbsolutePath());
			try
			{
				PrintWriter pw = new PrintWriter(texFile);
				//new LatexSourceFile(sf).printCoverage(pw, false, true, true);
				new LatexSourceFile(sf).print(pw, false, true, false,false);
				pw.flush();
				pw.close();

			} catch (FileNotFoundException e)
			{

				e.printStackTrace();
			}
		}
		String documentName = "";
		if (alternativeDocumentFileName == null
				|| alternativeDocumentFileName.length() == 0)
			documentName = saveDocument(output, project.getSettings().getName(), author);
		else
			documentName = alternativeDocumentFileName;
		p = Runtime.getRuntime().exec("pdflatex " + documentName, null, output);
		processes.add(p);

		ProcessConsolePrinter p1 = new ProcessConsolePrinter(new File(output, Phase.Latex
				+ "Err.txt"), p.getErrorStream());
		p1.start();

		ProcessConsolePrinter p2 = new ProcessConsolePrinter(new File(output, Phase.Latex
				+ "Out.txt"), p.getInputStream());
		p2.start();

		// try
		// {
		// p.waitFor();
		// } catch (InterruptedException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// p1.interrupt();
		// p2.interrupt();
	}

	public boolean isFinished()
	{
		try
		{
			if (p != null)
			{
				p.exitValue();
			}
		} catch (IllegalThreadStateException e)
		{
			return false;
		}
		return true;
	}

	public String saveDocument(File projectRoot, String name, String author)
			throws IOException
	{
		String document = FileUtils.readFile("/latex/document.tex");
		setDocumentFileName(name + ".tex");
		File latexRoot = output;
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

		FileUtils.writeFile(document, new File(output, getDocumentFileName()));
		return getDocumentFileName();
	}

	public boolean isBuild()
	{
		if (getDocumentFileName().length() > 5)
		{
			return new File(output, getDocumentFileName().substring(0, getDocumentFileName().length() - 4)
					+ ".pdf").exists();
		}
		return false;
	}

	public File getPdfFile()
	{
		if (getDocumentFileName().length() > 5)
		{
			return new File(output, getDocumentFileName().substring(0, getDocumentFileName().length() - 4)
					+ ".pdf");
		}
		return null;
	}

	public static String latexQuote(String s)
	{
		// Latex specials: \# \$ \% \^{} \& \_ \{ \} \~{} \\

		return s.replace("\\", "\\textbackslash ").replace("#", "\\#").replace("$", "\\$").replace("%", "\\%").replace("&", "\\&").replace("_", "\\_").replace("{", "\\{").replace("}", "\\}").replace("~", "\\~").replaceAll("\\^{1}", "\\\\^{}");
	}

	public void addInclude(String path)
	{
		if (!includes.contains(path))
			includes.add(path);
	}

	public void setDocumentFileName(String documentFileName)
	{
		this.documentFileName = documentFileName;
	}

	public String getDocumentFileName()
	{
		return documentFileName;
	}

	public void setAlternativeDocumentFileName(
			String alternativeDocumentFileName)
	{
		this.alternativeDocumentFileName = alternativeDocumentFileName;
	}

	public String getAlternativeDocumentFileName()
	{
		return alternativeDocumentFileName;
	}
}
