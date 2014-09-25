package org.overture.pog.tests;

import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.core.npp.NewPrettyPrinter;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.pub.ProofObligationGenerator;
import org.overture.pog.tests.newtests.PogLegacyTest;
import org.overture.pog.tests.newtests.PogTestResult;

import com.google.gson.Gson;

@RunWith(Parameterized.class)
public class PrettyPrintTest extends PogLegacyTest {

	public PrettyPrintTest(String nameParameter, String testParameter,
			String resultParameter) {
		super(nameParameter, testParameter, resultParameter);
	}

	@Override
	public void compareResults(PogTestResult actual, PogTestResult expected) {
		// dont care about results for now

	}

	@Override
	public PogTestResult processModel(List<INode> model) {
		IProofObligationList ipol;
		try {
			List<String> r = new LinkedList<String>();
			ipol = ProofObligationGenerator.generateProofObligations(model);
			for (IProofObligation po : ipol) {
				String npp = "prtprntr version: "
						+ NewPrettyPrinter.prettyPrint(po.getValueTree()
								.getPredicate());
				if (npp.contains("ERROR")) {
					System.out.println(npp);
				}
			}

		} catch (AnalysisException e) {
			fail("Could not process model in test " + testName);
			e.printStackTrace();
		}
		return null;

	}

	// // comment this annotation out when done! no need to run the test
	// @Test
	// public void quickTest() throws AnalysisException, IOException,
	// URISyntaxException
	// {
	//
	// // switch this flag to update a test result file
	// boolean write_result = false;
	// // write_result = true;
	//
	// // switch this flag to print the stored results
	// boolean show_result = false;
	// // show_result = true;
	//
	// String model = "src/test/resources/adhoc/sandbox.vdmpp";
	//
	// List<INode> ast = ParseTcFacade.typedAst(model, "Playground");
	//
	// IProofObligationList ipol =
	// ProofObligationGenerator.generateProofObligations(ast);
	//
	// System.out.println("ACTUAL POs:");
	// for (IProofObligation po : ipol)
	// {
	// System.out.println("toString version: " + po.getFullPredString());
	// System.out.println("prettyPrinter version: " +
	// NewPrettyPrinter.prettyPrint(po.getValueTree().getPredicate()));
	//
	// }
	//
	//
	//
	// }
	//
	// public void showStored(IProofObligationList ipol, String resultpath)
	// throws FileNotFoundException, IOException
	// {
	//
	// // read and deserialize results
	// Gson gson = new Gson();
	// String json = IOUtils.toString(new FileReader(resultpath));
	// Type datasetListType = new TypeToken<PogTestResult>()
	// {
	// }.getType();
	// PogTestResult result = gson.fromJson(json, datasetListType);
	//
	// System.out.println("STORED POs:");
	// for (String po : result)
	// {
	// System.out.println(po);
	// }
	//
	// }
	//
	// private void update(IProofObligationList ipol, String resultPath)
	// throws AnalysisException, IOException, URISyntaxException
	// {
	//
	// PogTestResult result = PogTestResult.convert(ipol);
	//
	// Gson gson = new Gson();
	// String json = gson.toJson(result);
	//
	// IOUtils.write(json, new FileOutputStream(resultPath));
	//
	// System.out.println("\n" + resultPath + " file updated \n");
	//
	// }

}
