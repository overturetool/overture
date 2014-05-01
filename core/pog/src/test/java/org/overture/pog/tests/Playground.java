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

public class Playground {

	static boolean UPDATE = true;
	
	@Test
	public void quickTest() throws AnalysisException, IOException, URISyntaxException{
		List<INode> ast = TestHelper
				.getAstFromName("src/test/resources/adhoc/sandbox.vdmsl");
		IProofObligationList ipol = ProofObligationGenerator
				.generateProofObligations(ast);
		
		for (IProofObligation po : ipol) {
			System.out.println(po.getValue());
		}
		
		if (UPDATE){
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
