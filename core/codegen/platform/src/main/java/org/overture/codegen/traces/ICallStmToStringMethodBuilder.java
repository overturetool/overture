package org.overture.codegen.traces;

import java.util.Map;

import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.SStmCG;
import org.overture.codegen.ir.declarations.AMethodDeclCG;
import org.overture.codegen.ir.expressions.AApplyExpCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public interface ICallStmToStringMethodBuilder
{
	public AMethodDeclCG consToString(IRInfo info, SStmCG callStm,
			Map<String, String> idConstNameMap,
			StoreAssistant storeAssistant,
			TransAssistantCG transAssistant);
	
	public AApplyExpCG toStringOf(SExpCG exp);
}
