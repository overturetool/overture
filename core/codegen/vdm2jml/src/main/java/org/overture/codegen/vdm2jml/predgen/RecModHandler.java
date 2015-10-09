package org.overture.codegen.vdm2jml.predgen;

import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.logging.Logger;

public class RecModHandler
{
	private TypePredDecorator invTrans;
	private RecModUtil util;

	public RecModHandler(TypePredDecorator invTrans)
	{
		this.invTrans = invTrans;
		this.util = new RecModUtil(this);
	}

	public AMetaStmCG handleCallObj(ACallObjectExpStmCG node)
	{
		if (util.simpleRecSetCallOutsideAtomic(node))
		{
			// E.g. rec.set_(3). Setter call to record outside atomic statement block
			return null;
		}

		if (node.getObj() instanceof SVarExpCG)
		{
			SVarExpCG var = (SVarExpCG) node.getObj();

			if (util.assertRec(var))
			{
				return util.handleRecAssert(var);
			}
		} else
		{
			Logger.getLog().printErrorln("Expected target to a variable expression at this point. Got "
					+ node.getObj() + " in '" + this.getClass().getSimpleName() + "'");
		}
		return null;
	}

	public TypePredDecorator getInvTrans()
	{
		return invTrans;
	}
	
	public AMetaStmCG consAssert(AIdentifierVarExpCG var)
	{
		if (util.assertRec(var))
		{
			return invTrans.consMetaStm(util.consValidRecCheck(var));
		} else
		{
			return null;
		}
	}
}
