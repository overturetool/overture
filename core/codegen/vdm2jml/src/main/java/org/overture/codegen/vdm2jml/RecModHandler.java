package org.overture.codegen.vdm2jml;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.logging.Logger;

public class RecModHandler
{
	private InvAssertionTrans invTrans;

	public RecModHandler(InvAssertionTrans invTrans)
	{
		this.invTrans = invTrans;
	}

	public void handleCallObj(ACallObjectExpStmCG node)
	{
		if (simpleRecSetCallOutsideAtomic(node))
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

			if (isRec(var))
			{
				handleRecAssert(node, var);
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
			if (isRec(subject))
			{
				handleRecAssert(node, (SVarExpCG) subject);
			}
		} else
		{
			Logger.getLog().printErrorln("Expected 'next' to be a variable expression at this point. Got: "
					+ subject + " in '" + this.getClass().getSimpleName()
					+ "'");
		}
	}
	
	public boolean simpleRecSetCallOutsideAtomic(ACallObjectExpStmCG node)
	{
		return !invTrans.getJmlGen().getJavaGen().getInfo().getStmAssistant().inAtomic(node)
				&& node.getObj() instanceof SVarExpCG
				&& node.getObj().getType() instanceof ARecordTypeCG;
	}

	public void handleRecAssert(SStmCG stm, SVarExpCG var)
	{
		if (invTrans.getRecChecks() != null)
		{
			String recCheck = consValidRecCheck(var);

			// No need to assert the same thing twice
			if (!invTrans.getRecChecks().contains(recCheck))
			{
				invTrans.getRecChecks().add(recCheck);
			}
		} else
		{
			invTrans.appendAsserts(stm, consValidRecCheck(var));
		}
	}

	public String consValidRecCheck(SVarExpCG var)
	{
		return "//@ assert " + var.getName() + ".valid();";
	}

	public boolean isRec(SExpCG exp)
	{
		return exp.getType().getNamedInvType() == null
				&& exp.getType() instanceof ARecordTypeCG;
	}
}
