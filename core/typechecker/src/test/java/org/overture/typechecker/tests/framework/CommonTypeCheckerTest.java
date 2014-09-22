/*
 * #%~
 * The VDM Type Checker
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
package org.overture.typechecker.tests.framework;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.test.framework.results.Result;
import org.overture.typechecker.tests.utils.OvertureTestHelper;

public abstract class CommonTypeCheckerTest extends TypeCheckTestCase
{
	private Dialect dialect;

	public CommonTypeCheckerTest(Dialect dialect, File file, String suiteName,
			File testSuiteRoot)
	{
		super(file, suiteName, testSuiteRoot);
		this.dialect = dialect;
	}

	@Before
	public void setUp() throws Exception
	{
		Settings.dialect = dialect;
		Settings.release = Release.VDM_10;
	}

	@Test
	public void test() throws Exception
	{
		configureResultGeneration();
		try
		{
			Result<Boolean> result = null;

			switch (dialect)
			{
				case VDM_PP:
					result = new OvertureTestHelper().typeCheckPp(file);
					break;
				case VDM_RT:
					result = new OvertureTestHelper().typeCheckRt(file);
					break;
				case VDM_SL:
					result = new OvertureTestHelper().typeCheckSl(file);
					break;

			}

			compareResults(result, file.getName() + ".result");
		} finally
		{
			unconfigureResultGeneration();
		}
	}

	protected File getStorageLocation()
	{
		return file.getParentFile();
	}

	protected File getInputLocation()
	{
		return file.getParentFile();
	}

	@Override
	protected File createResultFile(String filename)
	{
		getStorageLocation().mkdirs();
		return getResultFile(filename);
	}

	@Override
	protected File getResultFile(String filename)
	{
		return new File(getStorageLocation(), filename);
	}

}
