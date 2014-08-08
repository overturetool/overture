package org.overture.modelcheckers.probsolver;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Assume;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.test.framework.results.Result;

@RunWith(value = Parameterized.class)
public class TokenTests extends AllTest
{
	public TokenTests(Dialect dialect, File source, String operationName,
			String name)
	{
		super(dialect, source, operationName, name);
	}

	@Parameters(name = "{3}")
	public static Collection<Object[]> getData()
	{
		String root = "src/test/resources/modules/token/";

		Collection<Object[]> tests = new LinkedList<Object[]>();

		tests.addAll(getTests(new File(root), "Mktkn-0003", "Mktkn-0004"));

		return tests;
	}

	@Override
	protected String getPropertyId()
	{
		return "token";
	}

	@Override
	protected void compareResults(Result<String> result, String filename)
	{
		Assume.assumeTrue("Currently unable to test this since the inpit parameters are non random", false);
	}
}
