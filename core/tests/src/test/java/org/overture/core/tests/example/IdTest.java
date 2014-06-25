package org.overture.core.tests.example;

import static org.junit.Assert.fail;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.node.INode;
import org.overture.core.tests.ParamTestAbstract;
import org.overture.core.tests.PathsProvider;

import com.google.gson.reflect.TypeToken;

/**
 * Quick usage example of new overture tests. Takes an AST and dumps the entire
 * content into a string.
 * 
 * @author ldc
 * 
 */
@RunWith(Parameterized.class)
public class IdTest extends ParamTestAbstract<IdTestResult> {

	public IdTest(String _, String testParameter, String resultParameter) {
		super(_, testParameter, resultParameter);
		// The line below enables results updating. Working to improve this...
		//updateResult=true; 
	}

	private static final String EXAMPLE_RESOURCES_ROOT = "src/test/resources/example";

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData() {
		return PathsProvider.computePaths(EXAMPLE_RESOURCES_ROOT);
	}

	@Override
	public void testCompare(IdTestResult actual, IdTestResult expected) {
		Collection<String> stored_notfound = CollectionUtils.removeAll(
				expected, actual);
		Collection<String> found_notstored = CollectionUtils.removeAll(actual,
				expected);

		if (stored_notfound.isEmpty() && found_notstored.isEmpty()) {
			// Results match, tests pass;do nothing
		} else {
			StringBuilder sb = new StringBuilder();
			if (!stored_notfound.isEmpty()) {
				sb.append("Expected (but not found) Strings: " + "\n");
				for (String pr : stored_notfound) {
					sb.append(pr + "\n");
				}
			}
			if (!found_notstored.isEmpty()) {
				sb.append("Found (but not expected) Strings: " + "\n");
				for (String pr : found_notstored) {
					sb.append(pr + "\n");
				}
			}
			fail(sb.toString());
		}
	}

	@Override
	public IdTestResult processModel(List<INode> ast) {
		IdTestResult actual = IdTestResult.convert(ast);
		return actual;

	}

	@Override
	public Type getResultType() {
		Type resultType = new TypeToken<IdTestResult>() {
		}.getType();
		return resultType;
	}

}
