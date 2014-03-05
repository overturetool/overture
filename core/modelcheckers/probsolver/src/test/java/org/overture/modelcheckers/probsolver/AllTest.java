package org.overture.modelcheckers.probsolver;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.modelcheckers.probsolver.ProbSolverUtil.SolverException;
import org.overture.modelcheckers.probsolver.visitors.VdmToBConverter;
import org.overture.parser.util.ParserUtil;
import org.overture.parser.util.ParserUtil.ParserResult;
import org.overture.test.framework.ConditionalIgnoreMethodRule.ConditionalIgnore;

import de.be4.classicalb.core.parser.exceptions.BException;

//@RunWith(value = Parameterized.class)
public abstract class AllTest extends ProbConverterTestBase
{
//	@Parameters(name = "{3}")
//	public static Collection<Object[]> getData()
//	{
//		String root = "src/test/resources/";
//
//		Collection<Object[]> tests = new LinkedList<Object[]>();
//
//		tests.addAll(getTests(new File(root)));
//
//		return tests;
//	}

	protected static Collection<Object[]> getTests(File root)
	{
		Collection<Object[]> tests = new LinkedList<Object[]>();
		if (root.isFile())
		{
			if (root.getName().endsWith(".vdmsl"))
			{
				tests.addAll(extractSlTests(root));
			} else if (root.getName().endsWith(".vdmpp"))
			{
				tests.addAll(extractPpTests(root));
			}
		} else
		{
			for (File f : root.listFiles())
			{
				tests.addAll(getTests(f));
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
					if (def instanceof AImplicitOperationDefinition || def instanceof AImplicitFunctionDefinition)
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
		super(source);
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
		try
		{
			testMethod(operationName);
		} catch (SolverException e)
		{
			//We just test the translation so some of the invocations may not be valid
			if(!(e.getMessage().startsWith("no solution found")||e.getMessage().startsWith("cannot be solved")))
			{
				throw e;
			}
		}
	}
}
