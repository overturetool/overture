package org.overture.codegen.trans;

import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AStringLiteralExpIR;

public interface IPostCheckCreator
{
	public AApplyExpIR consPostCheckCall(AMethodDeclIR method,
			AApplyExpIR postCondCall, AIdentifierVarExpIR resultVar,
			AStringLiteralExpIR methodName);
}
