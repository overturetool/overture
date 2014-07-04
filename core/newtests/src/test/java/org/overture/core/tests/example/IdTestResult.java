package org.overture.core.tests.example;

import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections4.CollectionUtils;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;

public class IdTestResult extends Vector<String> implements Serializable,
		List<String>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static IdTestResult convert(List<INode> ast)
	{
		IdTestResult r = new IdTestResult();
		for (INode n : ast)
		{
			if (n instanceof AModuleModules) // ModuleModules prints the file path so we skip it
			{
				for (PDefinition p : ((AModuleModules) n).getDefs())
				{
					r.add(p.toString());
				}

			} else
			{
				r.add(n.toString());
			}
		}
		return r;
	}

	public static void compare(IdTestResult actual, IdTestResult expected,
			String testName)
	{
		Collection<String> stored_notfound = CollectionUtils.removeAll(expected, actual);
		Collection<String> found_notstored = CollectionUtils.removeAll(actual, expected);

		if (stored_notfound.isEmpty() && found_notstored.isEmpty())
		{
			// Results match, tests pass;do nothing
		} else
		{
			StringBuilder sb = new StringBuilder();
			if (!stored_notfound.isEmpty())
			{
				sb.append("Expected (but not found) Strings: " + "\n");
				for (String pr : stored_notfound)
				{
					sb.append(pr + "\n");
				}
			}
			if (!found_notstored.isEmpty())
			{
				sb.append("Found (but not expected) Strings: " + "\n");
				for (String pr : found_notstored)
				{
					sb.append(pr + "\n");
				}
			}
			fail("Error in test " + testName + " : \n" + sb.toString());
		}
	}
}
