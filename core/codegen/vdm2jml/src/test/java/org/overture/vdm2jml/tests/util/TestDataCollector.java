package org.overture.vdm2jml.tests.util;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;

public class TestDataCollector extends DepthFirstAnalysisAdaptor
{
	private final List<AAssignToExpStmCG> assignments;
	private final List<ACallObjectExpStmCG> setCalls;
	private final List<AMapSeqUpdateStmCG> mapSeqUpdates;
	private final List<AMetaStmCG> assertions;
	
	public TestDataCollector()
	{
		this.assignments = new LinkedList<AAssignToExpStmCG>();
		this.setCalls = new LinkedList<ACallObjectExpStmCG>();
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
	public void caseACallObjectExpStmCG(ACallObjectExpStmCG node)
			throws AnalysisException
	{
		if (node.getObj().getType() instanceof ARecordTypeCG
				&& node.getType() instanceof AVoidTypeCG)
		{
			setCalls.add(node);
		}
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
		if(node.getMetaData().size() == 1)
		{
			ClonableString elem = node.getMetaData().get(0);
			
			if (elem.value.equals("//@ set invChecksOn = false;")
					|| elem.value.equals("//@ set invChecksOn = true;"))
			{
				return;
			}
		}
		
		assertions.add(node);
	}

	public List<AAssignToExpStmCG> getAssignments()
	{
		return assignments;
	}

	public List<ACallObjectExpStmCG> getSetCalls()
	{
		return setCalls;
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
