package org.overture.ide.plugins.latex.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
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
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.plugins.latex.Activator;
import org.overture.ide.plugins.latex.ILatexConstants;
import org.overture.ide.plugins.latex.utility.LatexBuilder;
import org.overture.ide.plugins.latex.utility.PdfLatex;
import org.overture.ide.plugins.latex.utility.TreeSelectionLocater;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.internal.util.ConsoleWriter;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;
import org.overture.ide.vdmpp.core.IVdmPpCoreConstants;
import org.overture.ide.vdmrt.core.IVdmRtCoreConstants;
import org.overture.ide.vdmsl.core.IVdmSlCoreConstants;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.runtime.LatexSourceFile;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.syntax.ModuleReader;

@SuppressWarnings("restriction")
public class LatexCoverageAction implements IObjectActionDelegate
{

	private Shell shell;
	private ConsoleWriter console;

	/**
	 * Constructor for Action1.
	 */
	public LatexCoverageAction()
	{
		super();
		console = new ConsoleWriter("LATEX");
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

			IVdmProject selectedProject = TreeSelectionLocater.getSelectedProject(action);
			if (selectedProject == null)
			{
				console.print("Could not find selected project");
				return;
			}
			IProject project = (IProject) selectedProject.getAdapter(IProject.class);

			if (project != null)
			{
				if (project.hasNature(IVdmPpCoreConstants.NATURE))
					makeLatex(selectedProject, Dialect.VDM_PP);
				if (project.hasNature(IVdmSlCoreConstants.NATURE))
					makeLatex(selectedProject, Dialect.VDM_SL);
				if (project.hasNature(IVdmRtCoreConstants.NATURE))
					makeLatex(selectedProject, Dialect.VDM_RT);
			}
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

	// final static String VDM_MODEL_ENV_BEGIN = "\\begin{vdm_al}";
	// final static String VDM_MODEL_ENV_END = "\\end{vdm_al}";

	private void makeLatex(final IVdmProject selectedProject,
	// final String contentTypeId, final String natureId,
			final Dialect dialect)
	{
		final Job expandJob = new Job("Builder coverage tex files.")
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{

				monitor.worked(IProgressMonitor.UNKNOWN);
				try
				{
					IProject project = (IProject) selectedProject.getAdapter(IProject.class);
					IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);
					Assert.isNotNull(project, "Project could not be adapted");

					File projectRoot = project.getLocation().toFile();
					File outputFolder = LatexBuilder.makeOutputFolder(project);
					LatexBuilder latexBuilder = new LatexBuilder();

					latexBuilder.prepare(project, dialect);

					File outputFolderForGeneratedModelFiles = new File(outputFolder, "specification");
					if (!outputFolderForGeneratedModelFiles.exists())
						outputFolderForGeneratedModelFiles.mkdirs();

					IVdmModel model = selectedProject.getModel();
					if (model == null || !model.isTypeCorrect())
					{
						shell.getDisplay().asyncExec(new Runnable()
						{

							public void run()
							{
								VdmTypeCheckerUi.typeCheck(shell, selectedProject);

							}
						});

					}
					// if ()
					{

						boolean modelOnly = modelOnly(vdmProject);
						LexLocation.resetLocations();
						if (selectedProject.getDialect() == Dialect.VDM_PP
								|| selectedProject.getDialect() == Dialect.VDM_RT)
						{

							ClassList classes = parseClasses(selectedProject);

							List<File> outputFiles = getFileChildern(vdmProject.getModelBuildPath().getOutput().getLocation().toFile());

							for (ClassDefinition classDefinition : classes)
							{
								createCoverage(latexBuilder, outputFolderForGeneratedModelFiles, outputFiles, classDefinition.location.file, modelOnly);

							}
						} else if (selectedProject.getDialect() == Dialect.VDM_SL)
						{
							List<File> outputFiles = getFileChildern(vdmProject.getModelBuildPath().getOutput().getLocation().toFile());

							ModuleList modules = parseModules(selectedProject);

							for (Module classDefinition : modules)
							{
								for (File moduleFile : classDefinition.files)
								{
									createCoverage(latexBuilder, outputFolderForGeneratedModelFiles, outputFiles, moduleFile, modelOnly);
								}
							}
						}
					}

					String documentFileName = selectedProject.getName()
							+ ".tex";

					latexBuilder.saveDocument(project, projectRoot, documentFileName);
					if (hasGenerateMainDocument(vdmProject))
						buildPdf(project, monitor, outputFolder, documentFileName);
					else
					{
						documentFileName = getDocument(vdmProject);
						if (!new File(documentFileName).exists())
						{
							return new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.OK, "Main document does not exist: "
									+ documentFileName, null);
						}
						outputFolder = LatexBuilder.makeOutputFolder(project);
						buildPdf(project, monitor, outputFolder, documentFileName);
					}
				} catch (Exception e)
				{

					e.printStackTrace();
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unknown error", e);
				}

				monitor.done();
				// expandCompleted = true;

				return new Status(IStatus.OK, Activator.PLUGIN_ID, IStatus.OK, "Translation completed", null);

			}

			private void buildPdf(final IProject selectedProject,
					IProgressMonitor monitor, File outputFolder,
					String documentFileName) throws InterruptedException,
					CoreException
			{
				PdfLatex pdflatex = new PdfLatex(selectedProject, outputFolder, documentFileName);
				pdflatex.start();

				while (!monitor.isCanceled() && !pdflatex.isFinished
						&& !pdflatex.hasFailed)
					Thread.sleep(500);

				if (monitor.isCanceled() || pdflatex.hasFailed)
				{
					pdflatex.kill();
					if (pdflatex.hasFailed)
					{
						VdmUIPlugin.logErrorMessage("PDF creation failed. Please inspect the pdf console for further information.");
					}
				} else
				{
					PdfLatex pdflatex2 = new PdfLatex(selectedProject, outputFolder, documentFileName);
					pdflatex2.start();

					while (!monitor.isCanceled() && !pdflatex2.isFinished
							&& !pdflatex.hasFailed)
						Thread.sleep(500);

					if (monitor.isCanceled() || pdflatex.hasFailed)
					{
						pdflatex2.kill();
						if (pdflatex.hasFailed)
						{
							VdmUIPlugin.logErrorMessage("PDF creation failed. Please inspect the pdf console for further information.");
						}
					}
				}

				selectedProject.refreshLocal(IResource.DEPTH_INFINITE, null);
			}

			private void createCoverage(LatexBuilder latexBuilder,
					File outputFolderForGeneratedModelFiles,
					List<File> outputFiles, File moduleFile, boolean modelOnly)
					throws IOException, FileNotFoundException, CoreException
			{
				if (isStandardLibarary(moduleFile))
					return;

				if (!outputFolderForGeneratedModelFiles.exists())
					outputFolderForGeneratedModelFiles.mkdirs();

				File texFile = new File(outputFolderForGeneratedModelFiles, moduleFile.getName().replace(" ", "")
						+ ".tex");
				if (texFile.exists())
					texFile.delete();

				for (int i = 0; i < outputFiles.size(); i++)
				{
					File file = outputFiles.get(i);
					// System.out.println("Compare with file: "
					// + file.getName());
					if (file.getName().toLowerCase().endsWith(".covtbl")
							&& (moduleFile.getName()).equals(getFileName(file)))
					{
						// System.out.println("Match");
						LexLocation.mergeHits(moduleFile, file);
						outputFiles.remove(i);

					}

				}

				IFile selectedModelFile = selectedProject.findIFile(moduleFile);
				String charset = selectedModelFile.getCharset();
				latexBuilder.addInclude(texFile.getAbsolutePath());
				// VDMJ.filecharset = "utf-8";
				LatexSourceFile f = new LatexSourceFile(moduleFile, charset);

				PrintWriter pw = new PrintWriter(texFile, charset);
				IProject project = (IProject) selectedProject.getAdapter(IProject.class);
				Assert.isNotNull(project, "Project could not be adapted");
				IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);
				if (markCoverage(vdmProject))
				{
					f.printCoverage(pw, false, modelOnly, insertCoverageTable(vdmProject));
				} else
				{
					f.print(pw, false, modelOnly, insertCoverageTable(vdmProject), false);
				}
				// ConsoleWriter cw = new ConsoleWriter("LATEX");
				// f.printCoverage(cw);
				pw.close();
			}

			private boolean isStandardLibarary(File moduleFile)
			{
				return moduleFile.getParentFile().getName().equalsIgnoreCase("lib");
			}

		};
		expandJob.setPriority(Job.BUILD);
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

	private ClassList parseClasses(final IVdmProject project)
			throws CoreException
	{
		ClassReader reader;
		ClassList classes = new ClassList();
		for (IVdmSourceUnit source : project.getSpecFiles())
		{
			String charset = source.getFile().getCharset();

			LexTokenReader ltr = new LexTokenReader(source.getSystemFile(), Dialect.VDM_RT, charset);
			reader = new ClassReader(ltr);

			classes.addAll(reader.readClasses());
		}
		return classes;
	}

	private ModuleList parseModules(final IVdmProject project)
			throws CoreException
	{
		ModuleReader reader;
		ModuleList modules = new ModuleList();
		for (IVdmSourceUnit source : project.getSpecFiles())
		{
			String charset = source.getFile().getCharset();

			LexTokenReader ltr = new LexTokenReader(source.getSystemFile(), Dialect.VDM_SL, charset);
			reader = new ModuleReader(ltr);

			modules.addAll(reader.readModules());

		}
		return modules;
	}

	public boolean hasGenerateMainDocument(IVdmProject project)
			throws CoreException
	{
		return project.getOptions().getGroup(Activator.PLUGIN_ID, true).getAttribute(ILatexConstants.LATEX_GENERATE_MAIN_DOCUMENT, true);
	}

	public String getDocument(IVdmProject project) throws CoreException
	{
		return project.getOptions().getGroup(Activator.PLUGIN_ID, true).getAttribute(ILatexConstants.LATEX_MAIN_DOCUMENT, "");
	}

	public boolean insertCoverageTable(IVdmProject project)
			throws CoreException
	{
		return project.getOptions().getGroup(Activator.PLUGIN_ID, true).getAttribute(ILatexConstants.LATEX_INCLUDE_COVERAGETABLE, true);
	}

	public boolean markCoverage(IVdmProject project) throws CoreException
	{
		return project.getOptions().getGroup(Activator.PLUGIN_ID, true).getAttribute(ILatexConstants.LATEX_MARK_COVERAGE, true);
	}

	public boolean modelOnly(IVdmProject project) throws CoreException
	{
		return project.getOptions().getGroup(Activator.PLUGIN_ID, true).getAttribute(ILatexConstants.LATEX_MODEL_ONLY, true);
	}
}
