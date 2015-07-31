package org.overture.codegen.vdm2jml;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.logging.Logger;

public class RecModHandler
{
	private InvAssertionTrans invTrans;
	private RecModUtil util;

	public RecModHandler(InvAssertionTrans invTrans)
	{
		this.invTrans = invTrans;
		this.util = new RecModUtil(this);
	}

	public AMetaStmCG handleCallObj(ACallObjectExpStmCG node)
	{
		if (util.simpleRecSetCallOutsideAtomic(node))
		{
			/**
			 * E.g. rec.set_(3). Setter call to record outside atomic statement block
			 */
			return null;
		}

		SExpCG subject = invTrans.getJmlGen().getJavaGen().getInfo().getExpAssistant().findSubject(node.getObj());

		if (subject instanceof SVarExpCG)
		{
			SVarExpCG var = (SVarExpCG) subject;

			if (util.isRec(var))
			{
				return util.handleRecAssert(node, var);
			}
		} else
		{
			Logger.getLog().printErrorln("Expected target to a variable expression at this point. Got "
					+ subject + " in '" + this.getClass().getSimpleName()
					+ "'");
		}
		return null;
	}

	public AMetaStmCG handleMapSeq(AMapSeqUpdateStmCG node) throws AnalysisException
	{
		SExpCG subject = invTrans.getJmlGen().getJavaGen().getInfo().getExpAssistant().findSubject(node.getCol());

		if (subject instanceof SVarExpCG)
		{
			if (util.isRec(subject))
			{
				return util.handleRecAssert(node, (SVarExpCG) subject);
			}
		} else
		{
			Logger.getLog().printErrorln("Expected 'next' to be a variable expression at this point. Got: "
					+ subject + " in '" + this.getClass().getSimpleName()
					+ "'");
		}
		
		return null;
	}
	
	public InvAssertionTrans getInvTrans()
	{
		return invTrans;
	}
}
