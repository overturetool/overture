package org.overture.pog.tests;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.pub.ProofObligationGenerator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@RunWith(Parameterized.class)
public class BugRegressionTest
{

	private String modelPath;
	private String resultPath;

	@Before
	public void setup()
	{
		Settings.release = Release.DEFAULT;
	}

	public BugRegressionTest(String model, String result)
	{
		this.modelPath = model;
		this.resultPath =result;
	}

	@Parameters(name = "{index} : {1}")
	public static Collection<Object[]> testData()
	{
		return InputsProvider.bugRegs();
	}

	@Test
	public void testWithCompare() throws AnalysisException, IOException,
			URISyntaxException
	{

		List<INode> ast = TestHelper.getAstFromName(modelPath);
		IProofObligationList ipol = ProofObligationGenerator.generateProofObligations(ast);

		Gson gson = new Gson();
		String json = IOUtils.toString(new FileReader(resultPath));
		Type datasetListType = new TypeToken<Collection<PoResult>>()
		{
		}.getType();
		List<PoResult> results = gson.fromJson(json, datasetListType);

		TestHelper.checkSameElements(results, ipol);

	}

}
