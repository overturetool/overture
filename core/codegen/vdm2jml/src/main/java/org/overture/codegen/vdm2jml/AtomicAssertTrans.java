package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.statements.AAtomicStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;

public abstract class AtomicAssertTrans extends DepthFirstAnalysisAdaptor
{
	protected JmlGenerator jmlGen;
	private List<AMetaStmCG> recVarChecks = null;
	
	public AtomicAssertTrans(JmlGenerator jmlGen)
	{
		this.jmlGen = jmlGen;
	}
	
	@Override
	public void caseAAtomicStmCG(AAtomicStmCG node) throws AnalysisException
	{
		recVarChecks = new LinkedList<AMetaStmCG>();
		
		for(SStmCG stm : node.getStatements())
		{
			stm.apply(this);
		}
		
		node.getStatements().addFirst(consInvChecksStm(false));
		node.getStatements().add(consInvChecksStm(true));
		
		for(AMetaStmCG as : recVarChecks)
		{
			node.getStatements().add(as);
		}
		
		recVarChecks = null;
	}
	
	protected AMetaStmCG consMetaStm(String str)
	{
		AMetaStmCG assertion = new AMetaStmCG();
		jmlGen.getAnnotator().appendMetaData(assertion, jmlGen.getAnnotator().consMetaData(str));
		
		return assertion;
	}
	
	protected AMetaStmCG consInvChecksStm(boolean val)
	{
		AMetaStmCG setStm = new AMetaStmCG();
		String setStr = val ? JmlGenerator.JML_ENABLE_INV_CHECKS
				: JmlGenerator.JML_DISABLE_INV_CHECKS;
		List<ClonableString> setMetaData = jmlGen.getAnnotator().consMetaData(setStr);
		jmlGen.getAnnotator().appendMetaData(setStm, setMetaData);

		return setStm;
	}
	
	public JmlGenerator getJmlGen()
	{
		return jmlGen;
	}
	
	public void addCheck(AMetaStmCG check)
	{
		if(!contains(check))
		{
			recVarChecks.add(check);
		}
	}
	
	private boolean contains(AMetaStmCG check)
	{
		for(AMetaStmCG as : recVarChecks)
		{
			if(eq(as,check))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private boolean eq(AMetaStmCG left, AMetaStmCG right)
	{
		if(left.getMetaData().size() != right.getMetaData().size())
		{
			return false;
		}
		
		for(int i = 0; i < left.getMetaData().size(); i++)
		{
			String currentLeft = left.getMetaData().get(i).value;
			String currentRight = right.getMetaData().get(i).value;
			
			if(!currentLeft.equals(currentRight))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public boolean inAtomic()
	{
		return recVarChecks != null;
	}
}