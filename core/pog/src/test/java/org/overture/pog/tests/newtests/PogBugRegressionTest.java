package org.overture.pog.tests.newtests;

import static org.junit.Assert.fail;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.core.tests.AParamBasicTest;
import org.overture.core.tests.PathsProvider;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.pub.ProofObligationGenerator;

import com.google.gson.reflect.TypeToken;

/**
 * Quick usage example of new overture tests. Takes an AST and
 * dumps the entire content into a string.
 * 
 * @author ldc
 * 
 */
@RunWith(Parameterized.class)
public class PogBugRegressionTest extends AParamBasicTest<PogTestResult> {

	public PogBugRegressionTest(String _, String testParameter,
			String resultParameter) {
		super(_, testParameter, resultParameter);
	//	updateResult=true;
	}

	private static final String BUG_REGRESSION_ROOT = "src/test/resources/bug-regression";

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData() {
		return PathsProvider.computePaths(BUG_REGRESSION_ROOT);
	}

	@Override
	public void testCompare(PogTestResult actual, PogTestResult expected) {
		Collection<String> stored_notfound = CollectionUtils.removeAll(
				expected, actual);
		Collection<String> found_notstored = CollectionUtils.removeAll(
				actual, expected);

		if (stored_notfound.isEmpty() && found_notstored.isEmpty()) {
			// Results match, tests pass;do nothing
		} else {
			StringBuilder sb = new StringBuilder();
			if (!stored_notfound.isEmpty()) {
				sb.append("Expected (but not found) POS: " + "\n");
				for (String pr : stored_notfound) {
					sb.append(pr + "\n");
				}
			}
			if (!found_notstored.isEmpty()) {
				sb.append("Found (but not expected) POS: " + "\n");
				for (String pr : found_notstored) {
					sb.append(pr+ "\n");
				}
			}
			fail(sb.toString());
		}
	}

	@Override
	public PogTestResult processModel(List<INode> ast) {
		try {
			IProofObligationList ipol = ProofObligationGenerator
					.generateProofObligations(ast);
			PogTestResult actual = PogTestResult.convert(ipol);
			return actual;

		} catch (AnalysisException e) {
			fail("Could not process test file " + testName);
		}
		// will never hit due to fail()
		return null;
	}

	@Override
	public Type getResultType() {
		Type resultType = new TypeToken<PogTestResult>() {
		}.getType();
		return resultType;
	}

}
