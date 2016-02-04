package org.overture.codegen.vdm2jml.predgen;

import org.overture.codegen.ir.expressions.ACastUnaryExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.AMetaStmIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
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

	public AMetaStmIR handleCallObj(ACallObjectExpStmIR node)
	{
		if (util.simpleRecSetCallOutsideAtomic(node))
		{
			// E.g. rec.set_x(3). Setter call to record outside atomic statement block
			// or ((R) rec).set_x(3);
			return null;
		}

		if (node.getObj() instanceof SVarExpIR)
		{
			SVarExpIR subject = (SVarExpIR) node.getObj();
			
			if (util.assertRec(subject))
			{
				ARecordTypeIR recType = (ARecordTypeIR) subject.getType();
				
				return util.handleRecAssert(subject, subject.getName(), recType);
			}
		}
		else if(node.getObj() instanceof ACastUnaryExpIR)
		{
			ACastUnaryExpIR subject = (ACastUnaryExpIR) node.getObj();
			
			if(subject.getExp() instanceof SVarExpIR)
			{
				SVarExpIR var = (SVarExpIR) subject.getExp();
				
				if (util.assertRec(subject))
				{
					ARecordTypeIR recType = (ARecordTypeIR) subject.getType();
					
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
	
	public AMetaStmIR consAssert(AIdentifierVarExpIR var)
	{
		if (util.assertRec(var))
		{
			ARecordTypeIR recType = (ARecordTypeIR) var.getType();
			
			return invTrans.consMetaStm(util.consValidRecCheck(var, var.getName(), recType));
		} else
		{
			return null;
		}
	}
}
