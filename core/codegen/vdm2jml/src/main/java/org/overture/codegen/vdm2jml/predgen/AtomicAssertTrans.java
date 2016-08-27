package org.overture.codegen.vdm2jml.predgen;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.statements.AAtomicStmIR;
import org.overture.codegen.ir.statements.AMetaStmIR;
import org.overture.codegen.vdm2jml.JmlGenerator;

public abstract class AtomicAssertTrans extends DepthFirstAnalysisAdaptor
{
	protected JmlGenerator jmlGen;
	private List<AMetaStmIR> recVarChecks = null;

	public AtomicAssertTrans(JmlGenerator jmlGen)
	{
		this.jmlGen = jmlGen;
	}

	@Override
	public void caseAAtomicStmIR(AAtomicStmIR node) throws AnalysisException
	{
		recVarChecks = new LinkedList<AMetaStmIR>();

		for (SStmIR stm : node.getStatements())
		{
			stm.apply(this);
		}

		ADefaultClassDeclIR encClass = node.getAncestor(ADefaultClassDeclIR.class);

		node.getStatements().addFirst(consInvChecksStm(false, encClass));
		node.getStatements().add(consInvChecksStm(true, encClass));

		for (AMetaStmIR as : recVarChecks)
		{
			node.getStatements().add(as);
		}

		recVarChecks = null;
	}

	public AMetaStmIR consMetaStm(String str)
	{
		AMetaStmIR assertion = new AMetaStmIR();
		jmlGen.getAnnotator().appendMetaData(assertion, jmlGen.getAnnotator().consMetaData(str));

		return assertion;
	}

	protected AMetaStmIR consInvChecksStm(boolean val,
			ADefaultClassDeclIR encClass)
	{
		AMetaStmIR setStm = new AMetaStmIR();

		String setStmStr = String.format(JmlGenerator.JML_SET_INV_CHECKS, this.jmlGen.getAnnotator().consInvChecksOnNameEncClass(encClass), val);
		List<ClonableString> setMetaData = jmlGen.getAnnotator().consMetaData(setStmStr);
		jmlGen.getAnnotator().appendMetaData(setStm, setMetaData);

		return setStm;
	}

	public JmlGenerator getJmlGen()
	{
		return jmlGen;
	}

	public void addPostAtomicCheck(AMetaStmIR check)
	{
		if (!contains(check))
		{
			recVarChecks.add(check);
		}
	}

	private boolean contains(AMetaStmIR check)
	{
		for (AMetaStmIR as : recVarChecks)
		{
			if (jmlGen.getJavaGen().getInfo().getStmAssistant().equal(as, check))
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