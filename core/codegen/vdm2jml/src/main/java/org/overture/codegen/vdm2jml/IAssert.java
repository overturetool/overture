package org.overture.codegen.vdm2jml;

import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;

public interface IAssert
{
	public AMetaStmCG consAssert(AIdentifierVarExpCG var);
}
