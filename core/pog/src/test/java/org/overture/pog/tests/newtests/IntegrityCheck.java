package org.overture.pog.tests.newtests;

import java.util.List;

import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.node.INode;

public interface IntegrityCheck extends IAnalysis
{

	public List<INode> getProblemNodes();
	
}
