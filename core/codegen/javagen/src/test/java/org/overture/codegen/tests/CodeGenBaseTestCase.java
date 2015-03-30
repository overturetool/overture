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
package org.overture.codegen.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Assert;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.test.framework.BaseTestCase;
import org.overture.test.framework.Properties;

public abstract class CodeGenBaseTestCase extends BaseTestCase
{
	protected static JavaCodeGen vdmCodGen = new JavaCodeGen();
	
	public CodeGenBaseTestCase()
	{
		super();
	}

	public CodeGenBaseTestCase(File file)
	{
		super(file);
	}

	public IRSettings getIrSettings()
	{
		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(false);

		return irSettings;
	}

	public JavaSettings getJavaSettings()
	{
		JavaSettings javaSettings = new JavaSettings();
		javaSettings.setDisableCloning(false);

		return javaSettings;
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
		Logger.getLog().setSilent(true);
		
		vdmCodGen.clear();
		vdmCodGen.setSettings(getIrSettings());
		vdmCodGen.setJavaSettings(getJavaSettings());
	}

	public void test() throws ParserException, LexException, IOException
	{
		if (content == null)
		{
			return;
		}

		String filename = file.getAbsolutePath();

		if (Properties.recordTestResults)
		{
			File resultFile = createResultFile(filename);
			resultFile.getParentFile().mkdirs();

			try
			{
				String newResult = generateActualOutput();
				storeResult(resultFile, newResult);

			} catch (Exception e)// FileNotFoundException | AnalysisException
			{
				Assert.fail("The produced results could not be stored: "
						+ e.getMessage());
			}

			return;
		}

		File resultFile = getResultFile(filename);

		assertNotNull("Result file " + resultFile.getName() + " was not found", resultFile);
		assertTrue("Result file " + resultFile.getAbsolutePath()
				+ " does not exist", resultFile.exists());

		String parsedResult = GeneralUtils.readFromFile(resultFile);
		boolean parsed = parsedResult != null;

		Assert.assertTrue("Could not read result file: " + resultFile.getName(), parsed);

		String actual = null;
		try
		{
			actual = generateActualOutput();
		} catch (AnalysisException e)
		{
			Assert.fail("Could not generate actual output from file: "
					+ getName());
		}

		boolean resultOk = actual.trim().equals(parsedResult);
		Assert.assertTrue(resultOk);
	}

	@Override
	public String getName()
	{
		return this.content;
	}

	protected void storeResult(File file, String result)
			throws FileNotFoundException
	{
		PrintStream out = new PrintStream(new FileOutputStream(file));
		out.print(result);
		out.close();
	}

	protected File createResultFile(String filename)
	{
		return new File(filename + ".result");
	}

	protected File getResultFile(String filename)
	{
		return new File(filename + ".result");
	}

	abstract protected String generateActualOutput() throws AnalysisException;
}
