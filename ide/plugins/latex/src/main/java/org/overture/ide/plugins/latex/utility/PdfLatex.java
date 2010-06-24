package org.overture.ide.plugins.latex.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.overture.ide.ui.internal.util.ConsoleWriter;

public class PdfLatex extends Thread
{

	private String documentName;
	private IProject project;
	private File outputFolder;

	private Process process;
	public boolean isFinished = false;

	public PdfLatex(IProject project, File outputFolder, String documentName)
	{
		this.documentName = documentName;
		this.outputFolder = outputFolder;
		this.project = project;
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

	@Override
	public void run()
	{

		ConsoleWriter cw = new ConsoleWriter("PDF LATEX");
		cw.Show();
		try
		{
			
			if (documentName.contains(new Character(File.separatorChar).toString()))
			{
//				Map<String, String> env = pb.environment();
//				// watch out here! this could be “PATH” or “path”
//				// Windows doesn’t care, but Java will
//				String path = (String) env.get("PATH");
//				env.put("PATH", path + File.pathSeparator
//						+ new File(documentName).getParentFile().getAbsolutePath());
//				path = (String) env.get("PATH");
				
				documentName = getRelativePath(new File(documentName), outputFolder);
			}
			
			String argument = "pdflatex " + documentName;
			cw.println("Starting: " + argument + "\nIn: "
					+ outputFolder.getAbsolutePath());
			ProcessBuilder pb = new ProcessBuilder(argument);
			pb.command("pdflatex","\""+documentName+"\"");
			pb.directory(outputFolder);
			
			process = pb.start();
			new ProcessConsolePrinter(cw, process.getInputStream()).start();
			new ProcessConsolePrinter(cw, process.getErrorStream()).start();

			process.waitFor();
			if (project != null)
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
			isFinished = true;
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e)
		{
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
		while ((count < filePathStack.size() - 1)
				&& (count < relativeToPathStack.size() - 1)
				&& file.equals(relativeTo))
		{
			count++;
			file = (File) filePathStack.get(count);
			relativeTo = (File) relativeToPathStack.get(count);
		}
		if (file.equals(relativeTo))
			count++;
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
}
