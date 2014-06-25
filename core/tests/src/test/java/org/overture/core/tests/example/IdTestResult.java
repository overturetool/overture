package org.overture.core.tests.example;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.overture.ast.node.INode;

public class IdTestResult extends Vector<String> implements Serializable,
		List<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static IdTestResult convert(List<INode> ast) {
		IdTestResult r = new IdTestResult();
		for (INode n : ast) {
			r.add(n.toString());
		}
		return r;
	}
}
