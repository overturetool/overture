package org.overture.ide.plugins.latex.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;
import org.overture.ide.plugins.latex.Activator;
import org.overture.ide.plugins.latex.utility.ProcessConsolePrinter;
import org.overture.ide.plugins.latex.utility.TreeSelectionLocater;
import org.overture.ide.utility.ConsoleWriter;
import org.overture.ide.utility.ProjectUtility;
import org.overture.ide.vdmpp.core.VdmPpCorePluginConstants;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overture.ide.vdmrt.core.VdmRtCorePluginConstants;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;
import org.overture.ide.vdmsl.core.VdmSlCorePluginConstants;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;

@SuppressWarnings("restriction")
public class LatexAction implements IObjectActionDelegate
{

	private Shell shell;
	private ConsoleWriter console;
	/**
	 * Constructor for Action1.
	 */
	public LatexAction() {
		super();
		console = new ConsoleWriter(shell);
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action)
	{

		try
		{
			IProject selectedProject = null;
			selectedProject = TreeSelectionLocater.getSelectedProject(action,
					selectedProject);
			if (selectedProject == null)
			{
				console.println(
						"Could not find selected project");
				return;
			}

			if (selectedProject.hasNature(VdmPpProjectNature.VDM_PP_NATURE))
				makeLatex(selectedProject, VdmPpCorePluginConstants.CONTENT_TYPE);
			if (selectedProject.hasNature(VdmSlProjectNature.VDM_SL_NATURE))
				makeLatex(selectedProject, VdmSlCorePluginConstants.CONTENT_TYPE);
			if (selectedProject.hasNature(VdmRtProjectNature.VDM_RT_NATURE))
				makeLatex(selectedProject, VdmRtCorePluginConstants.CONTENT_TYPE);

		} catch (Exception ex)
		{
			System.err.println(ex.getMessage() + ex.getStackTrace());
			console.println( ex);
		}

	}

	public URL getResource(String pluginId, String path)
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

		if (fullPathString == null)
		{
			return null;
		}
		return fullPathString;

	}

	private String readFile(String relativePath) throws IOException
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
	
	private static boolean checkFileForModelEnv(File f) throws IOException
	{
		BufferedReader input =null;
		try{
		String line=null;
		 input = new BufferedReader(new FileReader(f));
		while ((line = input.readLine()) != null) {
			if(line.contains(VDM_MODEL_ENV_BEGIN))
				return true;
		}
		}finally{
			if(input!=null)
				input.close();
		}
		return false;
	}
	
	private static File makeModelFile(File f, File oututFolder) throws IOException
	{
		File modelFile = new File(oututFolder, f.getName()+".tex");
		String line=null;
		BufferedReader input = new BufferedReader(new FileReader(f));
		
		FileWriter outputWriter = new FileWriter(modelFile);

		BufferedWriter outputStream = new BufferedWriter(outputWriter);
		
		outputStream.write("\n"+VDM_MODEL_ENV_BEGIN);
		while ((line = input.readLine()) != null) {
			outputStream.write("\n"+line);
		}
		outputStream.write("\n"+VDM_MODEL_ENV_END);
		input.close();
		outputStream.close();
		return modelFile;
	}
	
final static String VDM_MODEL_ENV_BEGIN ="\\begin{vdm_al}";
final static String VDM_MODEL_ENV_END ="\\end{vdm_al}";
	private void makeLatex(final IProject selectedProject, final String contentTypeId)
	{
		final Job expandJob = new Job("Renaming") {

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{

				monitor.worked(IProgressMonitor.UNKNOWN);
				try
				{
					File projectRoot = selectedProject.getLocation().toFile();
					File outputFolder = new File(projectRoot, "latex");
					if (!outputFolder.exists())
						outputFolder.mkdirs();
					String overturesty = readFile("latex/overture.sty");
					String overturelanguagedef = readFile("latex/overturelanguagedef.sty");
					String document = readFile("latex/document.tex");

					final String PROJECT_INCLUDE_MODEL_FILES = "%PROJECT_INCLUDE_MODEL_FILES";
					
					
					writeFile(outputFolder, "overture.sty",overturesty);
					writeFile(outputFolder, "overturelanguagedef.sty",overturelanguagedef);
					
					
					StringBuilder sb = new StringBuilder();
					
					File outputFolderForGeneratedModelFiles = new File(outputFolder,"generated");
					if(!outputFolderForGeneratedModelFiles.exists())
					outputFolderForGeneratedModelFiles.mkdirs();
					
					sb.append("\n"+"\\section{VDM Model of "+selectedProject.getName()+"}");
					for (IFile f :ProjectUtility.getFiles(selectedProject, contentTypeId))
					{
						File file = ProjectUtility.getFile(selectedProject, f);
						
						if(!checkFileForModelEnv(file))
							file = makeModelFile(file, outputFolderForGeneratedModelFiles);
						
						sb.append("\n"+"\\subsection{"+file.getName()+"}");
						
						if(file.getAbsolutePath().contains(projectRoot.getAbsolutePath()))
						{
							String path = file.getAbsolutePath().substring(projectRoot.getAbsolutePath().length());
							sb.append("\n"+"\\input{"+ (".."+path).replace('\\', '/')+"}");
						}else
							sb.append("\n"+"\\input{"+ file.getAbsolutePath().replace('\\', '/')+"}");
					}
					
				document=	document.replace(PROJECT_INCLUDE_MODEL_FILES, sb.toString());
					
				String documentFileName = selectedProject.getName()+".tex";
					writeFile(outputFolder,documentFileName ,document);
					
					
				Process p =	Runtime.getRuntime().exec("pdflatex "+documentFileName , null, outputFolder);
				
				ConsoleWriter cw = new ConsoleWriter(shell);
				
				 new ProcessConsolePrinter(cw, p.getInputStream()).start();
				 new ProcessConsolePrinter(cw, p.getErrorStream()).start();
				 
//				String line=null;
//				BufferedReader input = new BufferedReader(new InputStreamReader(p
//						.getInputStream()));
//				while ((line = input.readLine()) != null) {
//					cw.println(line);
//				}
//				
//				BufferedReader inputerr = new BufferedReader(new InputStreamReader(p
//						.getErrorStream()));
//				while ((line = inputerr.readLine()) != null) {
//					cw.println(line);
//				}

					
					selectedProject.refreshLocal(IResource.DEPTH_INFINITE, null);

				} catch (Exception e)
				{

					e.printStackTrace();
					return new Status(IStatus.ERROR,
							"org.overture.ide.umltrans",
							"Translation error",
							e);
				}

				monitor.done();
				// expandCompleted = true;

				return new Status(IStatus.OK,
						"org.overture.ide.umltrans",
						IStatus.OK,
						"Translation completed",
						null);

			}

		};
		expandJob.setPriority(Job.INTERACTIVE);
		expandJob.schedule(0);

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

	private void writeFile(File outputFolder, String fileName,String content)
			throws IOException
	{
		FileWriter outputFileReader = new FileWriter(new File(outputFolder,fileName));
		BufferedWriter outputStream = new BufferedWriter(outputFileReader);
		outputStream.write(content);
		outputStream.close();
	}

}
