package org.overture.codegen.traces;

import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.ir.IRInfo;

public interface ICallStmToStringMethodBuilder
{
	public AMethodDeclCG consToString(IRInfo info, SStmCG callStm);
}
