package org.overturetool.tools.packworkspace.testing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overturetool.tools.packworkspace.ProjectPacker;
import org.overturetool.tools.packworkspace.latex.FileUtils;
import org.overturetool.tools.packworkspace.testing.ProjectTester.Phase;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.runtime.SourceFile;

public class LatexBuilder
{
	final static String OUTPUT_FOLDER_NAME = "latex";
	final String PROJECT_INCLUDE_MODEL_FILES = "%PROJECT_INCLUDE_MODEL_FILES";
	final String TITLE = "%TITLE";
	ProjectPacker project;
	List<String> includes = new Vector<String>();
	File output = null;
	String documentFileName ="";
	
	public File getLatexDirectory()
	{
		return output;
	}

	public LatexBuilder(ProjectPacker project) {
		this.project = project;
	}

	public void build(File reportLocation, Interpreter interpreter)
			throws IOException
	{
		File projectDir = new File(reportLocation, project.getSettings()
				.getName());

		output = new File(projectDir, Phase.Latex.toString());
		if (!output.exists())
			output.mkdirs();

		String languageStyleFolder = "";
		switch (project.getDialect())
		{
		case VDM_PP:
			languageStyleFolder = "pp";
			break;
		case VDM_RT:
			languageStyleFolder = "rt";
			break;
		case VDM_SL:
			languageStyleFolder = "sl";
			break;

		}

		String overturesty = readFile("/latex/overture.sty");
		String overturelanguagedef = readFile("/latex/" + languageStyleFolder
				+ "/overturelanguagedef.sty");

		writeFile(output, "/overture.sty", overturesty);
		writeFile(output, "/overturelanguagedef.sty", overturelanguagedef);

		for (File f : interpreter.getSourceFiles())
		{
			SourceFile sf = interpreter.getSourceFile(f);
			File texFile = new File(output, sf.filename.getName() + ".tex");
			addInclude(texFile.getAbsolutePath());
			try
			{
				PrintWriter pw = new PrintWriter(texFile);
				sf.printLatexCoverage(pw, false, true);
				pw.flush();
				pw.close();

			} catch (FileNotFoundException e)
			{

				e.printStackTrace();
			}
		}
		String documentName = saveDocument(output, project.getSettings()
				.getName());
		Process p = Runtime.getRuntime().exec("pdflatex.exe " + documentName,
				null,
				output);

		ProcessConsolePrinter p1=	new ProcessConsolePrinter(new File(output, Phase.Latex + "Err.txt"),
				p.getErrorStream());
		p1.start();

		ProcessConsolePrinter	p2=	new ProcessConsolePrinter(new File(output, Phase.Latex + "Out.txt"),
				p.getInputStream());
	p2.start();
	
//	try
//	{
//		p.waitFor();
//	} catch (InterruptedException e)
//	{
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	p1.interrupt();
//	p2.interrupt();
	}

	public String saveDocument(File projectRoot, String name)
			throws IOException
	{
		String document = readFile("/latex/document.tex");
		 documentFileName = name + ".tex";
		File latexRoot = output;
		StringBuilder sb = new StringBuilder();
		String title = "Coverage Report: "
				+ name;

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
		document = document.replace(TITLE, latexQuote(title))
				.replace(PROJECT_INCLUDE_MODEL_FILES, sb.toString());

		writeFile(output, documentFileName, document);
		return documentFileName;
	}
	
	public boolean isBuild()
	{
		return new File(documentFileName).exists();
	}

	private static String readFile(String relativePath) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		for (String s : FileUtils.readTextFromJar(relativePath))
		{
			sb.append("\n" + s);
		}
		return sb.toString();
	}

	private void writeFile(File outputFolder, String fileName, String content)
			throws IOException
	{
		FileWriter outputFileReader = new FileWriter(new File(outputFolder,
				fileName), false);
		BufferedWriter outputStream = new BufferedWriter(outputFileReader);
		outputStream.write(content);
		outputStream.close();
		outputFileReader.close();
	}

	public static String latexQuote(String s)
	{
		// Latex specials: \# \$ \% \^{} \& \_ \{ \} \~{} \\

		return s.replace("\\", "\\textbackslash ")
				.replace("#", "\\#")
				.replace("$", "\\$")
				.replace("%", "\\%")
				.replace("&", "\\&")
				.replace("_", "\\_")
				.replace("{", "\\{")
				.replace("}", "\\}")
				.replace("~", "\\~")
				.replaceAll("\\^{1}", "\\\\^{}");
	}

	public void addInclude(String path)
	{
		if (!includes.contains(path))
			includes.add(path);
	}
}
