package org.overture.codegen.trans;

import org.apache.commons.lang.BooleanUtils;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.SLiteralExpCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class OptionTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantCG assist;

	public OptionTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAReturnStmCG(AReturnStmCG node) throws AnalysisException
	{
		if (node.getExp() == null)
		{
			return;
		}

		AMethodDeclCG encMethod = node.getAncestor(AMethodDeclCG.class);

		if (encMethod != null)
		{
			STypeCG result = encMethod.getMethodType().getResult();

			if (BooleanUtils.isTrue(result.getOptional()))
			{
				if (shouldCorrect(node.getExp(), result))
				{
					correctExp(node.getExp(), result);
				}
			}

		} else
		{
			Logger.getLog().printErrorln("Could not find enclosing method of " + node + " in '"
					+ this.getClass().getSimpleName() + "'");
		}
	}

	/**
	 * This method performs a conservative check to see if <code>exp</code> needs to be casted to <code>type</code>
	 * 
	 * @param exp
	 *            The expression that may need to be casted
	 * @param type
	 *            The cast type
	 * @return True if casting is considered needed
	 */
	private boolean shouldCorrect(SExpCG exp, STypeCG type)
	{
		// In case of literals corrections should not be needed
		if (exp instanceof SLiteralExpCG)
		{
			return false;
		}

		// Recall that 'nil' values have 'unknown' types
		return exp != null && !(exp.getType() instanceof AUnknownTypeCG) && exp.getType().getClass() != type.getClass();
	}

	private void correctExp(SExpCG exp, STypeCG type)
	{
		ACastUnaryExpCG cast = new ACastUnaryExpCG();
		assist.replaceNodeWith(exp, cast);
		cast.setType(type.clone());
		cast.setExp(exp);
	}
}
