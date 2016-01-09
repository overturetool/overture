package org.overture.codegen.vdm2jml.predgen;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
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
		if(handler.getInvTrans().getJmlGen().getJavaGen().getInfo().getStmAssistant().inAtomic(node))
		{
			return false;
		}
		
		SExpCG obj = node.getObj();
		
		if(!(obj.getType() instanceof ARecordTypeCG))
		{
			return false;
		}
		
		if(obj instanceof SVarExpCG)
		{
			return true;
		}

		if(obj instanceof ACastUnaryExpCG && ((ACastUnaryExpCG) obj).getExp() instanceof SVarExpCG)
		{
			return true;
		}
		
		return false;
	}

	public AMetaStmCG handleRecAssert(SExpCG var, String varName, ARecordTypeCG recType)
	{
		return handler.getInvTrans().consMetaStm(consValidRecCheck(var, varName, recType));
	}

	public String consValidRecCheck(SExpCG subject, String varName, ARecordTypeCG recType)
	{
		StringBuilder as = new StringBuilder();
		as.append("//@ assert ");
		
		String subjectStr;
		if(subject instanceof ACastUnaryExpCG)
		{
			String fullRecType = handler.getInvTrans().getTypePredUtil().fullyQualifiedRecType(recType);
			as.append(varName);
			as.append(JmlGenerator.JAVA_INSTANCEOF);
			as.append(fullRecType);
			as.append(JmlGenerator.JML_IMPLIES);
			
			subjectStr = "((" + fullRecType + ") " + varName + ")";
		}
		else
		{
			subjectStr = varName; 
		}
		
		if(handler.getInvTrans().getJmlGen().getJmlSettings().genInvariantFor())
		{
			as.append(JmlGenerator.JML_INVARIANT_FOR);
			as.append('(');
			as.append(subjectStr);
			as.append(')');
		}
		else
		{
			as.append(subjectStr);
			as.append('.');
			as.append(JmlGenerator.REC_VALID_METHOD_CALL);
		}
		
		as.append(';');
		
		return as.toString();
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
