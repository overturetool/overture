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
import org.overture.ide.plugins.latex.LatexPlugin;
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
		final Job expandJob = new Job("Generating tex files.")
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
					PdfBuilder latexBuilder = null;
					if (LatexPlugin.usePdfLatex())
					{
						latexBuilder = new LatexBuilder();
					} else
					{
						latexBuilder = new XetexBuilder();
					}

					latexBuilder.prepare(project, dialect);

					File outputFolderForGeneratedModelFiles = new File(outputFolder, "specification");
					if (!outputFolderForGeneratedModelFiles.exists())
					{
						outputFolderForGeneratedModelFiles.mkdirs();
					}

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
						boolean includeCoverageTable = insertCoverageTable(vdmProject);
						boolean markCoverage = markCoverage(vdmProject);
						LexLocation.resetLocations();
						if (selectedProject.getDialect() == Dialect.VDM_PP
								|| selectedProject.getDialect() == Dialect.VDM_RT)
						{

							ClassList classes = parseClasses(selectedProject);

							List<File> outputFiles = getFileChildern(vdmProject.getModelBuildPath().getOutput().getLocation().toFile());

							for (SClassDefinition classDefinition : classes)
							{
								createCoverage(latexBuilder, outputFolderForGeneratedModelFiles, outputFiles, classDefinition.getLocation().getFile(), modelOnly,markCoverage,includeCoverageTable);

							}
						} else if (selectedProject.getDialect() == Dialect.VDM_SL)
						{
							List<File> outputFiles = getFileChildern(vdmProject.getModelBuildPath().getOutput().getLocation().toFile());

							ModuleList modules = parseModules(selectedProject);

							for (AModuleModules classDefinition : modules)
							{
								for (File moduleFile : classDefinition.getFiles())
								{
									createCoverage(latexBuilder, outputFolderForGeneratedModelFiles, outputFiles, moduleFile, modelOnly,markCoverage,includeCoverageTable);
								}
							}
						}
					}

					String documentFileName = selectedProject.getName()
							+ ".tex";

					latexBuilder.saveDocument(project, projectRoot, documentFileName);
					if (hasGenerateMainDocument(vdmProject))
					{
						buildPdf(project, monitor, outputFolder, documentFileName);
					} else
					{
						documentFileName = getDocument(vdmProject);
						if (!new File(documentFileName).exists())
						{
							return new Status(IStatus.ERROR, LatexPlugin.PLUGIN_ID, IStatus.OK, "Main document does not exist: "
									+ documentFileName, null);
						}
						outputFolder = LatexBuilder.makeOutputFolder(project);
						buildPdf(project, monitor, outputFolder, documentFileName);
					}
				} catch (Exception e)
				{

					e.printStackTrace();
					return new Status(IStatus.ERROR, LatexPlugin.PLUGIN_ID, "Unknown error", e);
				}

				monitor.done();
				// expandCompleted = true;

				return new Status(IStatus.OK, LatexPlugin.PLUGIN_ID, IStatus.OK, "Translation completed", null);

			}

			private void buildPdf(final IProject selectedProject,
					IProgressMonitor monitor, File outputFolder,
					String documentFileName) throws InterruptedException,
					CoreException
			{
				PdfGenerator pdflatex = null;
				if (LatexPlugin.usePdfLatex())
				{
					pdflatex = new PdfLatex(selectedProject, outputFolder, documentFileName);
				} else
				{
					pdflatex = new Xetex(selectedProject, outputFolder, documentFileName);
				}
				pdflatex.start();

				while (!monitor.isCanceled() && !pdflatex.isFinished()
						&& !pdflatex.hasFailed())
				{
					Thread.sleep(500);
				}

				if (monitor.isCanceled() || pdflatex.hasFailed())
				{
					pdflatex.kill();
					if (pdflatex.hasFailed())
					{
						VdmUIPlugin.logErrorMessage("PDF creation failed. Please inspect the pdf console for further information.");
					}
				} else
				{
					PdfGenerator pdflatex2 = null;

					if (LatexPlugin.usePdfLatex())
					{
						pdflatex2 = new PdfLatex(selectedProject, outputFolder, documentFileName);
					} else
					{
						pdflatex2 = new Xetex(selectedProject, outputFolder, documentFileName);
					}
					pdflatex2.start();

					while (!monitor.isCanceled() && !pdflatex2.isFinished()
							&& !pdflatex.hasFailed())
					{
						Thread.sleep(500);
					}

					if (monitor.isCanceled() || pdflatex.hasFailed())
					{
						pdflatex2.kill();
						if (pdflatex.hasFailed())
						{
							VdmUIPlugin.logErrorMessage("PDF creation failed. Please inspect the pdf console for further information.");
						}
					}
				}

				selectedProject.refreshLocal(IResource.DEPTH_INFINITE, null);
			}

			private void createCoverage(PdfBuilder latexBuilder,
					File outputFolderForGeneratedModelFiles,
					List<File> outputFiles, File moduleFile, boolean modelOnly,boolean markCoverage, boolean includeCoverageTable)
					throws IOException, FileNotFoundException, CoreException
			{
				if (isStandardLibarary(moduleFile))
				{
					return;
				}

				if (!outputFolderForGeneratedModelFiles.exists())
				{
					outputFolderForGeneratedModelFiles.mkdirs();
				}

				File texFile = new File(outputFolderForGeneratedModelFiles, moduleFile.getName().replace(" ", "")
						+ ".tex");
				if (texFile.exists())
				{
					texFile.delete();
				}

				if (markCoverage || includeCoverageTable)
				{
					for (int i = 0; i < outputFiles.size(); i++)
					{
						File file = outputFiles.get(i);
						// System.out.println("Compare with file: "
						// + file.getName());
						if (file.getName().toLowerCase().endsWith(".covtbl")
								&& moduleFile.getName().equals(getFileName(file)))
						{
							// System.out.println("Match");
							LexLocation.mergeHits(moduleFile, file);
							outputFiles.remove(i);

						}
					}
				}

				IFile selectedModelFile = selectedProject.findIFile(moduleFile);
				String charset = selectedModelFile.getCharset();
				latexBuilder.addInclude(texFile.getAbsolutePath());
				// VDMJ.filecharset = "utf-8";

				PrintWriter pw = new PrintWriter(texFile, charset);
				IProject project = (IProject) selectedProject.getAdapter(IProject.class);
				Assert.isNotNull(project, "Project could not be adapted");
				IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);
				Settings.dialect = vdmProject.getDialect();
				if (markCoverage(vdmProject))
				{
					if (LatexPlugin.usePdfLatex())
					{
						new LatexSourceFile(moduleFile, charset).printCoverage(pw, false, modelOnly, insertCoverageTable(vdmProject));
					} else
					{
						new XetexSourceFile(moduleFile, charset).printCoverage(pw, false, modelOnly, insertCoverageTable(vdmProject));
					}
				} else
				{
					if (LatexPlugin.usePdfLatex())
					{
						new LatexSourceFile(moduleFile, charset).print(pw, false, modelOnly, insertCoverageTable(vdmProject), false);
					} else
					{
						new XetexSourceFile(moduleFile, charset).print(pw, false, modelOnly, insertCoverageTable(vdmProject), false);
					}
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
