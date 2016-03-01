package org.overture.vdm2jml.tests.util;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.AMapSeqUpdateStmIR;
import org.overture.codegen.ir.statements.AMetaStmIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;

public class TestDataCollector extends DepthFirstAnalysisAdaptor
{
	private final List<AAssignToExpStmIR> assignments;
	private final List<ACallObjectExpStmIR> setCalls;
	private final List<AMapSeqUpdateStmIR> mapSeqUpdates;
	private final List<AMetaStmIR> assertions;
	
	public TestDataCollector()
	{
		this.assignments = new LinkedList<AAssignToExpStmIR>();
		this.setCalls = new LinkedList<ACallObjectExpStmIR>();
		this.mapSeqUpdates = new LinkedList<AMapSeqUpdateStmIR>();
		this.assertions = new LinkedList<AMetaStmIR>();
	}
	
	@Override
	public void caseAAssignToExpStmIR(AAssignToExpStmIR node)
			throws AnalysisException
	{
		assignments.add(node);
	}
	
	@Override
	public void caseACallObjectExpStmIR(ACallObjectExpStmIR node)
			throws AnalysisException
	{
		if (node.getObj().getType() instanceof ARecordTypeIR
				&& node.getType() instanceof AVoidTypeIR)
		{
			setCalls.add(node);
		}
	}
	
	@Override
	public void caseAMapSeqUpdateStmIR(AMapSeqUpdateStmIR node)
			throws AnalysisException
	{
		mapSeqUpdates.add(node);
	}

	@Override
	public void caseAMetaStmIR(AMetaStmIR node)
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

	public List<AAssignToExpStmIR> getAssignments()
	{
		return assignments;
	}

	public List<ACallObjectExpStmIR> getSetCalls()
	{
		return setCalls;
	}
	
	public List<AMapSeqUpdateStmIR> getMapSeqUpdates()
	{
		return mapSeqUpdates;
	}

	public List<AMetaStmIR> getAssertions()
	{
		return assertions;
	}
}
