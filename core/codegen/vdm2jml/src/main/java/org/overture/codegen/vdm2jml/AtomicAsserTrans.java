package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.statements.AAtomicStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.logging.Logger;

public abstract class AtomicAsserTrans extends DepthFirstAnalysisAdaptor
{
	protected JmlGenerator jmlGen;
	protected List<String> recVarChecks = null;

	public AtomicAsserTrans(JmlGenerator jmlGen)
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
		
		appendAssertsToAtomic(node);
		
		recVarChecks = null;
	}

	protected void appendAssertsToAtomic(AAtomicStmCG node)
	{
		if (node.parent() != null)
		{
			ABlockStmCG replBlock = new ABlockStmCG();
			jmlGen.getJavaGen().getTransAssistant().replaceNodeWith(node, replBlock);
			replBlock.getStatements().add(node);
			
			for(String str : recVarChecks)
			{
				AMetaStmCG assertion = new AMetaStmCG();
				jmlGen.getAnnotator().appendMetaData(assertion, jmlGen.getAnnotator().consMetaData(str));
				replBlock.getStatements().add(assertion);
			}
		} else
		{
			Logger.getLog().printErrorln("Could not find parent node of " + node
					+ " and therefore no assertion could be inserted (in"
					+ this.getClass().getSimpleName() + ")");
		}
	}
	
	protected void appendAsserts(SStmCG stm, String str)
	{
		if (stm.parent() != null)
		{
			AMetaStmCG assertion = new AMetaStmCG();
			jmlGen.getAnnotator().appendMetaData(assertion, jmlGen.getAnnotator().consMetaData(str));

			ABlockStmCG replacementBlock = new ABlockStmCG();

			jmlGen.getJavaGen().getTransAssistant().replaceNodeWith(stm, replacementBlock);

			replacementBlock.getStatements().add(stm);
			replacementBlock.getStatements().add(assertion);

		} else
		{
			Logger.getLog().printErrorln("Could not find parent node of " + stm
					+ " and therefore no assertion could be inserted (in"
					+ this.getClass().getSimpleName() + ")");
		}
	}
}