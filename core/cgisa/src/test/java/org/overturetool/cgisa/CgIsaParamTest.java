package org.overturetool.cgisa;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.core.tests.ParamStandardTest;
import org.overture.core.tests.PathsProvider;

import com.google.gson.reflect.TypeToken;

@RunWith(Parameterized.class)
public class CgIsaParamTest extends ParamStandardTest<CgIsaTestResult>
{

	public CgIsaParamTest(String nameParameter, String inputParameter,
			String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}

	private static final String UPDATE = "tests.update.cgisa";
	private static final String CGISA_ROOT = "src/test/resources/micro";

	@Override
	public CgIsaTestResult processModel(List<INode> ast)
	{
		IsaCodeGen gen = new IsaCodeGen();

		List<SClassDefinition> classes = new LinkedList<>();

		for (INode n : ast)
		{
			classes.add((SClassDefinition) n);
		}

		List<GeneratedModule> result;
		try
		{
			result = gen.generateIsabelleSyntax(classes);
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
		assertTrue("Expected: \n"+expected.translation + "\nGot: \n"+actual.translation, expected.compare(actual));
	}

}
