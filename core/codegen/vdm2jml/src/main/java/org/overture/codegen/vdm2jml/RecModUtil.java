package org.overture.codegen.vdm2jml;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.ir.IRInfo;

public class RecModUtil
{
	private RecModHandler handler;
	
	public RecModUtil(RecModHandler handler)
	{
		this.handler = handler;
	}

	public boolean simpleRecSetCallOutsideAtomic(ACallObjectExpStmCG node)
	{
		return !handler.getInvTrans().getJmlGen().getJavaGen().getInfo().getStmAssistant().inAtomic(node)
				&& node.getObj() instanceof SVarExpCG
				&& node.getObj().getType() instanceof ARecordTypeCG;
	}

	public AMetaStmCG handleRecAssert(SStmCG stm, SVarExpCG var)
	{
		if (handler.getInvTrans().inAtomic())
		{
			String recCheck = consValidRecCheck(var);

			// No need to assert the same thing twice
			if (!handler.getInvTrans().hasCheck(recCheck))
			{
				handler.getInvTrans().addCheck(recCheck);
			}
		} else
		{
			return handler.getInvTrans().consMetaStm(consValidRecCheck(var));
		}
		
		return null;
	}

	public String consValidRecCheck(SVarExpCG var)
	{
		return "//@ assert " + var.getName() + ".valid();";
	}

	public boolean assertRec(SExpCG exp)
	{
		if(exp.getType().getNamedInvType() != null || !(exp.getType() instanceof ARecordTypeCG))
		{
			return false;
		}
		
		ARecordTypeCG recType = (ARecordTypeCG) exp.getType();
		IRInfo info = handler.getInvTrans().getJmlGen().getJavaGen().getInfo();
		ARecordDeclCG rec = info.getDeclAssistant().findRecord(info.getClasses(), recType);
		
		return rec.getInvariant() != null;
	}
}
