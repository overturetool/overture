package org.overture.codegen.vdm2jml.predgen;

import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
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
			// E.g. rec.set_x(3). Setter call to record outside atomic statement block
			// or ((R) rec).set_x(3);
			return null;
		}

		if (node.getObj() instanceof SVarExpCG)
		{
			SVarExpCG subject = (SVarExpCG) node.getObj();
			
			if (util.assertRec(subject))
			{
				ARecordTypeCG recType = (ARecordTypeCG) subject.getType();
				
				return util.handleRecAssert(subject, subject.getName(), recType);
			}
		}
		else if(node.getObj() instanceof ACastUnaryExpCG)
		{
			ACastUnaryExpCG subject = (ACastUnaryExpCG) node.getObj();
			
			if(subject.getExp() instanceof SVarExpCG)
			{
				SVarExpCG var = (SVarExpCG) subject.getExp();
				
				if (util.assertRec(subject))
				{
					ARecordTypeCG recType = (ARecordTypeCG) subject.getType();
					
					return util.handleRecAssert(subject, var.getName(), recType);
				}
			}
			else
			{
				Logger.getLog().printErrorln("Expected subject of cast expression to be a variable in '"
						+ this.getClass().getSimpleName() + "'. Got: " + subject.getExp());
			}
		}
		else
		{
			Logger.getLog().printErrorln("Expected target to be a variable or cast expression at this point. Got "
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
			ARecordTypeCG recType = (ARecordTypeCG) var.getType();
			
			return invTrans.consMetaStm(util.consValidRecCheck(var, var.getName(), recType));
		} else
		{
			return null;
		}
	}
}
