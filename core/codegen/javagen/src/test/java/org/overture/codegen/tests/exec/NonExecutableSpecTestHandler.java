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
import org.overture.config.Release;

public class NonExecutableSpecTestHandler extends TestHandler
{
	public NonExecutableSpecTestHandler(Release release, Dialect dialect)
	{
		super(release, dialect);
	}

	@Override
	public void writeGeneratedCode(File parent, File resultFile, String rootPackage)
			throws IOException
	{
		List<StringBuffer> content = TestUtils.readJavaModulesFromResultFile(resultFile, rootPackage);

		if (content.size() == 0)
		{
			System.out.println("Got no clases for: " + resultFile.getName());
			return;
		}

		parent.mkdirs();

		for (StringBuffer classCgStr : content)
		{
			File tempFile = consTempFile(TestUtils.getJavaModuleName(classCgStr), parent, classCgStr);

			writeToFile(classCgStr.toString(), tempFile);
		}
	}
}
