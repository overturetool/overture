package org.overture.vdm2jml.tests.util;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;

public class TestDataCollector extends DepthFirstAnalysisAdaptor
{
	private final List<AAssignToExpStmCG> assignments;
	private final List<AMapSeqUpdateStmCG> mapSeqUpdates;
	private final List<AMetaStmCG> assertions;
	
	public TestDataCollector()
	{
		this.assignments = new LinkedList<AAssignToExpStmCG>();
		this.mapSeqUpdates = new LinkedList<AMapSeqUpdateStmCG>();
		this.assertions = new LinkedList<AMetaStmCG>();
	}
	
	@Override
	public void caseAAssignToExpStmCG(AAssignToExpStmCG node)
			throws AnalysisException
	{
		assignments.add(node);
	}
	
	@Override
	public void caseAMapSeqUpdateStmCG(AMapSeqUpdateStmCG node)
			throws AnalysisException
	{
		mapSeqUpdates.add(node);
	}

	@Override
	public void caseAMetaStmCG(AMetaStmCG node)
			throws AnalysisException
	{
		assertions.add(node);
	}

	public List<AAssignToExpStmCG> getAssignments()
	{
		return assignments;
	}

	public List<AMapSeqUpdateStmCG> getMapSeqUpdates()
	{
		return mapSeqUpdates;
	}

	public List<AMetaStmCG> getAssertions()
	{
		return assertions;
	}
}
