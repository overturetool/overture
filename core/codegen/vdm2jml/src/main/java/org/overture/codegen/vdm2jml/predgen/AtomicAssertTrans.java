package org.overture.codegen.vdm2jml.predgen;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.statements.AAtomicStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.vdm2jml.JmlGenerator;

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
		
		ADefaultClassDeclCG encClass = node.getAncestor(ADefaultClassDeclCG.class);
		
		node.getStatements().addFirst(consInvChecksStm(false, encClass));
		node.getStatements().add(consInvChecksStm(true, encClass));
		
		for(AMetaStmCG as : recVarChecks)
		{
			node.getStatements().add(as);
		}
		
		recVarChecks = null;
	}
	
	public AMetaStmCG consMetaStm(String str)
	{
		AMetaStmCG assertion = new AMetaStmCG();
		jmlGen.getAnnotator().appendMetaData(assertion, jmlGen.getAnnotator().consMetaData(str));
		
		return assertion;
	}
	
	protected AMetaStmCG consInvChecksStm(boolean val, ADefaultClassDeclCG encClass)
	{
		AMetaStmCG setStm = new AMetaStmCG();

		String setStmStr = String.format(JmlGenerator.JML_SET_INV_CHECKS, this.jmlGen.getAnnotator().consInvChecksOnNameEncClass(encClass), val);
		List<ClonableString> setMetaData = jmlGen.getAnnotator().consMetaData(setStmStr);
		jmlGen.getAnnotator().appendMetaData(setStm, setMetaData);

		return setStm;
	}
	
	public JmlGenerator getJmlGen()
	{
		return jmlGen;
	}
	
	public void addPostAtomicCheck(AMetaStmCG check)
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
			if(jmlGen.getJavaGen().getInfo().getStmAssistant().equal(as,check))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean inAtomic()
	{
		return recVarChecks != null;
	}
}