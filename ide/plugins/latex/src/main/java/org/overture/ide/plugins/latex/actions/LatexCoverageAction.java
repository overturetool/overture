package org.overture.ide.plugins.latex.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
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
import org.overture.ide.ast.AstManager;
import org.overture.ide.ast.RootNode;
import org.overture.ide.plugins.latex.Activator;
import org.overture.ide.plugins.latex.utility.LatexBuilder;
import org.overture.ide.plugins.latex.utility.LatexProject;
import org.overture.ide.plugins.latex.utility.PdfLatex;
import org.overture.ide.plugins.latex.utility.TreeSelectionLocater;
import org.overture.ide.utility.ConsoleWriter;
import org.overture.ide.utility.IVdmProject;
import org.overture.ide.utility.ProjectUtility;
import org.overture.ide.utility.VdmProject;
import org.overture.ide.vdmpp.core.VdmPpCorePluginConstants;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overture.ide.vdmrt.core.VdmRtCorePluginConstants;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;
import org.overture.ide.vdmsl.core.VdmSlCorePluginConstants;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.runtime.SourceFile;
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
						VdmPpProjectNature.VDM_PP_NATURE,
						Dialect.VDM_PP);
			if (selectedProject.hasNature(VdmSlProjectNature.VDM_SL_NATURE))
				makeLatex(selectedProject,
						VdmSlCorePluginConstants.CONTENT_TYPE,
						VdmSlProjectNature.VDM_SL_NATURE,
						Dialect.VDM_SL);
			if (selectedProject.hasNature(VdmRtProjectNature.VDM_RT_NATURE))
				makeLatex(selectedProject,
						VdmRtCorePluginConstants.CONTENT_TYPE,
						VdmRtProjectNature.VDM_RT_NATURE,
						Dialect.VDM_RT);

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

	final static String VDM_MODEL_ENV_BEGIN = "\\begin{vdm_al}";
	final static String VDM_MODEL_ENV_END = "\\end{vdm_al}";

	private void makeLatex(final IProject selectedProject,
			final String contentTypeId, final String natureId,
			final Dialect dialect)
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
					latexBuilder.prepare(selectedProject, dialect);

					File outputFolder = LatexBuilder.makeOutputFolder(selectedProject);

					File outputFolderForGeneratedModelFiles = new File(outputFolder,
							"specification");
					if (!outputFolderForGeneratedModelFiles.exists())
						outputFolderForGeneratedModelFiles.mkdirs();

					RootNode root = AstManager.instance()
							.getRootNode(selectedProject, natureId);
					if (root == null || !root.isChecked())
					{
						IVdmProject vdmProject = new VdmProject(selectedProject);
						vdmProject.typeCheck(monitor);
						VdmProject.waitForBuidCompletion();
						root = AstManager.instance()
								.getRootNode(selectedProject, natureId);
					}
					if (root != null && root.isChecked())
					{
						LexLocation.resetLocations();
						if (root.hasClassList())
						{
							ClassList classlist = AstManager.instance()
									.getRootNode(selectedProject, natureId)
									.getClassList();

							ClassList classes = parseClasses(selectedProject,
									classlist);

							List<File> outputFiles = getFileChildern(new File(projectRoot,
									"generated"));

							for (ClassDefinition classDefinition : classes)
							{
								createCoverage(latexBuilder,
										outputFolderForGeneratedModelFiles,
										outputFiles,
										classDefinition.location.file);

							}
						} else if (root.hasModuleList())
						{
							ModuleList modulelist = AstManager.instance()
									.getRootNode(selectedProject, natureId)
									.getModuleList();

							List<File> outputFiles = getFileChildern(new File(projectRoot,
									"generated"));

							ModuleList modules = parseModules(selectedProject,
									modulelist);

							for (Module classDefinition : modules)
							{
								for (File moduleFile : classDefinition.files)
								{
									createCoverage(latexBuilder,
											outputFolderForGeneratedModelFiles,
											outputFiles,
											moduleFile);
								}
							}
						}
					}

					String documentFileName = selectedProject.getName()
							+ ".tex";

					latexBuilder.saveDocument(projectRoot, documentFileName);
					if (!new LatexProject(selectedProject).hasDocument())
						buildPdf(selectedProject,
								monitor,
								outputFolder,
								documentFileName);
					else
					{
						documentFileName =new LatexProject(selectedProject).getMainDocument(); 
						outputFolder = new File(documentFileName);
						buildPdf(selectedProject,
								monitor,
								outputFolder,
								documentFileName);
					}
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

			private void buildPdf(final IProject selectedProject,
					IProgressMonitor monitor, File outputFolder,
					String documentFileName) throws InterruptedException,
					CoreException
			{
				PdfLatex pdflatex = new PdfLatex(selectedProject,
						outputFolder,
						documentFileName);
				pdflatex.start();

				while (!monitor.isCanceled() && !pdflatex.isFinished)
					Thread.sleep(500);

				if (monitor.isCanceled())
					pdflatex.kill();

				PdfLatex pdflatex2 = new PdfLatex(selectedProject,
						outputFolder,
						documentFileName);
				pdflatex2.start();

				while (!monitor.isCanceled() && !pdflatex2.isFinished)
					Thread.sleep(500);

				if (monitor.isCanceled())
					pdflatex2.kill();

				selectedProject.refreshLocal(IResource.DEPTH_INFINITE, null);
			}

			private void createCoverage(LatexBuilder latexBuilder,
					File outputFolderForGeneratedModelFiles,
					List<File> outputFiles, File moduleFile)
					throws IOException, FileNotFoundException
			{
				if (isStandardLibarary(moduleFile))
					return;
				File texFile = new File(outputFolderForGeneratedModelFiles,
						moduleFile.getName().replace(" ", "") + ".tex");
				if (texFile.exists())
					texFile.delete();

				for (int i = 0; i < outputFiles.size(); i++)
				{
					File file = outputFiles.get(i);
					// System.out.println("Compare with file: "
					// + file.getName());
					if (file.getName().toLowerCase().endsWith(".cov")
							&& (moduleFile.getName()).equals(getFileName(file)))
					{
						System.out.println("Match");
						LexLocation.mergeHits(moduleFile, file);
						outputFiles.remove(i);

					}

				}
				latexBuilder.addInclude(texFile.getAbsolutePath());
				SourceFile f = new SourceFile(moduleFile);
				PrintWriter pw = new PrintWriter(texFile);
				f.printLatexCoverage(pw, false, true);
				ConsoleWriter cw = new ConsoleWriter(shell);
				f.printCoverage(cw);
				pw.close();
			}

			private boolean isStandardLibarary(File moduleFile)
			{
				String name = moduleFile.getAbsolutePath()
						.toLowerCase()
						.replace('\\', '/');
				return (name.endsWith("/lib/io.vdmpp")
						|| name.endsWith("/lib/io.vdmrt")
						|| name.endsWith("/lib/io.vdmsl")
						|| name.endsWith("/lib/math.vdmpp")
						|| name.endsWith("/lib/math.vdmrt")
						|| name.endsWith("/lib/math.vdmsl")
						|| name.endsWith("/lib/vdmutil.vdmpp")
						|| name.endsWith("/lib/vdmutil.vdmrt") || name.endsWith("/lib/vdmutil.vdmsl"));

			}

		};
		expandJob.setPriority(Job.INTERACTIVE);
		try
		{
			selectedProject.build(IncrementalProjectBuilder.FULL_BUILD, null);
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

	private ClassList parseClasses(final IProject selectedProject,
			ClassList classlist)
	{
		ClassReader reader;
		ClassList classes = new ClassList();
		for (ClassDefinition cd : classlist)
		{
			String charset = "";
			try
			{
				charset = ProjectUtility.findIFile(selectedProject,
						cd.location.file).getCharset();

			} catch (CoreException e)
			{
				e.printStackTrace();
			}

			LexTokenReader ltr = new LexTokenReader(cd.location.file,
					Dialect.VDM_RT,
					charset);
			reader = new ClassReader(ltr);

			classes.addAll(reader.readClasses());
		}
		return classes;
	}

	private ModuleList parseModules(final IProject selectedProject,
			ModuleList modulelist)
	{
		ModuleReader reader;
		ModuleList modules = new ModuleList();
		for (Module m : modulelist)
		{
			String charset = "";
			try
			{
				charset = ProjectUtility.findIFile(selectedProject,
						m.files.get(0)).getCharset();

			} catch (CoreException e)
			{
				e.printStackTrace();
			}

			for (File f : m.files)
			{
				LexTokenReader ltr = new LexTokenReader(f,
						Dialect.VDM_SL,
						charset);
				reader = new ModuleReader(ltr);

				modules.addAll(reader.readModules());
			}
		}
		return modules;
	}

}
