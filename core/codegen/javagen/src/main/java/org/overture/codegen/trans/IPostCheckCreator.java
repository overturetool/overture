package org.overture.codegen.trans;

import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG;

public interface IPostCheckCreator
{
	public AApplyExpCG consPostCheckCall(AMethodDeclCG method,
			AApplyExpCG postCondCall, AIdentifierVarExpCG resultVar,
			AStringLiteralExpCG methodName);
}
