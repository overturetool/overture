package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.statements.AAtomicStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.logging.Logger;

public abstract class AtomicAssertTrans extends DepthFirstAnalysisAdaptor
{
	protected JmlGenerator jmlGen;
	private List<String> recVarChecks = null;
	
	public AtomicAssertTrans(JmlGenerator jmlGen)
	{
		this.jmlGen = jmlGen;
	}
	
	@Override
	public void caseAAtomicStmCG(AAtomicStmCG node) throws AnalysisException
	{
		recVarChecks = new LinkedList<String>();
		
		for(SStmCG stm : node.getStatements())
		{
			stm.apply(this);
		}
		
		node.getStatements().addFirst(consInvChecksStm(false));
		node.getStatements().add(consInvChecksStm(true));
		
		for(String str : recVarChecks)
		{
			AMetaStmCG assertion = new AMetaStmCG();
			jmlGen.getAnnotator().appendMetaData(assertion, jmlGen.getAnnotator().consMetaData(str));
			node.getStatements().add(assertion);
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
	
	public boolean hasCheck(String check)
	{
		return recVarChecks.contains(check);
	}
	
	public void addCheck(String check)
	{
		recVarChecks.add(check);
	}
	
	public void addCheck(AMetaStmCG check)
	{
		//TODO: Need more consistent handling of these checks
		if (check.getMetaData().size() == 1)
		{
			String checkStr = check.getMetaData().get(0).value;
			recVarChecks.add(checkStr);
		}
	}
	
	public boolean inAtomic()
	{
		return recVarChecks != null;
	}
}