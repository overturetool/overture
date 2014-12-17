package org.overture.pog.tests;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.core.tests.ParseTcFacade;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.pub.ProofObligationGenerator;
import org.overture.pog.tests.newtests.PogTestResult;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Class for quick testing and work on the pog
 * 
 * @author ldc
 */
public class Playground
{
	
	@Ignore
	@Test
	public void sandboxSl() throws AnalysisException, IOException, URISyntaxException{
		quickPog("src/test/resources/adhoc/sandbox.vdmsl");
	}

	@Ignore
	@Test
	public void sandboxPp() throws AnalysisException, IOException, URISyntaxException{
		quickPog("src/test/resources/adhoc/sandbox.vdmpp");
	}
	
	public void quickPog(String model) throws AnalysisException, IOException,
			URISyntaxException
	{

		// switch this flag to update a test result file
		boolean write_result = false;
		 write_result = true;

		// switch this flag to print the stored results
		boolean show_result = false;
		// show_result = true;

		String result = "src/test/resources/adhoc/sandbox.result";

		List<INode> ast = ParseTcFacade.typedAst(model, "Playground");

		IProofObligationList ipol = ProofObligationGenerator.generateProofObligations(ast);

		System.out.println("ACTUAL POs:");
		for (IProofObligation po : ipol)
		{
			System.out.println(po.getKindString() + " / "
					+ po.getFullPredString());
		}

		if (write_result)
		{
			this.update(ipol, result);
		}

		if (show_result)
		{
			this.showStored(ipol, result);
		}

	}

	public void showStored(IProofObligationList ipol, String resultpath)
			throws FileNotFoundException, IOException
	{

		// read and deserialize results
		Gson gson = new Gson();
		String json = IOUtils.toString(new FileReader(resultpath));
		Type datasetListType = new TypeToken<PogTestResult>()
		{
		}.getType();
		PogTestResult result = gson.fromJson(json, datasetListType);

		System.out.println("STORED POs:");
		for (String po : result)
		{
			System.out.println(po);
		}

	}

	private void update(IProofObligationList ipol, String resultPath)
			throws AnalysisException, IOException, URISyntaxException
	{

		PogTestResult result = PogTestResult.convert(ipol);

		Gson gson = new Gson();
		String json = gson.toJson(result);

		IOUtils.write(json, new FileOutputStream(resultPath));

		System.out.println("\n" + resultPath + " file updated \n");

	}

}
