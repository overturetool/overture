package org.overture.pog.tests;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
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

/**
 * Class for quick testing and work on the pog
 * 
 * @author ldc
 * 
 */
public class Playground {

	// switch this flag to update a test result file
	static boolean WRITE_RESULT = false;

	// comment this annotation out when done!
	//@Test
	public void quickTest() throws AnalysisException, IOException, URISyntaxException{
		String model = "src/test/resources/adhoc/sandbox.vdmsl";
		//String model = "src/test/resources/adhoc/sandbox.vdmpp";
		
		
		List<INode> ast = TestHelper
				.getAstFromName(model);
		
		IProofObligationList ipol = ProofObligationGenerator
				.generateProofObligations(ast);
		
		for (IProofObligation po : ipol) {
			System.out.println(po.getKindString() + " / " +po.getValue());
		}
		
		if (WRITE_RESULT){
			this.update(ipol);
		}
	}

	private void update(IProofObligationList ipol) throws AnalysisException,
			IOException, URISyntaxException {

		List<PoResult> prl = new LinkedList<PoResult>();

		for (IProofObligation po : ipol) {
			prl.add(new PoResult(po.getKindString(), po.getValue()));
		}

		Gson gson = new Gson();
		String json = gson.toJson(prl);

		IOUtils.write(json, new FileOutputStream(
				"src/test/resources/adhoc/sandbox.result"));

		System.out.println("sandbox.result file updated");

	}

}
