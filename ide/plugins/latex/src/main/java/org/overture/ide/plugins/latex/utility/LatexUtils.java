package org.overture.ide.plugins.latex.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.util.definitions.ClassList;
import org.overture.ast.util.modules.ModuleList;
import org.overture.config.Settings;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.latex.Activator;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;
import org.overture.interpreter.runtime.LatexSourceFile;

public class LatexUtils extends LatexUtilsBase
{
	private Shell shell;

	public LatexUtils(Shell shell)
	{
		this.shell = shell;
	}

	public void makeLatex(final IVdmProject selectedProject,
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

							for (SClassDefinition classDefinition : classes)
							{
								createCoverage(latexBuilder, outputFolderForGeneratedModelFiles, outputFiles, classDefinition.getLocation().file, modelOnly);

							}
						} else if (selectedProject.getDialect() == Dialect.VDM_SL)
						{
							List<File> outputFiles = getFileChildern(vdmProject.getModelBuildPath().getOutput().getLocation().toFile());

							ModuleList modules = parseModules(selectedProject);

							for (AModuleModules classDefinition : modules)
							{
								for (File moduleFile : classDefinition.getFiles())
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
				Settings.dialect = vdmProject.getDialect();
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
}
