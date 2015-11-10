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

import org.apache.commons.lang.SystemUtils;
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
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.prefs.Preferences;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.analysis.vdm.Renaming;
import org.overture.codegen.analysis.violations.InvalidNamesResult;
import org.overture.codegen.analysis.violations.Violation;
import org.overture.codegen.assistant.AssistantManager;
import org.overture.codegen.assistant.LocationAssistantCG;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.ir.VdmNodeInfo;
import org.overture.codegen.utils.AnalysisExceptionCG;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2java.IJavaConstants;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.codegen.vdm2jml.JmlSettings;
import org.overture.config.Settings;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;
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
		CodeGenConsole.GetInstance().clearConsole();
		
		deleteMarkers(project);

		final IVdmModel model = vdmProject.getModel();

		if (!PluginVdm2JavaUtil.isSupportedVdmDialect(vdmProject))
		{
			CodeGenConsole.GetInstance().println("The project  '"
					+ project.getName()
					+ "' is not supported by the Java code generator. "
					+ "The only dialects being supported are VDM-SL and VDM++.");
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
		
		CodeGenConsole.GetInstance().println("Starting VDM to Java code generation...\n");
		
		final List<String> classesToSkip = PluginVdm2JavaUtil.getClassesToSkip();
		final JavaSettings javaSettings = getJavaSettings(project, classesToSkip);
		
		final IRSettings irSettings = getIrSettings(project);
		
		Job codeGenerate = new Job("VDM to Java code generation")
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				if(javaSettings == null)
				{
					return Status.CANCEL_STATUS;
				}
				
				// Begin code generation
				final JavaCodeGen vdm2java = new JavaCodeGen();
				vdm2java.setSettings(irSettings);
				vdm2java.setJavaSettings(javaSettings);

				try
				{
					File eclipseProjectFolder = PluginVdm2JavaUtil.getEclipseProjectFolder(vdmProject);
					
					// Clean folder with generated Java code
					GeneralUtils.deleteFolderContents(eclipseProjectFolder, true);

					// Generate user specified classes
					GeneratedData generatedData = generateJava(vdmProject, model, vdm2java);
					
					outputUserSpecifiedSkippedClasses(classesToSkip);
					outputSkippedClasses(generatedData.getSkippedClasses());
					
					File javaCodeOutputFolder = PluginVdm2JavaUtil.getJavaCodeOutputFolder(vdmProject, javaSettings);
					
					try
					{
						vdm2java.genJavaSourceFiles(javaCodeOutputFolder, generatedData.getClasses());
					} catch (Exception e)
					{
						CodeGenConsole.GetInstance().printErrorln("Problems saving the code generated Java source files to disk.");
						CodeGenConsole.GetInstance().printErrorln("Try to run Overture with write permissions.\n");
						
						if(SystemUtils.IS_OS_WINDOWS)
						{
							CodeGenConsole.GetInstance().println("Operating System: Windows.");
							CodeGenConsole.GetInstance().println("If you installed Overture in a location such as \"C:\\Program Files\\Overture\"");
							CodeGenConsole.GetInstance().println("you may need to give Overture permissions to write to the file system. You can try");
							CodeGenConsole.GetInstance().println("run Overture as administrator and see if this solves the problem.");
						}
						
						return Status.CANCEL_STATUS;
					}
					
					File libFolder = PluginVdm2JavaUtil.getCodeGenRuntimeLibFolder(vdmProject);
					
					try
					{
						PluginVdm2JavaUtil.copyCodeGenFile(PluginVdm2JavaUtil.CODEGEN_RUNTIME_BIN_FILE, libFolder);
						outputRuntimeBinaries(libFolder);
					}
					catch(Exception e)
					{
						CodeGenConsole.GetInstance().printErrorln("Problems copying the Java code generator runtime library to " + libFolder.getAbsolutePath());
						CodeGenConsole.GetInstance().printErrorln("Reason: " + e.getMessage());
					}
					
					try
					{
						PluginVdm2JavaUtil.copyCodeGenFile(PluginVdm2JavaUtil.CODEGEN_RUNTIME_SOURCES_FILE, libFolder);
						outputRuntimeSources(libFolder);
					}
					catch(Exception e)
					{
						CodeGenConsole.GetInstance().printErrorln("Problems copying the Java code generator runtime library sources to " + libFolder.getAbsolutePath());
						CodeGenConsole.GetInstance().printErrorln("Reason: " + e.getMessage());
					}
					
					if(generateJml(vdmProject))
					{
						try
						{
							PluginVdm2JavaUtil.copyCodeGenFile(PluginVdm2JavaUtil.VDM2JML_RUNTIME_BIN_FILE, libFolder);
							outputVdm2JmlBinaries(libFolder);
						}
						catch(Exception e)
						{
							CodeGenConsole.GetInstance().printErrorln("Problems copying the VDM-to-JML runtime library to " + libFolder.getAbsolutePath());
							CodeGenConsole.GetInstance().printErrorln("Reason: " + e.getMessage());
						}
						
						try
						{
							PluginVdm2JavaUtil.copyCodeGenFile(PluginVdm2JavaUtil.VDM2JML_RUNTIME_SOURCES_FILE, libFolder);
							outputVdm2JmlSources(libFolder);
						}
						catch(Exception e)
						{
							CodeGenConsole.GetInstance().printErrorln("Problems copying the VDM-to-JML runtime library sources to " + libFolder.getAbsolutePath());
							CodeGenConsole.GetInstance().printErrorln("Reason: " + e.getMessage());
						}
					}
					
					try
					{
						PluginVdm2JavaUtil.copyCodeGenFile(PluginVdm2JavaUtil.ECLIPSE_RES_FILES_FOLDER +  "/"
								+ PluginVdm2JavaUtil.ECLIPSE_PROJECT_TEMPLATE_FILE, PluginVdm2JavaUtil.ECLIPSE_PROJECT_FILE, eclipseProjectFolder);
						
						GeneralCodeGenUtils.replaceInFile(new File(eclipseProjectFolder, PluginVdm2JavaUtil.ECLIPSE_PROJECT_FILE), "%s", project.getName());
						
						
						PluginVdm2JavaUtil.copyCodeGenFile(PluginVdm2JavaUtil.ECLIPSE_RES_FILES_FOLDER +  "/"
								+ PluginVdm2JavaUtil.ECLIPSE_CLASSPATH_TEMPLATE_FILE, PluginVdm2JavaUtil.ECLIPSE_CLASSPATH_FILE, eclipseProjectFolder);
						
						// Always imports codegen-runtime.jar
						String classPathEntries =  PluginVdm2JavaUtil.RUNTIME_CLASSPATH_ENTRY;
						
						if(generateJml(vdmProject))
						{
							// Import the VDM-to-JML runtime
							classPathEntries += PluginVdm2JavaUtil.VDM2JML_CLASSPATH_ENTRY;
						}
						
						GeneralCodeGenUtils.replaceInFile(new File(eclipseProjectFolder, PluginVdm2JavaUtil.ECLIPSE_CLASSPATH_FILE), "%s", classPathEntries);
						
						
						CodeGenConsole.GetInstance().println("Generated Eclipse project with Java generated code.\n");

					} catch (Exception e)
					{
						e.printStackTrace();
						CodeGenConsole.GetInstance().printErrorln("Problems generating the eclipse project with the generated Java code");
						CodeGenConsole.GetInstance().printErrorln("Reason: "
								+ e.getMessage());
					}
					
					outputUserspecifiedModules(javaCodeOutputFolder, generatedData.getClasses());

					// Quotes generation
					outputQuotes(vdmProject, javaCodeOutputFolder, vdm2java, generatedData.getQuoteValues());

					// Renaming of variables shadowing other variables
					outputRenamings(generatedData.getAllRenamings());
					
					InvalidNamesResult invalidNames = generatedData.getInvalidNamesResult();

					if (invalidNames != null && !invalidNames.isEmpty())
					{
						handleInvalidNames(invalidNames);
					}
					
					// Output any warnings such as problems with the user's launch configuration
					outputWarnings(generatedData.getWarnings());

					
					// Summarize the code generation process
					int noOfClasses = generatedData.getClasses().size();
					
					String msg = String.format("...finished Java code generation (generated %s %s).", 
							noOfClasses, 
							noOfClasses == 1 ? "class" : "classes");
					
					CodeGenConsole.GetInstance().println(msg);

					project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

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
	
	public GeneratedData generateJava(final IVdmProject project,
			final IVdmModel model, final JavaCodeGen vdm2java)
			throws AnalysisException
	{
		if(project.getDialect() != Dialect.VDM_SL)
		{
			List<SClassDefinition> ast = PluginVdm2JavaUtil.getClasses(model.getSourceUnits());
			return vdm2java.generateJavaFromVdm(ast);			
		}
		else
		{
			List<AModuleModules> ast = PluginVdm2JavaUtil.getModules(model.getSourceUnits());
			
			if (generateJml(project))
			{
				JmlSettings jmlSettings = getJmlSettings();
				
				JmlGenerator jmlGen = new JmlGenerator(vdm2java);
				jmlGen.setJmlSettings(jmlSettings);
				
				return jmlGen.generateJml(ast);
			} else
			{
				return vdm2java.generateJavaFromVdmModules(ast);
			}
		}
	}


	private boolean generateJml(IVdmProject project)
	{
		return project.getDialect() == Dialect.VDM_SL && getPrefs().getBoolean(ICodeGenConstants.GENERATE_JML, ICodeGenConstants.GENERATE_JML_DEFAULT);
	}
	
	private JmlSettings getJmlSettings()
	{
		Preferences preferences = getPrefs();
		
		boolean useInvFor = preferences.getBoolean(ICodeGenConstants.JML_USE_INVARIANT_FOR, ICodeGenConstants.JML_USE_INVARIANT_FOR_DEFAULT);;
		
		JmlSettings jmlSettings = new JmlSettings();
		jmlSettings.setGenInvariantFor(useInvFor);
		
		return jmlSettings;
	}
	
	public IRSettings getIrSettings(final IProject project)
	{
		Preferences preferences = getPrefs();
		
		boolean generateCharSeqsAsStrings = preferences.getBoolean(ICodeGenConstants.GENERATE_CHAR_SEQUENCES_AS_STRINGS, ICodeGenConstants.GENERATE_CHAR_SEQUENCES_AS_STRING_DEFAULT);
		boolean generateConcMechanisms = preferences.getBoolean(ICodeGenConstants.GENERATE_CONCURRENCY_MECHANISMS, ICodeGenConstants.GENERATE_CONCURRENCY_MECHANISMS_DEFAULT);
		
		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(generateCharSeqsAsStrings);
		irSettings.setGenerateConc(generateConcMechanisms);
		
		return irSettings;
	}

	private Preferences getPrefs()
	{
		Preferences preferences = InstanceScope.INSTANCE.getNode(ICodeGenConstants.PLUGIN_ID);
		return preferences;
	}
	
	public JavaSettings getJavaSettings(final IProject project, List<String> classesToSkip)
	{
		Preferences preferences = getPrefs();
		
		boolean disableCloning = preferences.getBoolean(ICodeGenConstants.DISABLE_CLONING, ICodeGenConstants.DISABLE_CLONING_DEFAULT);
		String javaPackage = preferences.get(ICodeGenConstants.JAVA_PACKAGE, ICodeGenConstants.JAVA_PACKAGE_DEFAULT);
		
		JavaSettings javaSettings = new JavaSettings();
		javaSettings.setDisableCloning(disableCloning);
		javaSettings.setModulesToSkip(classesToSkip);
		javaSettings.setJavaRootPackage(javaPackage);
		
		if (!JavaCodeGenUtil.isValidJavaPackage(javaSettings.getJavaRootPackage()))
		{
			javaSettings.setJavaRootPackage(project.getName());
		}
		
		return javaSettings;
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

	private void outputWarnings(List<String> warnings)
	{
		if(warnings != null && !warnings.isEmpty())
		{
			for(String warning : warnings)
			{
				CodeGenConsole.GetInstance().println(PluginVdm2JavaUtil.WARNING + " " + warning);
			}
			
			CodeGenConsole.GetInstance().printErrorln("");
		}
	}
	
	private void outputUserSpecifiedSkippedClasses(
			List<String> userspecifiedSkippedClasses)
	{
		if (!userspecifiedSkippedClasses.isEmpty())
		{
			CodeGenConsole.GetInstance().print("User specified filtered classes: ");

			for (String skippedClass : userspecifiedSkippedClasses)
			{
				CodeGenConsole.GetInstance().print(skippedClass + " ");
			}

			CodeGenConsole.GetInstance().println("\n");
		}
		else
		{
			CodeGenConsole.GetInstance().println("No user specified classes to skip.\n");
		}
	}

	private void outputSkippedClasses(List<String> skippedClasses)
	{
		if (!skippedClasses.isEmpty())
		{
			CodeGenConsole.GetInstance().print("Skipping classes (user specified and library named): ");

			for (String skippedClass : skippedClasses)
			{
				CodeGenConsole.GetInstance().print(skippedClass + " ");
			}

			CodeGenConsole.GetInstance().println("\n");
		}
	}
	
	private void outputRenamings(List<Renaming> allRenamings)
	{
		if(!allRenamings.isEmpty())
		{
			CodeGenConsole.GetInstance().println("Due to variable shadowing or normalisation of Java identifiers the following renamings of variables have been made:");
			CodeGenConsole.GetInstance().println(GeneralCodeGenUtils.constructVarRenamingString(allRenamings));;
		}
	}
	
	private void outputRuntimeBinaries(File outputFolder)
	{
		File runtime = new File(outputFolder, PluginVdm2JavaUtil.CODEGEN_RUNTIME_BIN_FILE);
		CodeGenConsole.GetInstance().println("Copied the Java code generator runtime library to " + runtime.getAbsolutePath());
	}
	
	private void outputRuntimeSources(File outputFolder)
	{
		File runtime = new File(outputFolder, PluginVdm2JavaUtil.CODEGEN_RUNTIME_SOURCES_FILE);
		CodeGenConsole.GetInstance().println("Copied the Java code generator runtime library sources to " + runtime.getAbsolutePath() + "\n");
	}
	
	private void outputVdm2JmlBinaries(File outputFolder)
	{
		File vdm2jmlRuntime = new File(outputFolder, PluginVdm2JavaUtil.VDM2JML_RUNTIME_BIN_FILE);
		CodeGenConsole.GetInstance().println("Copied the VDM-to-JML runtime library to " + vdm2jmlRuntime.getAbsolutePath());
	}
	
	private void outputVdm2JmlSources(File outputFolder)
	{
		File vdm2jmlSources = new File(outputFolder, PluginVdm2JavaUtil.VDM2JML_RUNTIME_BIN_FILE);
		CodeGenConsole.GetInstance().println("Copied the VDM-to-JML runtime library sources to " + vdm2jmlSources.getAbsolutePath() + "\n");
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
				CodeGenConsole.GetInstance().println("Could not code generate class: "
						+ generatedModule.getName() + ".");
				
				if(generatedModule.hasUnsupportedIrNodes())
				{
					LocationAssistantCG locationAssistant = assistantManager.getLocationAssistant();

					List<VdmNodeInfo> unsupportedInIr = locationAssistant.getVdmNodeInfoLocationSorted(generatedModule.getUnsupportedInIr());
					CodeGenConsole.GetInstance().println("Following VDM constructs are not supported by the code generator:");

					for (VdmNodeInfo  nodeInfo : unsupportedInIr)
					{
						String message = PluginVdm2JavaUtil.formatNodeString(nodeInfo, locationAssistant);
						CodeGenConsole.GetInstance().println(message);

						PluginVdm2JavaUtil.addMarkers(nodeInfo, locationAssistant);
					}
				}
				
				if(generatedModule.hasUnsupportedTargLangNodes())
				{
					Set<IrNodeInfo> unsupportedInTargLang = generatedModule.getUnsupportedInTargLang();
					CodeGenConsole.GetInstance().println("Following constructs are not supported by the code generator:");

					for (IrNodeInfo  nodeInfo : unsupportedInTargLang)
					{
						CodeGenConsole.GetInstance().println(nodeInfo.toString());
					}
				}
				
			} else
			{
				File javaFile = new File(outputFolder, generatedModule.getName()
						+ IJavaConstants.JAVA_FILE_EXTENSION);
				CodeGenConsole.GetInstance().println("Generated class: "
						+ generatedModule.getName());
				CodeGenConsole.GetInstance().println("Java source file: "
						+ javaFile.getAbsolutePath());
				
				Set<IrNodeInfo> warnings = generatedModule.getTransformationWarnings();
				
				if(!warnings.isEmpty())
				{
					CodeGenConsole.GetInstance().println("The following warnings were found for class " + generatedModule.getName() + ":");

					for (IrNodeInfo  nodeInfo : warnings)
					{
						CodeGenConsole.GetInstance().println(nodeInfo.getReason());
					}
				}

			}

			CodeGenConsole.GetInstance().println("");
		}
	}

	private void outputQuotes(IVdmProject vdmProject, File outputFolder,
			JavaCodeGen vdm2java, List<GeneratedModule> quotes) throws CoreException
	{
		if (quotes != null && !quotes.isEmpty())
		{
			for(GeneratedModule q : quotes)
			{
				vdm2java.genJavaSourceFile(outputFolder, q);
			}

			CodeGenConsole.GetInstance().println("Quotes generated to folder: "
					+ outputFolder.getAbsolutePath());
			CodeGenConsole.GetInstance().println("");
		}
	}

	private void handleUnexpectedException(Exception ex)
	{
		String errorMessage = 
				"Unexpected problem encountered when attempting to code generate the VDM model.\n"
				+ "The details of this problem have been reported in the Error Log.";

		Activator.log(errorMessage, ex);
		CodeGenConsole.GetInstance().printErrorln(errorMessage);
		ex.printStackTrace();
	}

	private void handleInvalidNames(InvalidNamesResult invalidNames)
	{
		String message = "The model either uses words that are reserved by Java, declares VDM types"
				+ " that uses Java type names or uses variable names that potentially"
				+ " conflict with code generated temporary variable names";

		CodeGenConsole.GetInstance().println("Warning: " + message);

		String violationStr = GeneralCodeGenUtils.constructNameViolationsString(invalidNames);
		CodeGenConsole.GetInstance().println(violationStr);

		Set<Violation> typeNameViolations = invalidNames.getTypenameViolations();
		PluginVdm2JavaUtil.addMarkers("Type name violation", typeNameViolations);

		Set<Violation> reservedWordViolations = invalidNames.getReservedWordViolations();
		PluginVdm2JavaUtil.addMarkers("Reserved word violations", reservedWordViolations);
	}
}
