package org.overture.ide.plugins.latex.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
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
import org.overture.ide.ast.AstManager;
import org.overture.ide.ast.RootNode;
import org.overture.ide.plugins.latex.Activator;
import org.overture.ide.plugins.latex.utility.LatexBuilder;
import org.overture.ide.plugins.latex.utility.PdfLatex;
import org.overture.ide.plugins.latex.utility.TreeSelectionLocater;
import org.overture.ide.utility.ConsoleWriter;
import org.overture.ide.vdmpp.core.VdmPpCorePluginConstants;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overture.ide.vdmrt.core.VdmRtCorePluginConstants;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;
import org.overture.ide.vdmsl.core.VdmSlCorePluginConstants;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.runtime.SourceFile;

@SuppressWarnings("restriction")
public class LatexCoverageAction implements IObjectActionDelegate
{

	private Shell shell;
	private ConsoleWriter console;

	/**
	 * Constructor for Action1.
	 */
	public LatexCoverageAction() {
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
				console.print("Could not find selected project");
				return;
			}

			if (selectedProject.hasNature(VdmPpProjectNature.VDM_PP_NATURE))
				makeLatex(selectedProject,
						VdmPpCorePluginConstants.CONTENT_TYPE,
						VdmPpProjectNature.VDM_PP_NATURE);
			if (selectedProject.hasNature(VdmSlProjectNature.VDM_SL_NATURE))
				makeLatex(selectedProject,
						VdmSlCorePluginConstants.CONTENT_TYPE,
						VdmSlProjectNature.VDM_SL_NATURE);
			if (selectedProject.hasNature(VdmRtProjectNature.VDM_RT_NATURE))
				makeLatex(selectedProject,
						VdmRtCorePluginConstants.CONTENT_TYPE,
						VdmRtProjectNature.VDM_RT_NATURE);

		} catch (Exception ex)
		{
			System.err.println(ex.getMessage() + ex.getStackTrace());
			console.print(ex);
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

	final static String VDM_MODEL_ENV_BEGIN = "\\begin{vdm_al}";
	final static String VDM_MODEL_ENV_END = "\\end{vdm_al}";

	private void makeLatex(final IProject selectedProject,
			final String contentTypeId, final String natureId)
	{
		final Job expandJob = new Job("Builder coverage tex files.") {

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{

				monitor.worked(IProgressMonitor.UNKNOWN);
				try
				{
					File projectRoot = selectedProject.getLocation().toFile();
					LatexBuilder latexBuilder = new LatexBuilder();
					latexBuilder.prepare(selectedProject);

					File outputFolder = LatexBuilder.makeOutputFolder(selectedProject);

					File outputFolderForGeneratedModelFiles = new File(outputFolder,
							"generated");
					if (!outputFolderForGeneratedModelFiles.exists())
						outputFolderForGeneratedModelFiles.mkdirs();

					RootNode root = AstManager.instance()
							.getRootNode(selectedProject, natureId);
					if (root == null || !root.isChecked())
					{
						selectedProject.build(IncrementalProjectBuilder.FULL_BUILD,
								monitor);
						return new Status(IStatus.OK,
								Activator.PLUGIN_ID,
								IStatus.CANCEL,
								"Project not type checked",
								null);
					}
					if (root != null && root.isChecked())
					{
						if (root.hasClassList())
						{
							ClassList classlist = AstManager.instance()
									.getRootNode(selectedProject, natureId)
									.getClassList();

							List<File> outputFiles = getFileChildern(new File(projectRoot,
									"generated"));
							LexLocation.clearLocations();
							for (ClassDefinition classDefinition : classlist)
							{
								File texFile = new File(outputFolderForGeneratedModelFiles,
										classDefinition.location.file.getName()
												+ ".tex");
								if (texFile.exists())
									texFile.delete();

								for (int i = 0; i < outputFiles.size(); i++)
								{
									File file = outputFiles.get(i);
									if (file.getName().endsWith(".cov")
											&& (classDefinition.location.file.getName()).equals(getFileName(file)))
									{
										LexLocation.mergeHits(classDefinition.location.file,
												file);
										outputFiles.remove(i);

										latexBuilder.addInclude(texFile.getAbsolutePath());
									}

								}
								SourceFile f = new SourceFile(classDefinition.location.file);
								PrintWriter pw = new PrintWriter(texFile);
								f.printLatexCoverage(pw, false);
								ConsoleWriter cw = new ConsoleWriter(shell);
								f.printCoverage(cw);
								pw.close();

							}
						} else if (root.hasModuleList())
						{
							ModuleList modulelist = AstManager.instance()
									.getRootNode(selectedProject, natureId)
									.getModuleList();

							List<File> outputFiles = getFileChildern(new File(projectRoot,
									"generated"));
							LexLocation.clearLocations();
							for (Module classDefinition : modulelist)
							{
								for (File moduleFile : classDefinition.files)
								{

									File texFile = new File(outputFolderForGeneratedModelFiles,
											moduleFile.getName() + ".tex");
									if (texFile.exists())
										texFile.delete();

									for (int i = 0; i < outputFiles.size(); i++)
									{
										File file = outputFiles.get(i);
										if (file.getName().endsWith(".cov")
												&& (moduleFile.getName()).equals(getFileName(file)))
										{
											LexLocation.mergeHits(moduleFile,
													file);
											outputFiles.remove(i);

											latexBuilder.addInclude(texFile.getAbsolutePath());
										}

									}
									SourceFile f = new SourceFile(moduleFile);
									PrintWriter pw = new PrintWriter(texFile);
									f.printLatexCoverage(pw, false);
									ConsoleWriter cw = new ConsoleWriter(shell);
									f.printCoverage(cw);
									pw.close();
								}
							}
						}
					}

					String documentFileName = selectedProject.getName()
							+ ".tex";

					latexBuilder.saveDocument(projectRoot, documentFileName);

					PdfLatex pdflatex = new PdfLatex(selectedProject,
							outputFolder,
							documentFileName);
					pdflatex.start();

					while (!monitor.isCanceled() && !pdflatex.isFinished)
						Thread.sleep(500);

					if (monitor.isCanceled())
						pdflatex.kill();

					selectedProject.refreshLocal(IResource.DEPTH_INFINITE, null);

				} catch (Exception e)
				{

					e.printStackTrace();
					return new Status(IStatus.ERROR,
							Activator.PLUGIN_ID,
							"Unknown error",
							e);
				}

				monitor.done();
				// expandCompleted = true;

				return new Status(IStatus.OK,
						Activator.PLUGIN_ID,
						IStatus.OK,
						"Translation completed",
						null);

			}

		};
		expandJob.setPriority(Job.INTERACTIVE);
		expandJob.schedule(0);

	}

	public static String getFileName(File file)
	{
		int index = file.getName().lastIndexOf('.');
		return file.getName().substring(0, index);

	}

	private static List<File> getFileChildern(File file)
	{
		List<File> list = new Vector<File>();

		if (file.isFile())
		{
			list.add(file);
			return list;
		}

		if (file != null && file.listFiles() != null)
			for (File file2 : file.listFiles())
			{
				list.addAll(getFileChildern(file2));
			}

		return list;

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

	private void writeFile(File outputFolder, String fileName, String content)
			throws IOException
	{
		FileWriter outputFileReader = new FileWriter(new File(outputFolder,
				fileName));
		BufferedWriter outputStream = new BufferedWriter(outputFileReader);
		outputStream.write(content);
		outputStream.close();
	}

}
