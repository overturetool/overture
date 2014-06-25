package org.overture.core.tests.examples;

import java.util.Collection;
import java.util.List;

import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.node.INode;
import org.overture.core.tests.IResult;
import org.overture.core.tests.ParamTestAbstract;
import org.overture.core.tests.PathsProvider;

public class PogBugRegressionTest extends ParamTestAbstract {

	public PogBugRegressionTest(String _, String testParameter,
			String resultParameter) {
		super(_, testParameter, resultParameter);
	}

	private static final String BUG_REGRESSION_ROOT = "src/test/resources/bug-regression";

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData() {
		return PathsProvider.computePaths(BUG_REGRESSION_ROOT);
	}

	@Override
	public <R extends IResult> void testCompare(R actual, R expected) {
		// TODO Auto-generated method stub

	}

	@Override
	public <R extends IResult> R processModel(List<INode> ast) {
		// TODO Auto-generated method stub
		return null;
	}

}
