package org.overture.codegen.traces;

import java.util.Map;

import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public interface ICallStmToStringMethodBuilder
{
	public AMethodDeclCG consToString(IRInfo info, SStmCG callStm,
			Map<String, String> idConstNameMap,
			StoreAssistant storeAssistant,
			TransAssistantCG transAssistant);
}
