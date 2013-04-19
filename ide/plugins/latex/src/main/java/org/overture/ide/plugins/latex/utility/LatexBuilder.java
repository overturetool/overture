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
package org.overture.ide.plugins.latex.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;
import org.overture.ast.lex.Dialect;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.latex.Activator;

@SuppressWarnings("restriction")
public class LatexBuilder
{
	final static String OUTPUT_FOLDER_NAME = "latex";
	final String PROJECT_INCLUDE_MODEL_FILES = "%PROJECT_INCLUDE_MODEL_FILES";
	final String TITLE = "%TITLE";
	File outputFolder = null;
	List<String> includes = new Vector<String>();

	public void prepare(IProject project, Dialect dialect) throws IOException
	{
		outputFolder = makeOutputFolder(project);
//		String overturesty = readFile("latex/overture.sty");
//
//		String overturelanguagedef = readFile("latex/overturelanguagedef.sty");
//		
//		overturesty = overturesty.replaceAll("OVERTURE_LANGUAGE", dialect.toString());
//
//		writeFile(outputFolder, "overture.sty", overturesty);
//		writeFile(outputFolder, "overturelanguagedef.sty", overturelanguagedef);

	}

	public void saveDocument(IProject project,File projectRoot, String name) throws IOException
	{
		String document = readFile("latex/document.tex");
		String documentFileName = name;// + ".tex";
		File latexRoot = makeOutputFolder(project);
		StringBuilder sb = new StringBuilder();
		String title = //"Coverage Report: " + 
		projectRoot.getName().replace('\\', '/').substring(0,
						projectRoot.getName().length());

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

		writeFile(outputFolder, documentFileName, document);
	}
	
	private String latexQuote(String s)
	{
		// Latex specials: \# \$ \% \^{} \& \_ \{ \} \~{} \\

		return s.
			replace("\\", "\\textbackslash ").
			replace("#", "\\#").
			replace("$", "\\$").
			replace("%", "\\%").
			replace("&", "\\&").
			replace("_", "\\_").
			replace("{", "\\{").
			replace("}", "\\}").
			replace("~", "\\~").
			replaceAll("\\^{1}", "\\\\^{}");
	}

	public void addInclude(String path)
	{
		if (!includes.contains(path))
			includes.add(path);
	}

	public static File makeOutputFolder(IProject project)
	{
//		File projectRoot = project.getLocation().toFile();
//		return makeOutputFolder(project);
//	}
//
//	public static File makeOutputFolder(IProject  project)
//	{

		IVdmProject p = (IVdmProject) project.getAdapter(IVdmProject.class);
		File outputFolder = p.getModelBuildPath().getOutput().getLocation().toFile();//new File(projectRoot, "generated");
		if (!outputFolder.exists())
			outputFolder.mkdirs();

		File latexoutput = new File(outputFolder, OUTPUT_FOLDER_NAME);
		if (!latexoutput.exists())
			latexoutput.mkdirs();

		return latexoutput;
	}

	private static String readFile(String relativePath) throws IOException
	{
		URL tmp = getResource(Activator.PLUGIN_ID, relativePath);

		InputStreamReader reader = new InputStreamReader(tmp.openStream());
		// Create Buffered/PrintWriter Objects
		// BufferedReader inputStream = new BufferedReader(bin);
		StringBuilder sb = new StringBuilder();

		int inLine;
		while ((inLine = reader.read()) != -1)
		{
			sb.append((char) inLine);
		}
		return sb.toString();
	}

	private void writeFile(File outputFolder, String fileName, String content)
			throws IOException
	{
		FileWriter outputFileReader = new FileWriter(new File(outputFolder,
				fileName),false);
		BufferedWriter outputStream = new BufferedWriter(outputFileReader);
		outputStream.write(content);
		outputStream.close();
		outputFileReader.close();
	}

	public static URL getResource(String pluginId, String path)
	{
		// if the bundle is not ready then there is no image
		Bundle bundle = Platform.getBundle(pluginId);
		if (!BundleUtility.isReady(bundle))
		{
			return null;
		}

		// look for the image (this will check both the plugin and fragment
		// folders
		URL fullPathString = BundleUtility.find(bundle, path);
		if (fullPathString == null)
		{
			try
			{
				fullPathString = new URL(path);
			} catch (MalformedURLException e)
			{
				return null;
			}
		}

		return fullPathString;

	}
}
