package org.overture.codegen.trans;

import org.overture.codegen.ir.declarations.AMethodDeclCG;
import org.overture.codegen.ir.expressions.AApplyExpCG;
import org.overture.codegen.ir.expressions.AIdentifierVarExpCG;
import org.overture.codegen.ir.expressions.AStringLiteralExpCG;

public interface IPostCheckCreator
{
	public AApplyExpCG consPostCheckCall(AMethodDeclCG method,
			AApplyExpCG postCondCall, AIdentifierVarExpCG resultVar,
			AStringLiteralExpCG methodName);
}
