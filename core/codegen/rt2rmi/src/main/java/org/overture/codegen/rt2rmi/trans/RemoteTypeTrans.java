package org.overture.codegen.rt2rmi.trans;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.types.AClassTypeCG;

public class RemoteTypeTrans extends DepthFirstAnalysisAdaptor
{
	@Override
	public void caseAClassTypeCG(AClassTypeCG node) throws AnalysisException
	{
		//TODO: Implement
		System.out.println("Chatty transformation found class type: " + node.getName());
	}
}
