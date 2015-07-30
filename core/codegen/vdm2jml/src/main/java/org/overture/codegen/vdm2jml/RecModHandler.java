package org.overture.codegen.vdm2jml;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.logging.Logger;

public class RecModHandler
{
	//
	// Start of RecModCheckTrans porting
	//
	private InvAssertionTrans invTrans;

	public RecModHandler(InvAssertionTrans invTrans)
	{
		this.invTrans = invTrans;
	}

	// Not there originally
	public void handleCallObj(ACallObjectExpStmCG node)
	{
		//
		// TODO: copy and paste...
		//

		if (!invTrans.getJmlGen().getJavaGen().getInfo().getStmAssistant().inAtomic(node)
				&& node.getObj() instanceof SVarExpCG
				&& node.getObj().getType() instanceof ARecordTypeCG)
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

	// Reconsider..
	public void handleAssign(AAssignToExpStmCG node) throws AnalysisException
	{
		if (node.getTarget() instanceof SVarExpCG
				&& node.getTarget().getType() instanceof ARecordTypeCG)
		{
			/**
			 * E.g. St = new St(..). Violation will be detected when constructing the record value or in the temporary
			 * variable section if the assignment occurs in the context of an atomic statement block
			 */
			return;
		}

		if (!invTrans.getJmlGen().getJavaGen().getInfo().getStmAssistant().inAtomic(node)
				&& node.getTarget() instanceof AFieldExpCG
				&& ((AFieldExpCG) node.getTarget()).getObject().getType() instanceof ARecordTypeCG)
		{
			/**
			 * E.g. rec.set_(3). Setter call to record outside atomic statement block
			 */
			return;
		}

		SExpCG subject = invTrans.getJmlGen().getJavaGen().getInfo().getExpAssistant().findSubject(node.getTarget());

		/**
		 * Note that this case method does not have to consider state updates on the form stateComp(52) := 4 since they
		 * get transformed into AMapSeqUpdateStmCGs which are treated using a separate case method in this visitor
		 */
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

	// Reconsider
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

	private void handleRecAssert(SStmCG stm, SVarExpCG var)
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

	private String consValidRecCheck(SVarExpCG var)
	{
		return "//@ assert " + var.getName() + ".valid();";
	}

	public boolean isRec(SExpCG exp)
	{
		return exp.getType().getNamedInvType() == null
				&& exp.getType() instanceof ARecordTypeCG;
	}
}
