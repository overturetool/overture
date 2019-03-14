/*
 * #%~
 * VDM to Isabelle Translation
 * %%
 * Copyright (C) 2008 - 2015 Overture
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
package org.overturetool.cgisa;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assume;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.core.testing.PathsProvider;

/**
 * Main integration test class. Runs tests on complete models.
 * 
 * @author ldc
 */
@RunWith(Parameterized.class)

public class IsaGenModelTest extends IsaGenParamTest
{

	public IsaGenModelTest(String nameParameter, String inputParameter,
			String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}

	private static final String UPDATE = "tests.update.isagen.model";
	private static final String MODELS_ROOT = "src/test/resources/models";
	private static final List<String> skippedTests = Arrays.asList("CustomAlarm.vdmsl","dummy.vdmsl","Alarm1.vdmsl");


	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePaths(MODELS_ROOT);
	}

	@Override
	protected String getUpdatePropertyString()
	{
		return UPDATE;
	}

	protected void checkAssumptions() {
		Assume.assumeTrue("Test in skip list.",notSkipped());
	}

	private boolean notSkipped() {
		return !skippedTests.contains(testName);
	}


}
