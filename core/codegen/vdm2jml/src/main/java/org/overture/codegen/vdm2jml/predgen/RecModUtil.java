package org.overture.codegen.vdm2jml.predgen;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.vdm2jml.JmlGenerator;

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

	public AMetaStmCG handleRecAssert(SVarExpCG var)
	{
		return handler.getInvTrans().consMetaStm(consValidRecCheck(var));
	}

	public String consValidRecCheck(SVarExpCG var)
	{
		return "//@ assert " + var.getName() + "." + JmlGenerator.REC_VALID_METHOD_CALL + ";";
	}

	public boolean assertRec(SExpCG exp)
	{
		if(exp.getType().getNamedInvType() != null || !(exp.getType() instanceof ARecordTypeCG))
		{
			return false;
		}
		
		IRInfo info = handler.getInvTrans().getJmlGen().getJavaGen().getInfo();
		ARecordDeclCG rec = info.getDeclAssistant().findRecord(info.getClasses(), (ARecordTypeCG) exp.getType());
		
		return rec.getInvariant() != null;
	}
}
