package org.overture.core.tests.example;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.overture.ast.node.INode;
import org.overture.core.tests.ExternalsTest;

@RunWith(Parameterized.class)
public class ExternalsIdTest extends ExternalsTest<IdTestResult>
{

	public ExternalsIdTest(String nameParameter, String testParameter,
			String resultParameter)
	{
		super(nameParameter, testParameter, resultParameter);
	}

	@Override
	public void testCompare(IdTestResult actual, IdTestResult expected)
	{
		System.out.println(testName);
		
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
	public IdTestResult processModel(List<INode> ast)
	{
		IdTestResult actual = IdTestResult.convert(ast);
		return actual;
	}

}
