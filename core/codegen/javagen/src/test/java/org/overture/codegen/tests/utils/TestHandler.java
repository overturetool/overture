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
package org.overture.codegen.tests.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.overture.ast.lex.Dialect;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2java.IJavaCodeGenConstants;
import org.overture.config.Release;
import org.overture.config.Settings;

public abstract class TestHandler
{
	public static final String QUOTES_PACKAGE_NAME = "quotes";

	protected File currentInputFile = null;
	protected File currentResultFile = null;

	private Release release;

	private Dialect dialect;

	public void setCurrentInputFile(File currentInputFile)
	{
		this.currentInputFile = currentInputFile;
	}

	public void setCurrentResultFile(File currentResultFile)
	{
		this.currentResultFile = currentResultFile;
	}

	public TestHandler(Release release, Dialect dialect)
	{
		this.release = release;
		this.dialect = dialect;
		initVdmEnv();
	}

	public void initVdmEnv()
	{
		Settings.release = release;
		Settings.dialect = dialect;
	}

	public abstract void writeGeneratedCode(File parent, File resultFile,
			String rootPackage) throws IOException;

	public void writeToFile(String toWrite, File file) throws IOException
	{
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
		BufferedWriter out = new BufferedWriter(writer);
		out.write(toWrite);
		out.close();
	}

	public File getFile(File parent, String className) throws IOException
	{
		File file = new File(parent, className
				+ IJavaCodeGenConstants.JAVA_FILE_EXTENSION);

		if (!file.exists())
		{
			file.createNewFile();
		}

		return file;
	}

	public String readFromFile(File resultFile) throws IOException
	{
		return GeneralUtils.readFromFile(resultFile).replace('#', ' ');
	}

	protected File consTempFile(String className, File parent,
			StringBuffer classCgStr) throws IOException
	{
		File outputDir = parent;

		if (className.equals(IRConstants.QUOTES_INTERFACE_NAME))
		{
			outputDir = new File(parent, QUOTES_PACKAGE_NAME);
			outputDir.mkdirs();
		}

		File tempFile = new File(outputDir, className
				+ IJavaCodeGenConstants.JAVA_FILE_EXTENSION);

		if (!tempFile.exists())
		{
			tempFile.getParentFile().mkdirs();
			tempFile.createNewFile();
		}
		return tempFile;
	}
}
