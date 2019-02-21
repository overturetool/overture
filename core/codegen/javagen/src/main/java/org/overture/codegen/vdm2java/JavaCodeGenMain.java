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
import org.overture.codegen.printer.MsgPrinter;
import org.overture.codegen.utils.*;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

import java.io.File;
import java.util.*;

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
	public static final String NO_STRINGS = "-nostrings";
	public static final String CONC = "-concurrency";
	public static final String GEN_SYS_CLASS = "-gensysclass";
	public static final String NO_WARNINGS = "-nowarnings";
	public static final String GEN_PRE_CONDITIONS = "-pre";
	public static final String GEN_POST_CONDITIONS = "-post";
	public static final String GEN_INVARIANTS = "-inv";

	// Folder names
	private static final String GEN_MODEL_CODE_FOLDER = "main";
	private static final String GEN_TESTS_FOLDER = "test";

	public static void main(String[] args)
	{
		long clock = System.currentTimeMillis();
		Settings.release = Release.VDM_10;

		JavaCodeGenMode cgMode = null;
		boolean printClasses = false;
		boolean printWarnings = true;

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
			} else if (arg.equals(RT_ARG))
			{
				cgMode = JavaCodeGenMode.OO_SPEC;
				Settings.dialect = Dialect.VDM_RT;
			} else if (arg.equals(SL_ARG))
			{
				cgMode = JavaCodeGenMode.SL_SPEC;
				Settings.dialect = Dialect.VDM_SL;
			} else if (arg.equals(CLASSIC))
			{
				Settings.release = Release.CLASSIC;
			} else if (arg.equals(VDM10))
			{
				Settings.release = Release.VDM_10;
			} else if (arg.equals(NO_WARNINGS))
			{
				printWarnings = false;
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
			} else if (arg.equals(PACKAGE_ARG))
			{
				if (i.hasNext())
				{
					String javaPackage = i.next();

					if (JavaCodeGenUtil.isValidJavaPackage(javaPackage))
					{
						javaSettings.setJavaRootPackage(javaPackage);
					} else
					{
						MsgPrinter.getPrinter().errorln("Not a valid java package. Using the default java package instead..\n");
					}
				}
			} else if (arg.equals(OUTPUT_ARG))
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
			} else if (arg.equals(VDM_ENTRY_EXP))
			{
				if (i.hasNext())
				{
					javaSettings.setVdmEntryExp(i.next());
				}
			} else if (arg.equals(NO_CODE_FORMAT))
			{
				javaSettings.setFormatCode(false);
			} else if (arg.equals(JUNIT4))
			{
				javaSettings.setGenJUnit4tests(true);
			} else if (arg.equals(SEP_TEST_CODE))
			{
				separateTestCode = true;
			} else if (arg.equals(VDM_LOC))
			{
				javaSettings.setPrintVdmLocations(true);
			} else if (arg.equals(NO_CLONING))
			{
				javaSettings.setDisableCloning(true);
			} else if(arg.equals(NO_STRINGS))
			{
				irSettings.setCharSeqAsString(false);
			}
			else if(arg.equals(CONC))
			{
				irSettings.setGenerateConc(true);
			}
			else if(arg.equals(GEN_SYS_CLASS))
			{
				javaSettings.setGenSystemClass(true);
			}
			else if(arg.equals(GEN_PRE_CONDITIONS))
			{
				irSettings.setGeneratePreConds(true);
			}
			else if(arg.equals(GEN_POST_CONDITIONS))
			{
				irSettings.setGeneratePostConds(true);
			}
			else if(arg.equals(GEN_INVARIANTS))
			{
				irSettings.setGenerateInvariants(true);
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

		if (Settings.dialect == null)
		{
			usage("No VDM dialect specified");
		}

		MsgPrinter.getPrinter().println("Starting code generation...\n");

		if (cgMode == JavaCodeGenMode.EXP)
		{
			handleExp(exp, irSettings, javaSettings, Settings.dialect);
		} else
		{
			if (files.isEmpty())
			{
				usage("Input files are missing");
			}

			if (outputDir == null && !printClasses)
			{
				MsgPrinter.getPrinter().println("No output directory specified - printing code generated classes instead..\n");
				printClasses = true;
			}

			if (cgMode == JavaCodeGenMode.OO_SPEC)
			{
				handleOo(files, irSettings, javaSettings, Settings.dialect, printClasses, outputDir, separateTestCode, printWarnings);
			} else if (cgMode == JavaCodeGenMode.SL_SPEC)
			{
				handleSl(files, irSettings, javaSettings, printClasses, outputDir, separateTestCode, printWarnings);
			} else
			{
				MsgPrinter.getPrinter().errorln("Unexpected dialect: "
						+ cgMode);
			}
		}
		clock = System.currentTimeMillis() - clock;
		MsgPrinter.getPrinter().println("\nFinished code generation! Bye... @" + (clock / 1000) + "s");
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
				MsgPrinter.getPrinter().println(String.format("VDM expression '%s' could not be merged. Following merge errors were found:", exp));
				GeneralCodeGenUtils.printMergeErrors(generated.getMergeErrors());
			} else if (!generated.canBeGenerated())
			{
				MsgPrinter.getPrinter().println("Could not generate VDM expression: "
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
				MsgPrinter.getPrinter().println("Code generated expression: "
						+ generated.getContent().trim());
			}
		} catch (AnalysisException e)
		{
			MsgPrinter.getPrinter().println("Could not code generate model: "
					+ e.getMessage());

		}
	}

	public static void handleSl(List<File> files, IRSettings irSettings,
			JavaSettings javaSettings, boolean printCode, File outputDir,
			boolean separateTestCode, boolean printWarnings)
	{
		try
		{
			JavaCodeGen vdmCodGen = new JavaCodeGen();
			vdmCodGen.setSettings(irSettings);
			vdmCodGen.setJavaSettings(javaSettings);

			Settings.dialect = Dialect.VDM_SL;
			TypeCheckResult<List<AModuleModules>> tcResult = TypeCheckerUtil.typeCheckSl(files);

			if (GeneralCodeGenUtils.hasErrors(tcResult))
			{
				MsgPrinter.getPrinter().error("Found errors in VDM model:");
				MsgPrinter.getPrinter().errorln(GeneralCodeGenUtils.errorStr(tcResult));
				return;
			}

			GeneratedData data = vdmCodGen.generate(CodeGenBase.getNodes(tcResult.result));

			processData(printCode, outputDir, vdmCodGen, data, separateTestCode, printWarnings);

		} catch (AnalysisException e)
		{
			MsgPrinter.getPrinter().println("Could not code generate model: "
					+ e.getMessage());
		}
	}

	public static void handleOo(List<File> files, IRSettings irSettings,
			JavaSettings javaSettings, Dialect dialect, boolean printCode,
			File outputDir, boolean separateTestCode, boolean printWarnings)
	{
		try
		{
			JavaCodeGen vdmCodGen = new JavaCodeGen();
			vdmCodGen.setSettings(irSettings);
			vdmCodGen.setJavaSettings(javaSettings);

			TypeCheckResult<List<SClassDefinition>> tcResult = null;

			if (dialect == Dialect.VDM_PP)
			{
				tcResult = TypeCheckerUtil.typeCheckPp(files);
			} else
			{
				tcResult = TypeCheckerUtil.typeCheckRt(files);
			}

			if (GeneralCodeGenUtils.hasErrors(tcResult))
			{
				MsgPrinter.getPrinter().error("Found errors in VDM model:");
				MsgPrinter.getPrinter().errorln(GeneralCodeGenUtils.errorStr(tcResult));
				return;
			}

			GeneratedData data = vdmCodGen.generate(CodeGenBase.getNodes(tcResult.result));

			processData(printCode, outputDir, vdmCodGen, data, separateTestCode,printWarnings);

		} catch (AnalysisException e)
		{
			MsgPrinter.getPrinter().println("Could not code generate model: "
					+ e.getMessage());

		}
	}

	public static void processData(boolean printCode, final File outputDir,
			JavaCodeGen vdmCodGen, GeneratedData data, boolean separateTestCode,
			boolean printWarnings)
	{
		List<GeneratedModule> generatedClasses = data.getClasses();
		List<String> names = new ArrayList<>();
		int errors_ = 0;
		int warnings_ = 0;
		if (!generatedClasses.isEmpty())
		{
			for (GeneratedModule generatedClass : generatedClasses)
			{
				if (generatedClass.hasMergeErrors())
				{
					MsgPrinter.getPrinter().println(String.format("Class %s could not be merged. Following merge errors were found:", generatedClass.getName()));

					GeneralCodeGenUtils.printMergeErrors(generatedClass.getMergeErrors());
				} else if (!generatedClass.canBeGenerated())
				{
					MsgPrinter.getPrinter().println("Could not generate class: "
							+ generatedClass.getName() + "\n");

					if (generatedClass.hasUnsupportedIrNodes())
					{
						MsgPrinter.getPrinter().println("Following VDM constructs are not supported by the code generator:");
						errors_ += generatedClass.getUnsupportedInIr().size();
						GeneralCodeGenUtils.printUnsupportedIrNodes(generatedClass.getUnsupportedInIr());
					}

					if (generatedClass.hasUnsupportedTargLangNodes())
					{
						MsgPrinter.getPrinter().println("Following constructs are not supported by the code generator:");
						errors_ += generatedClass.getUnsupportedInTargLang().size();
						GeneralCodeGenUtils.printUnsupportedNodes(generatedClass.getUnsupportedInTargLang());
					}

				} else
				{

					if (outputDir != null)
					{
						if (separateTestCode)
						{
							if (generatedClass.isTestCase())
							{
								vdmCodGen.genJavaSourceFile(new File(outputDir, GEN_TESTS_FOLDER), generatedClass);
							} else
							{
								vdmCodGen.genJavaSourceFile(new File(outputDir, GEN_MODEL_CODE_FOLDER), generatedClass);
							}
						} else
						{
							vdmCodGen.genJavaSourceFile(outputDir, generatedClass);
						}
					}

					names.add(generatedClass.getName());

					if (printCode)
					{
						MsgPrinter.getPrinter().println("**********");
						MsgPrinter.getPrinter().println(generatedClass.getContent());
						MsgPrinter.getPrinter().println("\n");
					} else
					{
						MsgPrinter.getPrinter().println("Generated class : "
								+ generatedClass.getName());
					}

					Set<IrNodeInfo> warnings = generatedClass.getTransformationWarnings();

					if (!warnings.isEmpty())
					{
						MsgPrinter.getPrinter().println("Following transformation warnings were found:");
						warnings_ += generatedClass.getTransformationWarnings().size();
						if (printWarnings) 
							GeneralCodeGenUtils.printUnsupportedNodes(generatedClass.getTransformationWarnings());
						else
							MsgPrinter.getPrinter().println("\t ...");
					}
				}
			}
		} else
		{
			MsgPrinter.getPrinter().println("No classes were generated!");
		}

		List<GeneratedModule> quotes = data.getQuoteValues();

		if (quotes != null && !quotes.isEmpty())
		{
			MsgPrinter.getPrinter().println("\nGenerated following quotes (" + quotes.size() + "):");
			
			if (outputDir != null)
			{
				for (GeneratedModule q : quotes)
				{
					if (separateTestCode)
					{
						vdmCodGen.genJavaSourceFile(new File(outputDir, GEN_MODEL_CODE_FOLDER), q);
					} else
					{
						vdmCodGen.genJavaSourceFile(outputDir, q);
					}
				}
			}

			for (GeneratedModule q : quotes)
			{
				MsgPrinter.getPrinter().print(q.getName() + " ");
			}

			MsgPrinter.getPrinter().println("");
		}

		InvalidNamesResult invalidName = data.getInvalidNamesResult();

		if (!invalidName.isEmpty())
		{
			MsgPrinter.getPrinter().println(GeneralCodeGenUtils.constructNameViolationsString(invalidName));
		}

		List<Renaming> allRenamings = data.getAllRenamings();

		if (!allRenamings.isEmpty())
		{
			MsgPrinter.getPrinter().println("\nDue to variable shadowing or normalisation of Java identifiers the following renamings of variables have been made (" + allRenamings.size()+"): ");

			MsgPrinter.getPrinter().println(GeneralCodeGenUtils.constructVarRenamingString(allRenamings));
		}

		if (data.getWarnings() != null && !data.getWarnings().isEmpty() && printWarnings)
		{
			MsgPrinter.getPrinter().println("");
			for (String w : data.getWarnings())
			{
				MsgPrinter.getPrinter().println("[WARNING] " + w);
			}
		}
		List<String> missing = new ArrayList<String>();

		for (GeneratedModule c : generatedClasses)
		{
			missing.add(c.getName());
		}

		missing.removeAll(names);

		MsgPrinter.getPrinter().println("\n Generated " + names.size() + " classes out of " + generatedClasses.size() + " requested" +
				"\n\tMissing : " + missing.toString() +
				"\n\tErrors  : " + errors_ +
				"\n\tWarnings: " + warnings_);
	}

	public static List<File> filterFiles(List<File> files)
	{
		List<File> filtered = new LinkedList<File>();

		for (File f : files)
		{
			if (GeneralCodeGenUtils.isVdmSourceFile(f))
			{
				filtered.add(f);
			}
		}

		return filtered;
	}

	public static void usage(String msg)
	{
		MsgPrinter.getPrinter().errorln("VDM-to-Java Code Generator: " + msg
				+ "\n");
		MsgPrinter.getPrinter().errorln("Usage: CodeGen < " + OO_ARG + " | "
				+ SL_ARG + " | " + RT_ARG + " | -exp > [<options>] [<files>]");
		MsgPrinter.getPrinter().errorln(OO_ARG
				+ ": code generate a VDMPP specification consisting of multiple .vdmpp files");
		MsgPrinter.getPrinter().errorln(SL_ARG
				+ ": code generate a VDMSL specification consisting of multiple .vdmsl files");
		MsgPrinter.getPrinter().errorln(RT_ARG
				+ ": code generate a limited part of a VDMRT specification consisting of multiple .vdmrt files");
		MsgPrinter.getPrinter().errorln(CLASSIC
				+ ": code generate using the VDM classic language release");
		MsgPrinter.getPrinter().errorln(VDM10
				+ ": code generate using the VDM-10 language release");
		MsgPrinter.getPrinter().errorln(EXP_ARG
				+ " <expression>: code generate a VDMPP expression");
		MsgPrinter.getPrinter().errorln(GEN_PRE_CONDITIONS
				+ ": generate pre condition functions");
		MsgPrinter.getPrinter().errorln(GEN_POST_CONDITIONS
				+ ": generate post condition functions");
		MsgPrinter.getPrinter().errorln(GEN_INVARIANTS
				+ ": generate invariant functions");
		MsgPrinter.getPrinter().errorln(FOLDER_ARG
				+ " <folder path>: a folder containing input vdm source files");
		MsgPrinter.getPrinter().errorln(PRINT_ARG
				+ ": print the generated code to the console");
		MsgPrinter.getPrinter().errorln(PACKAGE_ARG
				+ " <java package>:  the output java package of the generated code (e.g. my.code)");
		MsgPrinter.getPrinter().errorln(OUTPUT_ARG
				+ " <folder path>: the output folder of the generated code");
		MsgPrinter.getPrinter().errorln(VDM_ENTRY_EXP
				+ " <vdm entry point expression>: generate a Java main method based on the specified entry point");
		MsgPrinter.getPrinter().errorln(NO_CODE_FORMAT
				+ ": to NOT format the generated Java code");
		MsgPrinter.getPrinter().errorln(JUNIT4 + ": to generate VDMUnit "
				+ IRConstants.TEST_CASE + " sub-classes to JUnit4 tests");
		MsgPrinter.getPrinter().errorln(SEP_TEST_CODE
				+ ": to place the code generated model and the test code into separate folders named '"
				+ GEN_MODEL_CODE_FOLDER + "' and '" + GEN_TESTS_FOLDER
				+ "', respectively");
		MsgPrinter.getPrinter().errorln(VDM_LOC
				+ ": Generate VDM location information for code generated constructs");
		MsgPrinter.getPrinter().errorln(NO_CLONING
				+ ": To disable deep cloning of value types");

		MsgPrinter.getPrinter().errorln(CONC
				+ ": To enable code generation of VDM++'s concurrency constructs");
		
		MsgPrinter.getPrinter().errorln(GEN_SYS_CLASS
				+ ": To generate the VDM-RT system class");
		MsgPrinter.getPrinter().errorln(NO_WARNINGS
				+ ": To suppress printing detailed warning messages");

		// Terminate
		System.exit(1);
	}
}
