/*
 * #%~
 * Combinatorial Testing Runtime
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
package org.overture.ct.ctruntime.tests;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.ct.ctruntime.tests.util.TestSourceFinder;
import org.overture.test.framework.Properties;

@RunWith(value = Parameterized.class)
public class CtNoReductionTestCase extends CtTestCaseBase
{
	private static String TEST_NAME = "CT tests";
	private static final String ROOT = "src/test/resources/no_reduction_sl_specs";

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		Properties.recordTestResults = false;
		
		Collection<Object[]> tests = TestSourceFinder.createTestCompleteFile(Dialect.VDM_PP, TEST_NAME, ROOT, "", "");

		return tests;
	}

	public CtNoReductionTestCase(String name, File file, File traceFolder,
			String[] args)
	{
		super(file, traceFolder, args);
	}

}
