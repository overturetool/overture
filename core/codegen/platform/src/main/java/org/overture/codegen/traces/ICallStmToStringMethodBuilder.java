package org.overture.codegen.traces;

import java.util.Map;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public interface ICallStmToStringMethodBuilder
{
	public AMethodDeclIR consToString(IRInfo info, SStmIR callStm,
			Map<String, String> idConstNameMap,
			StoreAssistant storeAssistant,
			TransAssistantIR transAssistant);
	
	public AApplyExpIR toStringOf(SExpIR exp);
}
