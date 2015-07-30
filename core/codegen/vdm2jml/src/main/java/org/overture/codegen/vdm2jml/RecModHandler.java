package org.overture.codegen.vdm2jml;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
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

	public void handleCallObj(ACallObjectExpStmCG node)
	{
		if (util.simpleRecSetCallOutsideAtomic(node))
		{
			/**
			 * E.g. rec.set_(3). Setter call to record outside atomic statement block
			 */
			return;
		}

		SExpCG subject = invTrans.getJmlGen().getJavaGen().getInfo().getExpAssistant().findSubject(node.getObj());

		if (subject instanceof SVarExpCG)
		{
			SVarExpCG var = (SVarExpCG) subject;

			if (util.isRec(var))
			{
				util.handleRecAssert(node, var);
			}
		} else
		{
			Logger.getLog().printErrorln("Expected target to a variable expression at this point. Got "
					+ subject + " in '" + this.getClass().getSimpleName()
					+ "'");
		}
	}

	public void handleMapSeq(AMapSeqUpdateStmCG node) throws AnalysisException
	{
		SExpCG subject = invTrans.getJmlGen().getJavaGen().getInfo().getExpAssistant().findSubject(node.getCol());

		if (subject instanceof SVarExpCG)
		{
			if (util.isRec(subject))
			{
				util.handleRecAssert(node, (SVarExpCG) subject);
			}
		} else
		{
			Logger.getLog().printErrorln("Expected 'next' to be a variable expression at this point. Got: "
					+ subject + " in '" + this.getClass().getSimpleName()
					+ "'");
		}
	}
	
	public InvAssertionTrans getInvTrans()
	{
		return invTrans;
	}
}
