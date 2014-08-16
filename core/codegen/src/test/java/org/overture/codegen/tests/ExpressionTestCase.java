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
import java.io.IOException;

import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;

public class ExpressionTestCase extends CodeGenBaseTestCase
{
	
	public ExpressionTestCase()
	{
		super();
	}

	public ExpressionTestCase(File file)
	{
		super(file);
	}

	@Override
	protected String generateActualOutput() throws AnalysisException
	{
		String fileContent;
		try
		{
			fileContent = GeneralUtils.readFromFile(file);
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		String generatedJava = JavaCodeGenUtil.generateJavaFromExp(fileContent, getIrSettings(), getJavaSettings()).getContent().trim();
		String trimmed = generatedJava.replaceAll("\\s+", " ");
		
		return trimmed;
	}		
}
