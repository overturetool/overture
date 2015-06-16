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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.core.tests.ParamStandardTest;
import org.overture.core.tests.PathsProvider;

import com.google.gson.reflect.TypeToken;

/**
 * Main parameterized test class. Runs tests on modules with minimal
 * definitions to exercise the translation with a single construct 
 * at a time.
 * @author ldc
 *
 */
@RunWith(Parameterized.class)
public class IsaGenParamTest extends ParamStandardTest<CgIsaTestResult>
{

	public IsaGenParamTest(String nameParameter, String inputParameter,
			String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}

	private static final String UPDATE = "tests.update.isagen";
	private static final String CGISA_ROOT = "src/test/resources/modules";

	@Override
	public CgIsaTestResult processModel(List<INode> ast)
	{
		IsaGen gen = new IsaGen();

		List<AModuleModules> classes = new LinkedList<>();

		for (INode n : ast)
		{
			classes.add((AModuleModules) n);
		}

		List<GeneratedModule> result = null;
		try
		{
			result = gen.generateIsabelleSyntax(classes);
			if (!result.get(0).canBeGenerated())
			{
				StringBuilder sb = new StringBuilder();
				sb.append(result.get(0).getMergeErrors());
				sb.append(result.get(0).getUnsupportedInIr());
				sb.append(result.get(0).getUnsupportedInTargLang());
				fail(sb.toString());
			}
		} catch (AnalysisException
				| org.overture.codegen.cgast.analysis.AnalysisException e)
		{
			fail("Could not process test file " + testName);
			return null;
		}

		return CgIsaTestResult.convert(result);
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePaths(CGISA_ROOT);
	}

	@Override
	public Type getResultType()
	{
		Type resultType = new TypeToken<CgIsaTestResult>()
		{
		}.getType();
		return resultType;
	}

	@Override
	protected String getUpdatePropertyString()
	{
		return UPDATE;
	}

	@Override
	public void compareResults(CgIsaTestResult actual, CgIsaTestResult expected)
	{
		assertTrue("\n --- Expected: ---\n" + expected.translation
				+ "\n --- Got: ---\n" + actual.translation, expected.compare(actual));
	}

}
