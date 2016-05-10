/*
 * #%~
 * Overture Testing Framework
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
package org.overture.core.tests;

import java.util.Collection;
import java.util.Vector;
import java.util.ArrayList;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test with external inputs. This test behaves exactly like {@link ParamFineGrainTest}. The only difference is that the
 * test data provider is predefined and works with external inputs specified by passing
 * <code>-DexternalTestsPath=/path/to/files</code> to the VM.<br>
 * <br>
 * Test result files are stored locally for each module under <code>src/test/resources/external</code> *
 * 
 * @author ldc
 * @param <R>
 *            the type of result this test operates on.
 */
@RunWith(Parameterized.class)
public abstract class ParamExternalsTest<R> extends ParamFineGrainTest<R>
{

	private static String EXTERNAL_TESTS_PROPERTY = "externalTestsPath";

	/**
	 * Constructor simply passes the parameters along to the superclass. Since the parameters are provided by
	 * {@link #testData()}, any subclass of this test must have same constructor parameters or will it will have to
	 * declare a new provider.
	 * 
	 * @param nameParameter
	 * @param testParameter
	 * @param resultParameter
	 */
	public ParamExternalsTest(String nameParameter, String testParameter,
			String resultParameter)
	{
		super(nameParameter, testParameter, resultParameter);
	}

	/**
	 * The data provider for this test. The input paths are calculated based off the external path provided. The results
	 * are constructed using the entire path of the input so that the result folder structure mirrors that of the
	 * external inputs. <br>
	 * <br>
	 * This method collects <b>all</b> sources in the provided path. If you with to do some kind of preliminary
	 * filtering, you must call it from within your own provider.
	 * 
	 * @return a collection of test input and result paths in the form of {filename ,filepath, resultpath} arrays
	 */
	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		String external = System.getProperty(EXTERNAL_TESTS_PROPERTY);

		if (external == null)
		{
			return new ArrayList<Object[]>();
		} else
		{
			return PathsProvider.computeExternalPaths(external);
		}
	}

}
