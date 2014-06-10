package org.overture.pog.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.pub.ProofObligationGenerator;
import org.overture.pog.utility.POException;

/**
 * All examples Test class.
 * 
 * It's responsible for running the examples repository tests of the POG. These
 * tests consists of the example models from the documentation folder.
 * 
 * 
 * @author ldc
 * 
 */

@RunWith(Parameterized.class)
public class AllExamplesTest {
	
	private String modelpath;

	@Before
	public void setup(){
		Settings.release = Release.DEFAULT;
	}
	
	/**
	 * Constructor for the test. Initialized with parameters from
	 * {@link #testData()}.
	 * 
	 * @param testParameter
	 *            filename for the model to test
	 * @param resultParameter
	 *            test result file
	 */

	public AllExamplesTest(String modelpathP) {
		this.modelpath = modelpathP;
	}

	/**
	 * Generate the test data. Actually just fetches it from
	 * {@link BaseInputProvider}.
	 * @return 
	 */
	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData() {
		return InputsProvider.allExamples();
	}

	// Run the POG to ensure it does not crash
	@Test
	public void testNoCrash() throws IOException, AnalysisException {

		List<INode> ast = TestHelper.getAstFromName(modelpath);
		IProofObligationList ipol = ProofObligationGenerator
				.generateProofObligations(ast);
		assertNotNull(ipol);
	}

	// Run the POG and print the output to the console
//	@Test
	public void testPrint() throws IOException, AnalysisException {
		List<INode> ast = TestHelper.getAstFromName(modelpath);

		try {
			IProofObligationList polist = ProofObligationGenerator
					.generateProofObligations(ast);

			if (polist.isEmpty()) {
				System.out.println("No proof obligations.");
			}

			for (IProofObligation po : polist) {
				printPo(po);
			}
		} catch (POException e) {
			fail("POG crashed on " + modelpath);
			throw e;
		}

		System.out.println("========================================");

	}


	private void printPo(IProofObligation po) {
		// TODO Replace toString with PrettyPrinter
		System.out.println("------------------------");

		StringBuilder sb = new StringBuilder();

		sb.append(po.getKind().toString());

		sb.append(": " + po.getValueTree().toString());

		System.out.println(sb.toString());
	}

}
