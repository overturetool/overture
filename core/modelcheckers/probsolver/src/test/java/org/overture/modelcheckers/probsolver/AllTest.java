/*
 * #%~
 * Integration of the ProB Solver for VDM
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
package org.overture.modelcheckers.probsolver;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.modelcheckers.probsolver.AbstractProbSolverUtil.SolverException;
import org.overture.modelcheckers.probsolver.visitors.VdmToBConverter;
import org.overture.parser.util.ParserUtil;
import org.overture.parser.util.ParserUtil.ParserResult;
import org.overture.test.framework.ConditionalIgnoreMethodRule.ConditionalIgnore;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

import de.be4.classicalb.core.parser.exceptions.BException;

//@RunWith(value = Parameterized.class)
public abstract class AllTest extends ProbConverterTestBase
{
	// @Parameters(name = "{3}")
	// public static Collection<Object[]> getData()
	// {
	// String root = "src/test/resources/";
	//
	// Collection<Object[]> tests = new LinkedList<Object[]>();
	//
	// tests.addAll(getTests(new File(root)));
	//
	// return tests;
	// }

	final static String[] EMPTY_FILTER = new String[] {};

	protected static Collection<Object[]> getTests(File root)
	{
		return getTests(root, EMPTY_FILTER);
	}

	protected static Collection<Object[]> getTests(File root, String... filter)
	{

		Set<String> filterSet = new HashSet<String>(Arrays.asList(filter));
		Collection<Object[]> tests = new LinkedList<Object[]>();
		if (root.isFile())
		{
			final String fn = root.getName();

			if (fn.indexOf('.') != -1
					&& filterSet.contains(fn.substring(0, fn.indexOf('.'))))
			{
				// skip
			} else
			{

				if (fn.endsWith(".vdmsl"))
				{
					tests.addAll(extractSlTests(root));
				} else if (root.getName().endsWith(".vdmpp"))
				{
					tests.addAll(extractPpTests(root));
				}
			}
		} else
		{
			for (File f : root.listFiles())
			{
				tests.addAll(getTests(f, filter));
			}
		}
		return tests;
	}

	private static Collection<? extends Object[]> extractSlTests(File f)
	{
		Collection<Object[]> tests = new LinkedList<Object[]>();

		try
		{
			Settings.dialect = Dialect.VDM_SL;
			Settings.release = Release.VDM_10;
			ParserResult<List<AModuleModules>> result = ParserUtil.parseSl(f);

			for (AModuleModules m : result.result)
			{
				for (PDefinition def : m.getDefs())
				{
					if (def instanceof AImplicitOperationDefinition
							|| def instanceof AImplicitFunctionDefinition)
					{
						tests.add(new Object[] {
								Dialect.VDM_SL,
								f,
								def.getName().getName(),
								"SL: " + f.getName() + " - "
										+ m.getName().getName() + "."
										+ def.getName().getName() });
					}
				}
			}
		} catch (Exception e)
		{

		}

		return tests;
	}

	private static Collection<? extends Object[]> extractPpTests(File f)
	{
		Collection<Object[]> tests = new LinkedList<Object[]>();

		try
		{
			Settings.dialect = Dialect.VDM_PP;
			ParserResult<List<SClassDefinition>> result = ParserUtil.parseOo(f);

			for (SClassDefinition m : result.result)
			{
				for (PDefinition def : m.getDefinitions())
				{
					if (def instanceof AImplicitOperationDefinition)
					{
						tests.add(new Object[] {
								Dialect.VDM_PP,
								f,
								def.getName().getName(),
								"PP: " + f.getName() + " - "
										+ m.getName().getName() + "."
										+ def.getName().getName() });
					}
				}
			}
		} catch (Exception e)
		{

		}

		return tests;
	}

	Dialect dialect;
	private String operationName;
	private String name;

	public AllTest(Dialect dialect, File source, String operationName,
			String name)
	{
		super(source, new TypeCheckerAssistantFactory());
		this.dialect = dialect;
		this.operationName = operationName;
		this.name = name;
	}

	@Before
	public void setup() throws BException
	{
		Settings.dialect = dialect;
		Settings.release = Release.VDM_10;
		VdmToBConverter.USE_INITIAL_FIXED_STATE = true;
	}

	@Test
	@ConditionalIgnore(condition = ProbNotInstalledCondition.class)
	public void testMethod() throws IOException, AnalysisException,
			SolverException
	{
		System.out.println("==============================================================\n\t"
				+ name
				+ "\n==============================================================");
		testMethod(operationName);
	}
}
