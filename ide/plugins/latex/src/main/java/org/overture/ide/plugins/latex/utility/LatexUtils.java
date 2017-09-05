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
import java.util.Vector;
import java.util.stream.Collectors;

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
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.CoverageUtil;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.modules.AModuleModules;
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
					// TODO: Check whether this must be the else case of (model == null || !model.isTypeCorrect())
					// if ()
					// {
						// Set the dialect to be used in the objects generating the TeX files
						Settings.dialect = dialect;

						// Obtain user settings for TeX generation options 
						boolean modelOnly            = modelOnly(vdmProject);
						boolean coverageTable        = insertCoverageTable(vdmProject);
						boolean markCoverage         = markCoverage(vdmProject);

						
						// Prepare coverage highlighting
						LexLocation.resetLocations();
						
						// Compute the model files (either VDM module or class containing files)
						List<File> modelFiles = new Vector<File>();

						if (selectedProject.getDialect() == Dialect.VDM_PP
								|| selectedProject.getDialect() == Dialect.VDM_RT)
							modelFiles = parseClasses(selectedProject)
									.stream()
									.map((SClassDefinition::getLocation))
									.map(ILexLocation::getFile)
									.collect(Collectors.toList());
						
					    if (selectedProject.getDialect() == Dialect.VDM_SL)			
					    	modelFiles = parseModules(selectedProject)
					    			.stream()
					    			.map(AModuleModules::getFiles)
					    			.flatMap(List::stream)
					    			.collect(Collectors.toList());


						
						if (markCoverage || coverageTable)
						{
							List<File> outputFiles = getFileChildern(vdmProject.getModelBuildPath()
									                                           .getOutput()
									                                           .getLocation()
									                                           .toFile())
									                 .stream().filter(file -> file.getName()
									                                              .toLowerCase()
									                                              .endsWith(".covtbl"))
									                 .collect(Collectors.toList());

							for (File modelFile : modelFiles)
							{
								for (int i = 0; i < outputFiles.size(); i++)
								{
									File file = outputFiles.get(i);
										
									if (file.getName().toLowerCase().endsWith(".covtbl")
											&& modelFile.getName().equals(getFileName(file)))
									{
										LexLocation.mergeHits(modelFile, file);
										outputFiles.remove(i);

									}
								}
							}		
						}
							
						CoverageUtil coverageUtil = new CoverageUtil(LexLocation.getAllLocations(), LexLocation.getNameSpans());
						
						// Create TeX files for each model file
						for (File modelFile : modelFiles)
						{
								createTeXFile(latexBuilder, 
										outputFolderForGeneratedModelFiles, 
										modelFile, 
										modelOnly,
										markCoverage,
										coverageTable, 
										coverageUtil);
						}
						
							
					//}

					// Create main TeX file
					String documentFileName = selectedProject.getName() + ".tex";

					latexBuilder.saveDocument(project, projectRoot, documentFileName, modelOnly);
					
					// Build the Pdf
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

			private void createTeXFile(PdfBuilder latexBuilder,
					File outputFolder,
				    File modelFile, boolean modelOnly,boolean markCoverage, boolean includeCoverageTable, CoverageUtil coverageUtil)
					throws IOException, FileNotFoundException, CoreException
			{
				// Standard library files not generated
				if (modelFile.getParentFile().getName().equalsIgnoreCase("lib"))
				{
					return;
				}

				// Prepare output folder
				if (!outputFolder.exists())
				{
					outputFolder.mkdirs();
				}


				// Prepare the TeX file for this module deleting previous version
				File texFile = new File(outputFolder, modelFile.getName().replace(" ", "") + ".tex");
				if (texFile.exists())
				{
					texFile.delete();
				}

				// Compute file encoding
				IFile selectedModelFile = selectedProject.findIFile(modelFile);
				String charset = selectedModelFile.getCharset();
				
				// Generate the TeX file
				PrintWriter pw = new PrintWriter(texFile, charset);
				
				if (LatexPlugin.usePdfLatex())
				{
					new LatexSourceFile(modelFile, charset).print(pw, false, modelOnly, includeCoverageTable, markCoverage, coverageUtil);
				} else
				{
					new XetexSourceFile(modelFile, charset).print(pw, false, modelOnly, includeCoverageTable, markCoverage, coverageUtil);
				}
				pw.close();
				
				// Add the current successfully generated TeX file to the main file include list 
				latexBuilder.addInclude(texFile.getAbsolutePath());

			}

		};
		expandJob.setPriority(Job.BUILD);
		expandJob.schedule(0);

	}
}
