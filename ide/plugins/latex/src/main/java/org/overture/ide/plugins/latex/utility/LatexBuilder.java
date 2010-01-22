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
import org.overture.ide.plugins.latex.Activator;

@SuppressWarnings("restriction")
public class LatexBuilder {
	final static String OUTPUT_FOLDER_NAME = "latex";
	final String PROJECT_INCLUDE_MODEL_FILES = "%PROJECT_INCLUDE_MODEL_FILES";
	File outputFolder = null;
	List<String> includes = new Vector<String>();

	public void prepare(IProject project) throws IOException {
		outputFolder = makeOutputFolder(project);

		String overturesty = readFile("latex/overture.sty");
		String overturelanguagedef = readFile("latex/overturelanguagedef.sty");

		writeFile(outputFolder, "overture.sty", overturesty);
		writeFile(outputFolder, "overturelanguagedef.sty", overturelanguagedef);

	}

	public void saveDocument(File projectRoot, String name) throws IOException {
		String document = readFile("latex/document.tex");
		String documentFileName = name ;//+ ".tex";
File latexRoot = makeOutputFolder(projectRoot);
		StringBuilder sb = new StringBuilder();
		sb.append("\n\\section{"+projectRoot.getName().replace('\\', '/').substring(0,projectRoot.getName().length())+"}");
		for (String path : includes) {

			String includeName = path;
			includeName = includeName
					.substring(0, includeName.lastIndexOf('.'));
			includeName = includeName.substring(0, includeName.lastIndexOf('.'));
			String tmp = includeName.replace('\\', '/');
			includeName = tmp.substring(tmp.lastIndexOf('/')+1);

			sb.append("\n" + "\\subsection{" + includeName + "}");
												
			if (path.contains(latexRoot.getAbsolutePath())) {
				path = path.substring(latexRoot.getAbsolutePath().length());
//				sb.append("\n" + "\\input{" + (".." + path).replace('\\', '/')
//						+ "}");
				sb.append("\n" + "\\input{" + ( path).replace('\\', '/').substring(1,path.length())
						+ "}");
			} else
				sb.append("\n" + "\\input{" + path.replace('\\', '/') + "}");

		}
		document = document.replace(PROJECT_INCLUDE_MODEL_FILES, sb.toString());

		writeFile(outputFolder, documentFileName, document);
	}

	public void addInclude(String path) {
		if (!includes.contains(path))
			includes.add(path);
	}

	public static File makeOutputFolder(IProject project) {
		File projectRoot = project.getLocation().toFile();
		return makeOutputFolder(projectRoot);
	}
	
	public static File makeOutputFolder(File projectRoot) {
		
		File outputFolder = new File(projectRoot, "generated");
		if (!outputFolder.exists())
			outputFolder.mkdirs();
		
		File latexoutput = new File(outputFolder,OUTPUT_FOLDER_NAME);
		if (!latexoutput.exists())
			latexoutput.mkdirs();
		
		return latexoutput;
	}

	private static String readFile(String relativePath) throws IOException {
		URL tmp = getResource(Activator.PLUGIN_ID, relativePath);

		InputStreamReader reader = new InputStreamReader(tmp.openStream());
		// Create Buffered/PrintWriter Objects
		// BufferedReader inputStream = new BufferedReader(bin);
		StringBuilder sb = new StringBuilder();

		int inLine;
		while ((inLine = reader.read()) != -1) {
			sb.append((char) inLine);
		}
		return sb.toString();
	}

	private void writeFile(File outputFolder, String fileName, String content)
			throws IOException {
		FileWriter outputFileReader = new FileWriter(new File(outputFolder,
				fileName));
		BufferedWriter outputStream = new BufferedWriter(outputFileReader);
		outputStream.write(content);
		outputStream.close();
	}

	public static URL getResource(String pluginId, String path) {
		// if the bundle is not ready then there is no image
		Bundle bundle = Platform.getBundle(pluginId);
		if (!BundleUtility.isReady(bundle)) {
			return null;
		}

		// look for the image (this will check both the plugin and fragment
		// folders
		URL fullPathString = BundleUtility.find(bundle, path);
		if (fullPathString == null) {
			try {
				fullPathString = new URL(path);
			} catch (MalformedURLException e) {
				return null;
			}
		}

		if (fullPathString == null) {
			return null;
		}
		return fullPathString;

	}
}
