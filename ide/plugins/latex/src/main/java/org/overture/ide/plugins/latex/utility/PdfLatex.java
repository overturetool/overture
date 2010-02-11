package org.overture.ide.plugins.latex.utility;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.overture.ide.utility.ConsoleWriter;

public class PdfLatex extends Thread
{

	private String documentName;
	private IProject project;
	private File outputFolder;

	private Process process;
	public boolean isFinished = false;

	public PdfLatex(IProject project, File outputFolder, String documentName) {
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
			String argument = "pdflatex " + documentName;
			cw.println("Starting: "+argument+"\nIn: "+outputFolder.getAbsolutePath());
			process = Runtime.getRuntime().exec(argument,
					null,
					outputFolder);
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
}
