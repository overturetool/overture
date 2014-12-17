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
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.analysis.violations.InvalidNamesResult;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.config.Release;
import org.overture.config.Settings;

public class JavaCodeGenMain
{
	public static void main(String[] args)
	{
		Settings.release = Release.VDM_10;
		Dialect dialect = Dialect.VDM_RT;

		if (args.length <= 1)
		{
			Logger.getLog().println("Wrong input!");
		}

		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(false);
		irSettings.setGeneratePreConds(false);
		irSettings.setGeneratePreCondChecks(false);
		irSettings.setGeneratePostConds(false);
		irSettings.setGeneratePostCondChecks(false);
		
		JavaSettings javaSettings = new JavaSettings();
		javaSettings.setDisableCloning(false);

		String setting = args[0];
		if (setting.toLowerCase().equals("oo"))
		{
			try
			{
				List<File> files = GeneralUtils.getFilesFromPaths(args);

				List<File> libFiles = GeneralUtils.getFiles(new File("src\\test\\resources\\lib"));
				files.addAll(libFiles);

				GeneratedData data = JavaCodeGenUtil.generateJavaFromFiles(files, irSettings, javaSettings, dialect);
				List<GeneratedModule> generatedClasses = data.getClasses();

				for (GeneratedModule generatedClass : generatedClasses)
				{
					Logger.getLog().println("**********");

					if (generatedClass.hasMergeErrors())
					{
						Logger.getLog().println(String.format("Class %s could not be merged. Following merge errors were found:", generatedClass.getName()));

						JavaCodeGenUtil.printMergeErrors(generatedClass.getMergeErrors());
					} else if (!generatedClass.canBeGenerated())
					{
						Logger.getLog().println("Could not generate class: "
								+ generatedClass.getName() + "\n");
						
						if(generatedClass.hasUnsupportedIrNodes())
						{
							JavaCodeGenUtil.printUnsupportedIrNodes(generatedClass.getUnsupportedInIr());
						}
						
						if(generatedClass.hasUnsupportedTargLangNodes())
						{
							JavaCodeGenUtil.printUnsupportedNodes(generatedClass.getUnsupportedInTargLang());
						}
						
					} else
					{
						Logger.getLog().println(generatedClass.getContent());
					}

					Logger.getLog().println("\n");
				}

				List<GeneratedModule> quotes = data.getQuoteValues();

				Logger.getLog().println("Generated following quotes:");
				
				if (quotes != null && !quotes.isEmpty())
				{
					for(GeneratedModule q : quotes)
					{
						Logger.getLog().println(q.getName());
					}
				}

				InvalidNamesResult invalidName = data.getInvalidNamesResult();

				if (!invalidName.isEmpty())
				{
					Logger.getLog().println(JavaCodeGenUtil.constructNameViolationsString(invalidName));
				}

			} catch (AnalysisException e)
			{
				Logger.getLog().println(e.getMessage());

			} catch (UnsupportedModelingException e)
			{
				Logger.getLog().println("Could not generate model: "
						+ e.getMessage());
				Logger.getLog().println(JavaCodeGenUtil.constructUnsupportedModelingString(e));
			}
		} else if (setting.toLowerCase().equals("exp"))
		{
			try
			{
				Generated generated = JavaCodeGenUtil.generateJavaFromExp(args[1], irSettings, javaSettings);

				if (generated.hasMergeErrors())
				{
					Logger.getLog().println(String.format("VDM expression '%s' could not be merged. Following merge errors were found:", args[1]));
					JavaCodeGenUtil.printMergeErrors(generated.getMergeErrors());
				} else if (!generated.canBeGenerated())
				{
					Logger.getLog().println("Could not generate VDM expression: "
							+ args[1]);
					
					if(generated.hasUnsupportedIrNodes())
					{
						JavaCodeGenUtil.printUnsupportedIrNodes(generated.getUnsupportedInIr());
					}
					
					if(generated.hasUnsupportedTargLangNodes())
					{
						JavaCodeGenUtil.printUnsupportedNodes(generated.getUnsupportedInTargLang());
					}
					
				} else
				{
					Logger.getLog().println(generated.getContent().trim());
				}

			} catch (AnalysisException e)
			{
				Logger.getLog().println(e.getMessage());
			}
		}
	}
}
