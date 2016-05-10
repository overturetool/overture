/*
 * #%~
 * VDM Code Generator
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
package org.overture.codegen.vdm2java;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.analysis.vdm.Renaming;
import org.overture.codegen.analysis.violations.InvalidNamesResult;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class JavaCodeGenMain
{
	// Command-line args
	public static final String OO_ARG = "-pp";
	public static final String RT_ARG = "-rt";
	public static final String SL_ARG = "-sl";
	public static final String CLASSIC = "-classic";
	public static final String VDM10 = "-vdm10";
	public static final String EXP_ARG = "-exp";
	public static final String FOLDER_ARG = "-folder";
	public static final String PRINT_ARG = "-print";
	public static final String PACKAGE_ARG = "-package";
	public static final String OUTPUT_ARG = "-output";
	public static final String VDM_ENTRY_EXP = "-entry";
	public static final String NO_CODE_FORMAT = "-nocodeformat";
	public static final String JUNIT4 = "-junit4";
	public static final String SEP_TEST_CODE = "-separate";
	public static final String VDM_LOC = "-vdmloc";
	public static final String NO_CLONING = "-nocloning";
	
	// Folder names
	private static final String GEN_MODEL_CODE_FOLDER = "main";
	private static final String GEN_TESTS_FOLDER = "test";
	
	public static void main(String[] args)
	{
		Settings.release = Release.VDM_10;

		JavaCodeGenMode cgMode = null;
		boolean printClasses = false;

		if (args == null || args.length <= 1)
		{
			usage("Too few arguments provided");
		}
		
		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(true);
		irSettings.setGeneratePreConds(false);
		irSettings.setGeneratePreCondChecks(false);
		irSettings.setGeneratePostConds(false);
		irSettings.setGeneratePostCondChecks(false);

		JavaSettings javaSettings = new JavaSettings();
		javaSettings.setDisableCloning(false);

		List<String> listArgs = Arrays.asList(args);
		String exp = null;
		File outputDir = null;
		
		List<File> files = new LinkedList<File>();

		Settings.release = Release.VDM_10;
		
		boolean separateTestCode = false;
		
		for (Iterator<String> i = listArgs.iterator(); i.hasNext();)
		{
			String arg = i.next();

			if (arg.equals(OO_ARG))
			{
				cgMode = JavaCodeGenMode.OO_SPEC;
				Settings.dialect = Dialect.VDM_PP;
			}
			else if(arg.equals(RT_ARG))
			{
				cgMode = JavaCodeGenMode.OO_SPEC;
				Settings.dialect = Dialect.VDM_RT;
			}
			else if(arg.equals(SL_ARG))
			{
				cgMode = JavaCodeGenMode.SL_SPEC;
				Settings.dialect = Dialect.VDM_SL;
			}
			else if(arg.equals(CLASSIC))
			{
				Settings.release = Release.CLASSIC;
			}
			else if(arg.equals(VDM10))
			{
				Settings.release = Release.VDM_10;
			}
			else if (arg.equals(EXP_ARG))
			{
				cgMode = JavaCodeGenMode.EXP;

				if (i.hasNext())
				{
					exp = i.next();
				} else
				{
					usage(EXP_ARG + " requires a VDM expression");
				}
			} else if (arg.equals(PRINT_ARG))
			{
				printClasses = true;
			} else if (arg.equals(FOLDER_ARG))
			{
				if (i.hasNext())
				{
					File path = new File(i.next());

					if (path.isDirectory())
					{
						files.addAll(filterFiles(GeneralUtils.getFiles(path)));
					} else
					{
						usage("Could not find path: " + path);
					}
				} else
				{
					usage(FOLDER_ARG + " requires a directory");
				}
			} else if(arg.equals(PACKAGE_ARG))
			{
				if(i.hasNext())
				{
					String javaPackage = i.next();
					
					if(JavaCodeGenUtil.isValidJavaPackage(javaPackage))
					{
						javaSettings.setJavaRootPackage(javaPackage);
					}
					else
					{
						Logger.getLog().printErrorln("Not a valid java package. Using the default java package instead..\n");
					}
				}
			}
			else if(arg.equals(OUTPUT_ARG))
			{
				if (i.hasNext())
				{
					outputDir = new File(i.next());
					outputDir.mkdirs();
					
					if (!outputDir.isDirectory())
					{
						usage(outputDir + " is not a directory");	
					}
					
				} else
				{
					usage(OUTPUT_ARG + " requires a directory");
				}
			}
			else if(arg.equals(VDM_ENTRY_EXP))
			{
				if(i.hasNext())
				{
					javaSettings.setVdmEntryExp(i.next());
				}
			}
			else if(arg.equals(NO_CODE_FORMAT))
			{
				javaSettings.setFormatCode(false);
			}
			else if(arg.equals(JUNIT4))
			{
				javaSettings.setGenJUnit4tests(true);
			}
			else if(arg.equals(SEP_TEST_CODE))
			{
				separateTestCode = true;
			}
			else if(arg.equals(VDM_LOC))
			{
				javaSettings.setPrintVdmLocations(true);
			}
			else if(arg.equals(NO_CLONING))
			{
				javaSettings.setDisableCloning(true);
			}
			else
			{
				// It's a file or a directory
				File file = new File(arg);

				if (file.isFile())
				{
					if (GeneralCodeGenUtils.isVdmSourceFile(file))
					{
						files.add(file);
					}
				} else
				{
					usage("Not a file: " + file);
				}
			}
		}
		
		if(Settings.dialect == null)
		{
			usage("No VDM dialect specified");
		}

		Logger.getLog().println("Starting code generation...\n");

		
		if(cgMode == JavaCodeGenMode.EXP)
		{
			handleExp(exp, irSettings, javaSettings, Settings.dialect);
		}
		else
		{
			if(files.isEmpty())
			{
				usage("Input files are missing");
			}
			
			if(outputDir == null && !printClasses)
			{
				Logger.getLog().println("No output directory specified - printing code generated classes instead..\n");
				printClasses = true;
			}
			
			if (cgMode == JavaCodeGenMode.OO_SPEC) {
				handleOo(files, irSettings, javaSettings, Settings.dialect,
						printClasses, outputDir, separateTestCode);
			} else if(cgMode == JavaCodeGenMode.SL_SPEC) {
				handleSl(files, irSettings, javaSettings, printClasses,
						outputDir, separateTestCode);
			}
			else
			{
				Logger.getLog().printErrorln("Unexpected dialect: " + cgMode);
			}
		}

		Logger.getLog().println("\nFinished code generation! Bye...");
	}

	private static void handleExp(String exp, IRSettings irSettings,
			JavaSettings javaSettings, Dialect dialect)
	{
		try
		{
			Settings.release = Release.VDM_10;
			Settings.dialect = Dialect.VDM_PP;
			
			Generated generated = JavaCodeGenUtil.generateJavaFromExp(exp, irSettings, javaSettings, dialect);

			if (generated.hasMergeErrors())
			{
				Logger.getLog().println(String.format("VDM expression '%s' could not be merged. Following merge errors were found:", exp));
				GeneralCodeGenUtils.printMergeErrors(generated.getMergeErrors());
			} else if (!generated.canBeGenerated())
			{
				Logger.getLog().println("Could not generate VDM expression: "
						+ exp);

				if (generated.hasUnsupportedIrNodes())
				{
					GeneralCodeGenUtils.printUnsupportedIrNodes(generated.getUnsupportedInIr());
				}

				if (generated.hasUnsupportedTargLangNodes())
				{
					GeneralCodeGenUtils.printUnsupportedNodes(generated.getUnsupportedInTargLang());
				}

			} else
			{
				Logger.getLog().println("Code generated expression: "
						+ generated.getContent().trim());
			}
		} catch (AnalysisException e)
		{
			Logger.getLog().println("Could not code generate model: "
					+ e.getMessage());

		}
	}
	
	public static void handleSl(List<File> files, IRSettings irSettings,
			JavaSettings javaSettings, boolean printCode, File outputDir, boolean separateTestCode)
	{
		try
		{
			JavaCodeGen vdmCodGen = new JavaCodeGen();
			vdmCodGen.setSettings(irSettings);
			vdmCodGen.setJavaSettings(javaSettings);
			
			Settings.dialect = Dialect.VDM_SL;
			TypeCheckResult<List<AModuleModules>> tcResult = TypeCheckerUtil.typeCheckSl(files);
			
			if(GeneralCodeGenUtils.hasErrors(tcResult))
			{
				Logger.getLog().printError("Found errors in VDM model:");
				Logger.getLog().printErrorln(GeneralCodeGenUtils.errorStr(tcResult));
				return;
			}
			
			GeneratedData data = vdmCodGen.generate(CodeGenBase.getNodes(tcResult.result));
			
			processData(printCode, outputDir, vdmCodGen, data, separateTestCode);

		} catch (AnalysisException e)
		{
			Logger.getLog().println("Could not code generate model: "
					+ e.getMessage());
		}
	}

	public static void handleOo(List<File> files, IRSettings irSettings,
			JavaSettings javaSettings, Dialect dialect, boolean printCode, File outputDir, boolean separateTestCode)
	{
		try
		{
			JavaCodeGen vdmCodGen = new JavaCodeGen();
			vdmCodGen.setSettings(irSettings);
			vdmCodGen.setJavaSettings(javaSettings);
			
			TypeCheckResult<List<SClassDefinition>> tcResult;
			
			if (dialect == Dialect.VDM_PP)
			{
				tcResult = TypeCheckerUtil.typeCheckPp(files);
			} else
			{
				tcResult = TypeCheckerUtil.typeCheckRt(files);
			}
			
			if(GeneralCodeGenUtils.hasErrors(tcResult))
			{
				Logger.getLog().printError("Found errors in VDM model:");
				Logger.getLog().printErrorln(GeneralCodeGenUtils.errorStr(tcResult));
				return;
			}
			
			GeneratedData data = vdmCodGen.generate(CodeGenBase.getNodes(tcResult.result));
			
			processData(printCode, outputDir, vdmCodGen, data, separateTestCode);

		} catch (AnalysisException e)
		{
			Logger.getLog().println("Could not code generate model: "
					+ e.getMessage());

		}
	}
	
	public static void processData(boolean printCode,
			final File outputDir, JavaCodeGen vdmCodGen, GeneratedData data, boolean separateTestCode) {
		List<GeneratedModule> generatedClasses = data.getClasses();

		Logger.getLog().println("");
		
		if(!generatedClasses.isEmpty())
		{
			for (GeneratedModule generatedClass : generatedClasses)
			{
				if (generatedClass.hasMergeErrors())
				{
					Logger.getLog().println(String.format("Class %s could not be merged. Following merge errors were found:", generatedClass.getName()));
	
					GeneralCodeGenUtils.printMergeErrors(generatedClass.getMergeErrors());
				} else if (!generatedClass.canBeGenerated())
				{
					Logger.getLog().println("Could not generate class: "
							+ generatedClass.getName() + "\n");
	
					if (generatedClass.hasUnsupportedIrNodes())
					{
						Logger.getLog().println("Following VDM constructs are not supported by the code generator:");
						GeneralCodeGenUtils.printUnsupportedIrNodes(generatedClass.getUnsupportedInIr());
					}
	
					if (generatedClass.hasUnsupportedTargLangNodes())
					{
						Logger.getLog().println("Following constructs are not supported by the code generator:");
						GeneralCodeGenUtils.printUnsupportedNodes(generatedClass.getUnsupportedInTargLang());
					}
	
				} else
				{
					
					if (outputDir != null)
					{
						if(separateTestCode)
						{
							if(generatedClass.isTestCase())
							{
								vdmCodGen.genJavaSourceFile(new File(outputDir, GEN_TESTS_FOLDER), generatedClass);
							}
							else
							{
								vdmCodGen.genJavaSourceFile(new File(outputDir, GEN_MODEL_CODE_FOLDER), generatedClass);
							}
						}
						else
						{
							vdmCodGen.genJavaSourceFile(outputDir, generatedClass);
						}
					}
					
					if (printCode)
					{
						Logger.getLog().println("**********");
						Logger.getLog().println(generatedClass.getContent());
						Logger.getLog().println("\n");
					} else
					{
						Logger.getLog().println("Generated class : "
								+ generatedClass.getName());
					}
	
					Set<IrNodeInfo> warnings = generatedClass.getTransformationWarnings();
	
					if (!warnings.isEmpty())
					{
						Logger.getLog().println("Following transformation warnings were found:");
						GeneralCodeGenUtils.printUnsupportedNodes(generatedClass.getTransformationWarnings());
					}
				}
			}
		}
		else
		{
			Logger.getLog().println("No classes were generated!");
		}

		List<GeneratedModule> quotes = data.getQuoteValues();

		Logger.getLog().println("\nGenerated following quotes:");

		if (quotes != null && !quotes.isEmpty())
		{
			if(outputDir != null)
			{
				for (GeneratedModule q : quotes)
				{
					if(separateTestCode)
					{
						vdmCodGen.genJavaSourceFile(new File(outputDir, GEN_MODEL_CODE_FOLDER), q);
					}
					else
					{
						vdmCodGen.genJavaSourceFile(outputDir, q);
					}
				}
			}
			
			for (GeneratedModule q : quotes)
			{
				Logger.getLog().print(q.getName() + " ");
			}

			Logger.getLog().println("");
		}

		InvalidNamesResult invalidName = data.getInvalidNamesResult();

		if (!invalidName.isEmpty())
		{
			Logger.getLog().println(GeneralCodeGenUtils.constructNameViolationsString(invalidName));
		}

		List<Renaming> allRenamings = data.getAllRenamings();

		if (!allRenamings.isEmpty())
		{
			Logger.getLog().println("\nDue to variable shadowing or normalisation of Java identifiers the following renamings of variables have been made: ");

			Logger.getLog().println(GeneralCodeGenUtils.constructVarRenamingString(allRenamings));
		}
		
		if(data.getWarnings() != null && !data.getWarnings().isEmpty())
		{
			Logger.getLog().println("");
			for(String w : data.getWarnings())
			{
				Logger.getLog().println("[WARNING] " + w);
			}
		}
	}
	
	public static List<File> filterFiles(List<File> files)
	{
		List<File> filtered = new LinkedList<File>();
		
		for(File f : files)
		{
			if(GeneralCodeGenUtils.isVdmSourceFile(f))
			{
				filtered.add(f);
			}
		}
		
		return filtered;
	}
	
	public static void usage(String msg)
	{
		Logger.getLog().printErrorln("VDM-to-Java Code Generator: " + msg + "\n");
		Logger.getLog().printErrorln("Usage: CodeGen <-oo | -sl | -rt | -exp> [<options>] [<files>]");
		Logger.getLog().printErrorln(OO_ARG
				+ ": code generate a VDMPP specification consisting of multiple .vdmpp files");
		Logger.getLog().printErrorln(SL_ARG
				+ ": code generate a VDMSL specification consisting of multiple .vdmsl files");
		Logger.getLog().printErrorln(RT_ARG
				+ ": code generate a limited part of a VDMRT specification consisting of multiple .vdmrt files");
		Logger.getLog().printErrorln(CLASSIC + ": code generate using the VDM classic language release");
		Logger.getLog().printErrorln(VDM10 + ": code generate using the VDM-10 language release");
		Logger.getLog().printErrorln(EXP_ARG + " <expression>: code generate a VDMPP expression");
		Logger.getLog().printErrorln(FOLDER_ARG + " <folder path>: a folder containing input vdm source files");
		Logger.getLog().printErrorln(PRINT_ARG + ": print the generated code to the console");
		Logger.getLog().printErrorln(PACKAGE_ARG
				+ " <java package>:  the output java package of the generated code (e.g. my.code)");
		Logger.getLog().printErrorln(OUTPUT_ARG + " <folder path>: the output folder of the generated code");
		Logger.getLog().printErrorln(VDM_ENTRY_EXP
				+ " <vdm entry point expression>: generate a Java main method based on the specified entry point");
		Logger.getLog().printErrorln(NO_CODE_FORMAT + ": to NOT format the generated Java code");
		Logger.getLog().printErrorln(JUNIT4 + ": to generate VDMUnit " + IRConstants.TEST_CASE + " sub-classes to JUnit4 tests");
		Logger.getLog().printErrorln(SEP_TEST_CODE + ": to place the code generated model and the test code into separate folders named '"
				+ GEN_MODEL_CODE_FOLDER + "' and '" + GEN_TESTS_FOLDER + "', respectively");
		Logger.getLog().printErrorln(VDM_LOC + ": Generate VDM location information for code generated constructs");
		Logger.getLog().printErrorln(NO_CLONING + ": To disable deep cloning of value types");

		// Terminate
		System.exit(1);
	}
}
