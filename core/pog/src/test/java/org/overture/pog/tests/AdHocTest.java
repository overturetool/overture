package org.overture.pog.tests;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.visitors.PogVisitor;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

/**
 * @author battlenc
 */
public class AdHocTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
	}

	private ProofObligationList pogSL(File file) throws AnalysisException {
		System.out.println("Processing " + file);

		TypeCheckResult<List<AModuleModules>> TC = TypeCheckerUtil
				.typeCheckSl(file);
		assertTrue("Specification has syntax errors",
				TC.parserResult.errors.isEmpty());
		assertTrue("Specification has type errors", TC.errors.isEmpty());
		ProofObligationList proofObligations = new ProofObligationList();

		for (INode aModule : TC.result) {
			proofObligations.addAll(aModule.apply(new PogVisitor(),
					new POContextStack()));
		}

		return proofObligations;
	}


	@Test
	public void test() throws AnalysisException {
		ProofObligationList polist = pogSL(new File(
				"src/test/resources/adhoc/test.vdmsl"));

		for (IProofObligation po : polist) {

			String pretty = po.getValueTree().toString();
			System.out.println(pretty);

		}
	}
}
