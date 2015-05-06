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
import org.overture.codegen.analysis.vdm.Renaming;
import org.overture.codegen.analysis.violations.InvalidNamesResult;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
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

public class JavaCodeGenMain
{
	public static final String OO_ARG = "-oo";
	public static final String EXP_ARG = "-exp";
	public static final String FOLDER_ARG = "-folder";
	public static final String PRINT_ARG = "-print";
	public static final String PACKAGE_ARG = "-package";
	public static final String OUTPUT_ARG = "-output";
	public static final String VDM_ENTRY_EXP = "-entry";
	
	public static void main(String[] args)
	{
		Settings.release = Release.VDM_10;
		Dialect dialect = Dialect.VDM_PP;

		boolean ooChosen = false;
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

		for (Iterator<String> i = listArgs.iterator(); i.hasNext();)
		{
			String arg = i.next();

			if (arg.equals(OO_ARG))
			{
				ooChosen = true;
			} else if (arg.equals(EXP_ARG))
			{
				ooChosen = false;

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
						files.addAll(filterPp(GeneralUtils.getFiles(path)));
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
					
					if(GeneralCodeGenUtils.isValidJavaPackage(javaPackage))
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
			else
			{
				// It's a file or a directory
				File file = new File(arg);

				if (file.isFile())
				{
					if (file.getName().endsWith(".vdmpp"))
					{
						files.add(file);
					}
				} else
				{
					usage("Not a file: " + file);
				}
			}
		}

		Logger.getLog().println("Starting code generation...\n");

		
		if (ooChosen)
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
			
			handleOo(files, irSettings, javaSettings, dialect, printClasses, outputDir);
		} else
		{
			handleExp(exp, irSettings, javaSettings, dialect);
		}

		Logger.getLog().println("\nFinished code generation! Bye...");
	}

	private static void handleExp(String exp, IRSettings irSettings,
			JavaSettings javaSettings, Dialect dialect)
	{
		try
		{
			Generated generated = JavaCodeGenUtil.generateJavaFromExp(exp, irSettings, javaSettings, dialect);

			if (generated.hasMergeErrors())
			{
				Logger.getLog().println(String.format("VDM expression '%s' could not be merged. Following merge errors were found:", exp));
				JavaCodeGenUtil.printMergeErrors(generated.getMergeErrors());
			} else if (!generated.canBeGenerated())
			{
				Logger.getLog().println("Could not generate VDM expression: "
						+ exp);

				if (generated.hasUnsupportedIrNodes())
				{
					JavaCodeGenUtil.printUnsupportedIrNodes(generated.getUnsupportedInIr());
				}

				if (generated.hasUnsupportedTargLangNodes())
				{
					JavaCodeGenUtil.printUnsupportedNodes(generated.getUnsupportedInTargLang());
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

	public static void handleOo(List<File> files, IRSettings irSettings,
			JavaSettings javaSettings, Dialect dialect, boolean printCode, File outputDir)
	{
		try
		{
			JavaCodeGen vdmCodGen = new JavaCodeGen();
			vdmCodGen.setSettings(irSettings);
			vdmCodGen.setJavaSettings(javaSettings);
			
			List<SClassDefinition> ast = JavaCodeGenUtil.consMergedParseList(files, dialect);
			
			GeneratedData data = vdmCodGen.generateJavaFromVdm(ast);
			
			List<GeneratedModule> generatedClasses = data.getClasses();

			Logger.getLog().println("");
			
			if (outputDir != null)
			{
				String javaPackage = javaSettings.getJavaRootPackage();
				if(GeneralCodeGenUtils.isValidJavaPackage(javaPackage))
				{
					String packageFolderPath = GeneralCodeGenUtils.getFolderFromJavaRootPackage(javaPackage);
					outputDir = new File(outputDir, packageFolderPath);
				}
			}
			
			for (GeneratedModule generatedClass : generatedClasses)
			{
				if (generatedClass.hasMergeErrors())
				{
					Logger.getLog().println(String.format("Class %s could not be merged. Following merge errors were found:", generatedClass.getName()));

					JavaCodeGenUtil.printMergeErrors(generatedClass.getMergeErrors());
				} else if (!generatedClass.canBeGenerated())
				{
					Logger.getLog().println("Could not generate class: "
							+ generatedClass.getName() + "\n");

					if (generatedClass.hasUnsupportedIrNodes())
					{
						Logger.getLog().println("Following VDM constructs are not supported by the code generator:");
						JavaCodeGenUtil.printUnsupportedIrNodes(generatedClass.getUnsupportedInIr());
					}

					if (generatedClass.hasUnsupportedTargLangNodes())
					{
						Logger.getLog().println("Following constructs are not supported by the code generator:");
						JavaCodeGenUtil.printUnsupportedNodes(generatedClass.getUnsupportedInTargLang());
					}

				} else
				{
					if (outputDir != null)
					{
						vdmCodGen.generateJavaSourceFile(outputDir, generatedClass);
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
						JavaCodeGenUtil.printUnsupportedNodes(generatedClass.getTransformationWarnings());
					}
				}
			}

			List<GeneratedModule> quotes = data.getQuoteValues();

			Logger.getLog().println("\nGenerated following quotes:");

			if (quotes != null && !quotes.isEmpty())
			{
				if(outputDir != null)
				{
					outputDir = new File(outputDir, JavaCodeGen.QUOTES);
					
					for (GeneratedModule q : quotes)
					{
						vdmCodGen.generateJavaSourceFile(outputDir, q);
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
				Logger.getLog().println(JavaCodeGenUtil.constructNameViolationsString(invalidName));
			}

			List<Renaming> allRenamings = data.getAllRenamings();

			if (!allRenamings.isEmpty())
			{
				Logger.getLog().println("\nDue to variable shadowing or normalisation of Java identifiers the following renamings of variables have been made: ");

				Logger.getLog().println(JavaCodeGenUtil.constructVarRenamingString(allRenamings));
			}
			
			if(data.getWarnings() != null && !data.getWarnings().isEmpty())
			{
				Logger.getLog().println("");
				for(String w : data.getWarnings())
				{
					Logger.getLog().println("[WARNING] " + w);
				}
			}

		} catch (AnalysisException e)
		{
			Logger.getLog().println("Could not code generate model: "
					+ e.getMessage());

		} catch (UnsupportedModelingException e)
		{
			Logger.getLog().println("Could not generate model: "
					+ e.getMessage());
			Logger.getLog().println(JavaCodeGenUtil.constructUnsupportedModelingString(e));
		}
	}
	
	public static List<File> filterPp(List<File> files)
	{
		List<File> filtered = new LinkedList<File>();
		
		for(File f : files)
		{
			if(f.getName().endsWith(".vdmpp"))
			{
				filtered.add(f);
			}
		}
		
		return filtered;
	}

	public static void usage(String msg)
	{
		Logger.getLog().printErrorln("VDM++ to Java Code Generator: " + msg
				+ "\n");
		Logger.getLog().printErrorln("Usage: CodeGen <-oo | -exp> [<options>] [<files>]");
		Logger.getLog().printErrorln(OO_ARG + ": code generate a VDMPP specification consisting of multiple .vdmpp files");
		Logger.getLog().printErrorln(EXP_ARG + " <expression>: code generate a VDMPP expression");
		Logger.getLog().printErrorln(FOLDER_ARG + " <folder path>: a folder containing input .vdmpp files");
		Logger.getLog().printErrorln(PRINT_ARG  + ": print the generated code to the console");
		Logger.getLog().printErrorln(PACKAGE_ARG + " <java package>:  the output java package of the generated code (e.g. my.code)");
		Logger.getLog().printErrorln(OUTPUT_ARG + " <folder path>: the output folder of the generated code");
		Logger.getLog().printErrorln(VDM_ENTRY_EXP + " <vdm entry point expression>: generate a Java main method based on the specified entry point");
		
		// Terminate
		System.exit(1);
	}
}
