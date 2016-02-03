package org.overture.codegen.vdm2jml.predgen;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.expressions.ACastUnaryExpIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.AMetaStmIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.vdm2jml.JmlGenerator;

public class RecModUtil
{
	private RecModHandler handler;
	
	public RecModUtil(RecModHandler handler)
	{
		this.handler = handler;
	}

	public boolean simpleRecSetCallOutsideAtomic(ACallObjectExpStmIR node)
	{
		if(handler.getInvTrans().getJmlGen().getJavaGen().getInfo().getStmAssistant().inAtomic(node))
		{
			return false;
		}
		
		SExpIR obj = node.getObj();
		
		if(!(obj.getType() instanceof ARecordTypeIR))
		{
			return false;
		}
		
		if(obj instanceof SVarExpIR)
		{
			return true;
		}

		if(obj instanceof ACastUnaryExpIR && ((ACastUnaryExpIR) obj).getExp() instanceof SVarExpIR)
		{
			return true;
		}
		
		return false;
	}

	public AMetaStmIR handleRecAssert(SExpIR var, String varName, ARecordTypeIR recType)
	{
		return handler.getInvTrans().consMetaStm(consValidRecCheck(var, varName, recType));
	}

	public String consValidRecCheck(SExpIR subject, String varName, ARecordTypeIR recType)
	{
		StringBuilder as = new StringBuilder();
		as.append("//@ assert ");
		
		String subjectStr;
		if(subject instanceof ACastUnaryExpIR)
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

	public boolean assertRec(SExpIR exp)
	{
		if(exp.getType().getNamedInvType() != null || !(exp.getType() instanceof ARecordTypeIR))
		{
			return false;
		}
		
		IRInfo info = handler.getInvTrans().getJmlGen().getJavaGen().getInfo();
		ARecordDeclIR rec = info.getDeclAssistant().findRecord(info.getClasses(), (ARecordTypeIR) exp.getType());
		
		return rec.getInvariant() != null;
	}
}
