package org.overture.codegen.vdm2java;

import org.overture.codegen.ir.declarations.AMethodDeclCG;
import org.overture.codegen.ir.expressions.AApplyExpCG;
import org.overture.codegen.ir.expressions.AExplicitVarExpCG;
import org.overture.codegen.ir.expressions.AIdentifierVarExpCG;
import org.overture.codegen.ir.expressions.AStringLiteralExpCG;
import org.overture.codegen.ir.types.ABoolBasicTypeCG;
import org.overture.codegen.ir.types.AExternalTypeCG;
import org.overture.codegen.ir.types.AMethodTypeCG;
import org.overture.codegen.ir.types.AStringTypeCG;
import org.overture.codegen.trans.IPostCheckCreator;

public class JavaPostCheckCreator implements IPostCheckCreator
{
	private String postCheckMethodName;

	public JavaPostCheckCreator(String postCheckMethodName)
	{
		this.postCheckMethodName = postCheckMethodName;
	}

	public AApplyExpCG consPostCheckCall(AMethodDeclCG method,
			AApplyExpCG postCondCall, AIdentifierVarExpCG resultVar,
			AStringLiteralExpCG methodName)
	{
		AExternalTypeCG externalType = new AExternalTypeCG();
		externalType.setName(JavaFormat.UTILS_FILE);

		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(new ABoolBasicTypeCG());
		methodType.getParams().add(method.getMethodType().getResult().clone());
		methodType.getParams().add(new ABoolBasicTypeCG());
		methodType.getParams().add(new AStringTypeCG());

		AExplicitVarExpCG explicitVar = new AExplicitVarExpCG();
		explicitVar.setType(methodType);
		explicitVar.setIsLambda(false);
		explicitVar.setIsLocal(false);
		explicitVar.setName(postCheckMethodName);
		explicitVar.setClassType(externalType);

		AApplyExpCG utilsCall = new AApplyExpCG();
		utilsCall.setRoot(explicitVar);
		utilsCall.setType(methodType.getResult().clone());
		utilsCall.getArgs().add(resultVar);
		utilsCall.getArgs().add(postCondCall);
		utilsCall.getArgs().add(methodName);

		return utilsCall;
	}
}
