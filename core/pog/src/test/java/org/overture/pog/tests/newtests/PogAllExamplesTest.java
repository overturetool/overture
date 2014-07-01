package org.overture.pog.tests.newtests;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.core.tests.AllExamplesHelper;
import org.overture.core.tests.AllExamplesHelper.ExampleAstData;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.pub.ProofObligationGenerator;
import org.overture.pog.tests.PoResult;
import org.overture.pog.tests.TestHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Initial attempt at constructing an All Examples test in the new test framework. Doesn't work
 * yet since some examples are crashing.
 * @author ldc
 *
 */
@RunWith(Parameterized.class)
public class PogAllExamplesTest
{

	String resultFile;
	List<INode> model;

	private static String EXAMPLES_RESULTS_ROOT = "src/test/resources/exampleResults/";

	public PogAllExamplesTest(String _, List<INode> ast, String result)
	{
		this.model = ast;
		this.resultFile = result;
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData() throws ParserException,
			LexException
	{
		Collection<ExampleAstData> examples = AllExamplesHelper.getExamplesAsts();
		Collection<Object[]> r = new Vector<Object[]>();

		for (ExampleAstData e : examples)
		{
			r.add(new Object[] { e.getExampleName(), e.getModel(),
					EXAMPLES_RESULTS_ROOT + e.getExampleName() });
		}

		return r;
	}

	@Test
	public void examplesTest() throws AnalysisException, FileNotFoundException,
			IOException
	{
		IProofObligationList actual = ProofObligationGenerator.generateProofObligations(model);

		Gson gson = new Gson();
		String json = IOUtils.toString(new FileReader(resultFile));
		Type resultType = new TypeToken<Collection<PoResult>>()
		{
		}.getType();
		List<PoResult> expected = gson.fromJson(json, resultType);

		TestHelper.checkSameElements(expected, actual);

	}

}
