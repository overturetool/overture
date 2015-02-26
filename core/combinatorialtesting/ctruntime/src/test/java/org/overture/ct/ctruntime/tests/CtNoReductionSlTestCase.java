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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.ct.ctruntime.tests.util.TestSourceFinder;
import org.overture.ct.ctruntime.utils.CtHelper.CtTestData;

@RunWith(value = Parameterized.class)
public class CtNoReductionSlTestCase extends CtTestCaseBase
{
	@BeforeClass
	public static void s()
	{
		System.out.println();
	}
	
	@AfterClass
	public static void e()
	{
		System.out.println();
	}
//	@Rule
//	public TestRule benchmarkRun = new BenchmarkRule();
	
	private static String TEST_NAME = "CT no reduction SL tests";
	private static final String ROOT = "src/test/resources/no_reduction_sl_specs";

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		Collection<Object[]> tests = TestSourceFinder.createTestCompleteFile(Dialect.VDM_SL, TEST_NAME, ROOT, "", "");

		return tests;
	}

	public CtNoReductionSlTestCase(String name, File file, File traceFolder,
			CtTestData args)
	{
		super(file, traceFolder, args);
	}

	@Override
	public void setUp() throws Exception
	{
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
	}
	
	@Override
	protected String getPropertyId()
	{
		return "sl.no";
	}
}
