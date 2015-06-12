package org.overture.isapog;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.core.tests.ParamStandardTest;
import org.overture.core.tests.PathsProvider;

import com.google.gson.reflect.TypeToken;

@RunWith(Parameterized.class)
public class IsaPogIntegrationTest extends ParamStandardTest<IsaPogResult>
{

	private static final String UPDATE_PROPERTY = "tests.update.isapog";
	private static final String ISA_POG_ROOT = "src/test/resources/integration";;

	public IsaPogIntegrationTest(String nameParameter, String inputParameter,
			String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePaths(ISA_POG_ROOT);
	}

	@Override
	public IsaPogResult processModel(List<INode> ast)
	{
		INode model = ast.get(0);
		if (model instanceof AModuleModules)
		{
			try
			{
				AModuleModules module = (AModuleModules) model;
				IsaPog ip = new IsaPog(module);
				return new IsaPogResult(ip.getModelThyString(), ip.getPosThyString(), false);
			} catch (AnalysisException
					| org.overture.codegen.cgast.analysis.AnalysisException e)
			{
				e.printStackTrace();
				return new IsaPogResult("", "", true);
			}
		}

		else
		{
			fail("Source model is not type correct single module VDM-SL");
		}
		return null;
	}

	@Override
	public Type getResultType()
	{
		Type resultType = new TypeToken<IsaPogResult>()
		{
		}.getType();
		return resultType;
	}

	@Override
	protected String getUpdatePropertyString()
	{
		return UPDATE_PROPERTY;
	}

	@Override
	public void compareResults(IsaPogResult actual, IsaPogResult expected)
	{
		assertEquals("Negative test mismatch", expected.isNegative(), actual.isNegative());

		assertEquals("Model translation mismatch", expected.getModelthy(), actual.getModelthy());

		assertEquals("PO translation mismatch", expected.getPosthy(), actual.getPosthy());

	}

}
