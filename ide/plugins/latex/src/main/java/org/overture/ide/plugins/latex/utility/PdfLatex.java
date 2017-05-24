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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.overture.ide.plugins.latex.ILatexConstants;
import org.overture.ide.plugins.latex.LatexPlugin;
import org.overture.ide.ui.internal.util.ConsoleWriter;

public class PdfLatex extends Thread implements PdfGenerator
{

	private String documentName;
	private IProject project;
	private File outputFolder;

	private Process process;
	public boolean finished = false;
	public boolean failed = false;
	private String currentOS = null;
	private boolean latexFailed = false;

	public PdfLatex(IProject project, File outputFolder, String documentName)
	{
		this.documentName = documentName;
		this.outputFolder = outputFolder;
		this.project = project;
		currentOS = Platform.getOS();
	}

	public void kill()
	{
		try
		{
			process.destroy();
		} catch (Exception e)
		{
		}
	}

	public void setFail(boolean b)
	{
		this.latexFailed = b;
	}

	@Override
	public void run()
	{

		ConsoleWriter cw = new ConsoleWriter("PDF LATEX");
		cw.show();
		try
		{

			if (documentName.contains(new Character(File.separatorChar).toString()))
			{
				// Map<String, String> env = pb.environment();
				// // watch out here! this could be �PATH� or �path�
				// // Windows doesn�t care, but Java will
				// String path = (String) env.get("PATH");
				// env.put("PATH", path + File.pathSeparator
				// + new File(documentName).getParentFile().getAbsolutePath());
				// path = (String) env.get("PATH");

				documentName = getRelativePath(new File(documentName), outputFolder);
			}

			String argument = "pdflatex " + documentName;
			cw.println("Starting: " + argument + "\nIn: "
					+ outputFolder.getAbsolutePath());
			ProcessBuilder pb = new ProcessBuilder(argument);
			if (currentOS.equals(Platform.OS_MACOSX))
			{ // fix for MacOS
				String osxpath = LatexPlugin.getDefault().getPreferenceStore().getString(ILatexConstants.OSX_LATEX_PATH_PREFERENCE);
				if (osxpath.equals(""))
				{
					pb.command(ILatexConstants.DEFAULT_OSX_LATEX_PATH, "-interaction=nonstopmode", documentName);
				} else
				{
					pb.command(osxpath, "-interaction=nonstopmode", documentName);
				}
			} else
			{
				pb.command("pdflatex", "-interaction=nonstopmode", "\""
						+ documentName + "\"");
			}

			pb.directory(outputFolder);

			process = pb.start();
			new ProcessConsolePrinter(cw, process.getInputStream(), this).start();
			new ProcessConsolePrinter(cw, process.getErrorStream(), this).start();

			process.waitFor();

			if (project != null)
			{
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
			}

			if (latexFailed)
			{
				this.failed = true;
			}

			finished = true;
		} catch (IOException e)
		{
			this.failed = true;
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e)
		{
			this.failed = true;
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			this.failed = true;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String getRelativePath(File file, File relativeTo)
			throws IOException
	{
		file = new File(file + File.separator + "89243jmsjigs45u9w43545lkhj7").getParentFile();
		relativeTo = new File(relativeTo + File.separator
				+ "984mvcxbsfgqoykj30487df556").getParentFile();
		File origFile = file;
		File origRelativeTo = relativeTo;
		List<File> filePathStack = new ArrayList<File>();
		List<File> relativeToPathStack = new ArrayList<File>();
		// build the path stack info to compare it afterwards
		file = file.getCanonicalFile();
		while (file != null)
		{
			filePathStack.add(0, file);
			file = file.getParentFile();
		}
		relativeTo = relativeTo.getCanonicalFile();
		while (relativeTo != null)
		{
			relativeToPathStack.add(0, relativeTo);
			relativeTo = relativeTo.getParentFile();
		}
		// compare as long it goes
		int count = 0;
		file = (File) filePathStack.get(count);
		relativeTo = (File) relativeToPathStack.get(count);
		while (count < filePathStack.size() - 1
				&& count < relativeToPathStack.size() - 1
				&& file.equals(relativeTo))
		{
			count++;
			file = (File) filePathStack.get(count);
			relativeTo = (File) relativeToPathStack.get(count);
		}
		if (file.equals(relativeTo))
		{
			count++;
		}
		// up as far as necessary
		StringBuffer relString = new StringBuffer();
		for (int i = count; i < relativeToPathStack.size(); i++)
		{
			relString.append(".." + File.separator);
		}
		// now back down to the file
		for (int i = count; i < filePathStack.size() - 1; i++)
		{
			relString.append(((File) filePathStack.get(i)).getName()
					+ File.separator);
		}
		relString.append(((File) filePathStack.get(filePathStack.size() - 1)).getName());
		// just to test
		File relFile = new File(origRelativeTo.getAbsolutePath()
				+ File.separator + relString.toString());
		if (!relFile.getCanonicalFile().equals(origFile.getCanonicalFile()))
		{
			throw new IOException("Failed to find relative path.");
		}
		return relString.toString();
	}

	@Override
	public boolean isFinished()
	{
		return finished;
	}

	@Override
	public boolean hasFailed()
	{
		return failed;
	}
}
