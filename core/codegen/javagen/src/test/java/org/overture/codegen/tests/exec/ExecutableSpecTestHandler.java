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
package org.overture.codegen.tests.exec;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.overture.ast.lex.Dialect;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;

public class ExecutableSpecTestHandler extends EntryBasedTestHandler
{
	public ExecutableSpecTestHandler(Release release, Dialect dialect)
	{
		super(release, dialect);
	}

	@Override
	public void writeGeneratedCode(File parent, File resultFile, String rootPackage)
			throws IOException
	{
		List<StringBuffer> content = TestUtils.readJavaModulesFromResultFile(resultFile, rootPackage);

		if (content.isEmpty())
		{
			System.out.println("Got no clases for: " + resultFile.getName());
			return;
		}

		writeMainClass(parent, rootPackage);
		
		if (rootPackage != null)
		{
			parent = new File(parent, JavaCodeGenUtil.getFolderFromJavaRootPackage(rootPackage));
		}
		
		parent.mkdirs();
		
		
		for (StringBuffer classCgStr : content)
		{
			String className = TestUtils.getJavaModuleName(classCgStr);

			File out = null;
			if(classCgStr.toString().contains("quotes;"))
			{
				out = new File(parent, "quotes");
			} else
			{
				out = parent;
			}

			File tempFile = consTempFile(className, out, classCgStr);

			injectSerializableInterface(classCgStr, className);

			writeToFile(classCgStr.toString(), tempFile);
		}
	}

	public void writeMainClass(File parent, String rootPackage)
			throws IOException
	{
		injectArgIntoMainClassFile(parent, rootPackage != null ? (rootPackage  + "." + getJavaEntry()) : getJavaEntry());
	}

	private void injectSerializableInterface(StringBuffer classCgStr,
			String className)
	{
		if (!className.equals(IRConstants.QUOTES_INTERFACE_NAME)
				&& !className.startsWith(CodeGenBase.INTERFACE_NAME_PREFIX))
		{
			// TODO: Improve way that the EvaluatePP/Serializable interface is handled
			String classStr = classCgStr.toString();
			if (!className.equals(IRConstants.QUOTES_INTERFACE_NAME) && !classStr.contains(" implements EvaluatePP")
					&& !classStr.contains(" implements java.io.Serializable"))
			{
				int classNameIdx = classCgStr.indexOf(className);

				int prv = classCgStr.indexOf("private");
				int pub = classCgStr.indexOf("public");
				int abstr = classCgStr.indexOf("abstract");
				int suppress = classCgStr.indexOf("@SuppressWarnings(\"all\")");

				int min = prv >= 0 && prv < pub ? prv : pub;
				min = abstr >= 0 && abstr < min ? abstr : min;
				min = suppress >= 0 && suppress < min ? suppress : min;

				if (min < 0)
				{
					min = classNameIdx;
				}

				int firstLeftBraceIdx = classCgStr.indexOf("{", classNameIdx);

				String toReplace = classCgStr.substring(min, firstLeftBraceIdx);

				String replacement = "import java.io.*;\n\n" + toReplace
						+ " implements Serializable";

				classCgStr.replace(min, firstLeftBraceIdx, replacement);
			}
		}
	}

	@Override
	public String getJavaEntry()
	{
		return "Entry.Run()";
	}

	@Override
	public String getVdmEntry()
	{
		return "Entry`Run()";
	}

	@Override
	public ExecutionResult interpretVdm(File intputFile) throws Exception
	{
		Value val = InterpreterUtil.interpret(Settings.dialect, getVdmEntry(), intputFile);
		return new ExecutionResult(val.toString(), val);
	}
}
