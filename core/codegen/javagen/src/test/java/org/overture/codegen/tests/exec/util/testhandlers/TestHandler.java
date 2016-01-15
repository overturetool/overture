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
package org.overture.codegen.tests.exec.util.testhandlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.overture.ast.lex.Dialect;
import org.overture.codegen.vdm2java.IJavaConstants;
import org.overture.config.Release;
import org.overture.config.Settings;

public class TestHandler
{
	private Release release;
	private Dialect dialect;
	
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
				+ IJavaConstants.JAVA_FILE_EXTENSION);

		if (!file.exists())
		{
			file.createNewFile();
		}

		return file;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
