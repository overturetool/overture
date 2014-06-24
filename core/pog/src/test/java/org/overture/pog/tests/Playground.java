package org.overture.pog.tests;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.pub.ProofObligationGenerator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Class for quick testing and work on the pog
 * 
 * @author ldc
 * 
 */
public class Playground {

	

	
	// comment this annotation out when done! no need to run the test
	@Test
	public void quickTest() throws AnalysisException, IOException,
			URISyntaxException {

		// switch this flag to update a test result file
		boolean write_result = false;
//		write_result = true;

		// switch this flag to print the stored results
		boolean show_result = false;
//		show_result = true;
		
		String model = "src/test/resources/adhoc/sandbox.vdmpp";
		String result = "src/test/resources/adhoc/sandbox.RESULT";

		List<INode> ast = TestHelper.getAstFromName(model);

		IProofObligationList ipol = ProofObligationGenerator
				.generateProofObligations(ast);

		System.out.println("ACTUAL POs:");
		for (IProofObligation po : ipol) {
			System.out.println(po.getKindString() + " / " + po.getFullPredString());
		}

		if (write_result) {
			this.update(ipol, result);
		}

		if (show_result) {
			this.compareWithResults(ipol, result);
		}

	}

	public void compareWithResults(IProofObligationList ipol, String resultpath)
			throws FileNotFoundException, IOException {

		// read and deserialize results
		Gson gson = new Gson();
		String json = IOUtils.toString(new FileReader(resultpath));
		Type datasetListType = new TypeToken<Collection<PoResult>>() {
		}.getType();
		List<PoResult> lpr = gson.fromJson(json, datasetListType);

		System.out.println("STORED POs:");
		for (PoResult por : lpr) {
			System.out.println(por.getPoKind() + " / " + por.getPoExp());
		}

	}

	private void update(IProofObligationList ipol, String resultPath)
			throws AnalysisException, IOException, URISyntaxException {

		List<PoResult> prl = new LinkedList<PoResult>();

		for (IProofObligation po : ipol) {
			prl.add(new PoResult(po.getKindString(), po.getFullPredString()));
		}

		Gson gson = new Gson();
		String json = gson.toJson(prl);

		IOUtils.write(json, new FileOutputStream(resultPath));
		
		System.out.println("\n" +resultPath + " file updated \n");

	}

}
