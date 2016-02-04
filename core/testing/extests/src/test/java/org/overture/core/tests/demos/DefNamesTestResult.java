package org.overture.core.tests.demos;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections4.CollectionUtils;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;

/**
 * The {@link DefNamesTestResult} class holds test result info for an identify
 * test on an Overture example. It is similar to a counterpart class in the
 * main test demos but since those are not deployed, they cannot be
 * reused, nor are they meant to.
 * 
 * @author ldc
 *
 */
public class DefNamesTestResult {

	String exampleName;
	List<String> defNames;

	public List<String> getDefNames() {
		return defNames;
	}

	public String getName() {
		return exampleName;
	}

	public DefNamesTestResult(List<INode> ast, String name) {
		this.exampleName = name;
		defNames = new Vector<String>();
		for (INode n : ast) {
			if (n instanceof AModuleModules) // ModuleModules prints the file
												// path so we skip it
			{
				for (PDefinition p : ((AModuleModules) n).getDefs()) {
					if (p.getName() != null) {
						defNames.add(p.getName().getName());
					}
				}

			} else if (n instanceof SClassDefinition) {
				for (PDefinition p : ((SClassDefinition) n).getDefinitions()) {
					if (p.getName() != null) {
						defNames.add(p.getName().getName());
					}
				}
			}
		}
	}

	public static void compare(DefNamesTestResult actual,
			DefNamesTestResult expected, String testName) {
		Collection<String> stored_notfound = CollectionUtils.removeAll(
				expected.getDefNames(), actual.getDefNames());
		Collection<String> found_notstored = CollectionUtils.removeAll(
				actual.getDefNames(), expected.getDefNames());

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
			fail("Error in example " + actual.getName() + " : \n"
					+ sb.toString());
		}
	}
}
