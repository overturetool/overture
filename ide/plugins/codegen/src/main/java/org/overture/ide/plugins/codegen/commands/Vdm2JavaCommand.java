/*
 * #%~
 * Code Generator Plugin
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
package org.overture.ide.plugins.codegen.commands;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.codegen.analysis.violations.InvalidNamesResult;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.analysis.violations.Violation;
import org.overture.codegen.assistant.AssistantManager;
import org.overture.codegen.assistant.LocationAssistantCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.ir.NodeInfo;
import org.overture.codegen.utils.AnalysisExceptionCG;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2java.IJavaCodeGenConstants;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.config.Settings;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.plugins.codegen.Activator;
import org.overture.ide.plugins.codegen.CodeGenConsole;
import org.overture.ide.plugins.codegen.ICodeGenConstants;
import org.overture.ide.plugins.codegen.util.PluginVdm2JavaUtil;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;

public class Vdm2JavaCommand extends AbstractHandler
{
	private AssistantManager assistantManager;

	public Vdm2JavaCommand()
	{
		this.assistantManager = new AssistantManager();
	}

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		// Validate project
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
		{
			return null;
		}

		IStructuredSelection structuredSelection = (IStructuredSelection) selection;

		Object firstElement = structuredSelection.getFirstElement();

		if (!(firstElement instanceof IProject))
		{
			return null;
		}

		final IProject project = (IProject) firstElement;
		final IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);

		try
		{
			Settings.release = vdmProject.getLanguageVersion();
			Settings.dialect = vdmProject.getDialect();
		} catch (CoreException e)
		{
			Activator.log("Problems setting VDM language version and dialect", e);
			e.printStackTrace();
		}

		CodeGenConsole.GetInstance().activate();

		deleteMarkers(project);

		final IVdmModel model = vdmProject.getModel();

		if (!PluginVdm2JavaUtil.isSupportedVdmDialect(vdmProject))
		{
			CodeGenConsole.GetInstance().println("Project : "
					+ project.getName()
					+ " is not supported by the Java code generator. Currently, VDM++ is the only supported dialect.");
			return null;
		}

		if (model == null)
		{
			CodeGenConsole.GetInstance().println("Could not get model for project: "
					+ project.getName());
			return null;
		}

		if (!model.isParseCorrect())
		{
			CodeGenConsole.GetInstance().println("Could not parse model: "
					+ project.getName());
			return null;
		}

		if (!model.isTypeChecked())
		{
			VdmTypeCheckerUi.typeCheck(HandlerUtil.getActiveShell(event), vdmProject);
		}

		if (!model.isTypeCorrect())
		{
			CodeGenConsole.GetInstance().println("Could not type check model: "
					+ project.getName());
			return null;
		}

		Job codeGenerate = new Job("Code generate")
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				// Begin code generation
				final JavaCodeGen vdm2java = new JavaCodeGen();

				IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
				boolean generateCharSeqsAsStrings = preferences.getBoolean(ICodeGenConstants.GENERATE_CHAR_SEQUENCES_AS_STRINGS);

				IRSettings irSettings = new IRSettings();
				irSettings.setCharSeqAsString(generateCharSeqsAsStrings);

				boolean disableCloning = preferences.getBoolean(ICodeGenConstants.DISABLE_CLONING);

				JavaSettings javaSettings = new JavaSettings();
				javaSettings.setDisableCloning(disableCloning);

				vdm2java.setSettings(irSettings);
				vdm2java.setJavaSettings(javaSettings);

				try
				{
					CodeGenConsole.GetInstance().clearConsole();
					CodeGenConsole.GetInstance().println("Starting VDM++ to Java code generation...\n");

					File outputFolder = PluginVdm2JavaUtil.getOutputFolder(vdmProject);

					// Clean folder with generated Java code
					GeneralUtils.deleteFolderContents(outputFolder);

					// Generate user specified classes
					List<IVdmSourceUnit> sources = model.getSourceUnits();
					List<SClassDefinition> mergedParseLists = PluginVdm2JavaUtil.mergeParseLists(sources);
					GeneratedData generatedData = vdm2java.generateJavaFromVdm(mergedParseLists);
					vdm2java.generateJavaSourceFiles(outputFolder, generatedData.getClasses());
					
					outputSkippedClasses(generatedData.getSkippedClasses());
					outputUserspecifiedModules(outputFolder, generatedData.getClasses());

					// Quotes generation
					outputQuotes(vdmProject, outputFolder, vdm2java, generatedData.getQuoteValues());

					InvalidNamesResult invalidNames = generatedData.getInvalidNamesResult();

					if (invalidNames != null && !invalidNames.isEmpty())
					{
						handleInvalidNames(invalidNames);
					}

					project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

				} catch (UnsupportedModelingException ex)
				{
					handleUnsupportedModeling(ex);
				} catch (AnalysisExceptionCG ex)
				{
					CodeGenConsole.GetInstance().println("Could not code generate VDM model: "
							+ ex.getMessage());
				} catch (Exception ex)
				{
					handleUnexpectedException(ex);
				}

				return Status.OK_STATUS;
			}
		};

		codeGenerate.schedule();

		return null;
	}

	private void deleteMarkers(IProject project)
	{
		if (project == null)
		{
			return;
		}

		try
		{
			project.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
		} catch (CoreException ex)
		{
			Activator.log("Could not delete markers for project: "
					+ project.toString(), ex);
			ex.printStackTrace();
		}
	}
	

	protected void outputSkippedClasses(List<String> skippedClasses)
	{
		if (!skippedClasses.isEmpty())
		{
			CodeGenConsole.GetInstance().print("Skipping classes with library names: ");

			for (String skippedClass : skippedClasses)
			{
				CodeGenConsole.GetInstance().print(skippedClass + " ");
			}

			CodeGenConsole.GetInstance().println("\n");
		}
	}

	private void outputUserspecifiedModules(File outputFolder,
			List<GeneratedModule> userspecifiedClasses)
	{
		for (GeneratedModule generatedModule : userspecifiedClasses)
		{
			if (generatedModule.hasMergeErrors())
			{
				CodeGenConsole.GetInstance().printErrorln(String.format("Could not generate Java for class %s. Following errors were found:", generatedModule.getName()));

				List<Exception> mergeErrors = generatedModule.getMergeErrors();

				for (Exception error : mergeErrors)
				{
					CodeGenConsole.GetInstance().printErrorln(error.toString());
				}
			} else if (!generatedModule.canBeGenerated())
			{
				LocationAssistantCG locationAssistant = assistantManager.getLocationAssistant();

				List<NodeInfo> unsupportedNodes = locationAssistant.getNodesLocationSorted(generatedModule.getUnsupportedNodes());
				CodeGenConsole.GetInstance().println("Could not code generate class: "
						+ generatedModule.getName() + ".");
				CodeGenConsole.GetInstance().println("Following constructs are not supported:");

				for (NodeInfo nodeInfo : unsupportedNodes)
				{
					String message = PluginVdm2JavaUtil.formatNodeString(nodeInfo, locationAssistant);
					CodeGenConsole.GetInstance().println(message);

					PluginVdm2JavaUtil.addMarkers(nodeInfo, locationAssistant);

				}
			} else
			{
				File javaFile = new File(outputFolder, generatedModule.getName()
						+ IJavaCodeGenConstants.JAVA_FILE_EXTENSION);
				CodeGenConsole.GetInstance().println("Generated class: "
						+ generatedModule.getName());
				CodeGenConsole.GetInstance().println("Java source file: "
						+ javaFile.getAbsolutePath());

			}

			CodeGenConsole.GetInstance().println("");
		}
	}

	private void outputQuotes(IVdmProject vdmProject, File outputFolder,
			JavaCodeGen vdm2java, GeneratedModule quotes) throws CoreException
	{
		if (quotes != null)
		{
			File quotesFolder = PluginVdm2JavaUtil.getQuotesFolder(vdmProject);
			vdm2java.generateJavaSourceFile(quotesFolder, quotes);

			CodeGenConsole.GetInstance().println("Quotes interface generated.");
			File quotesFile = new File(outputFolder, IRConstants.QUOTES_INTERFACE_NAME
					+ IJavaCodeGenConstants.JAVA_FILE_EXTENSION);
			CodeGenConsole.GetInstance().println("Java source file: "
					+ quotesFile.getAbsolutePath());
			CodeGenConsole.GetInstance().println("");
		}
	}

	private void handleUnexpectedException(Exception ex)
	{
		String errorMessage = "Unexpected exception caught when attempting to code generate VDM model.";

		Activator.log(errorMessage, ex);

		CodeGenConsole.GetInstance().println(errorMessage);
		CodeGenConsole.GetInstance().println(ex.getMessage());
		ex.printStackTrace();
	}

	private void handleUnsupportedModeling(UnsupportedModelingException ex)
	{
		CodeGenConsole.GetInstance().println("Could not code generate VDM model: "
				+ ex.getMessage());

		String violationStr = JavaCodeGenUtil.constructUnsupportedModelingString(ex);
		CodeGenConsole.GetInstance().println(violationStr);

		Set<Violation> violations = ex.getViolations();
		PluginVdm2JavaUtil.addMarkers("Modeling rule not supported", violations);
	}

	private void handleInvalidNames(InvalidNamesResult invalidNames)
	{
		String message = "The model either uses words that are reserved by Java, declares VDM types"
				+ " that uses Java type names or uses variable names that potentially"
				+ " conflict with code generated temporary variable names";

		CodeGenConsole.GetInstance().println("Warning: " + message);

		String violationStr = JavaCodeGenUtil.constructNameViolationsString(invalidNames);
		CodeGenConsole.GetInstance().println(violationStr);

		Set<Violation> typeNameViolations = invalidNames.getTypenameViolations();
		PluginVdm2JavaUtil.addMarkers("Type name violation", typeNameViolations);

		Set<Violation> reservedWordViolations = invalidNames.getReservedWordViolations();
		PluginVdm2JavaUtil.addMarkers("Reserved word violations", reservedWordViolations);
	}
}
