package org.overture.codegen.vdm2java;

import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AStringLiteralExpIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.AStringTypeIR;
import org.overture.codegen.trans.IPostCheckCreator;

public class JavaPostCheckCreator implements IPostCheckCreator
{
	private String postCheckMethodName;

	public JavaPostCheckCreator(String postCheckMethodName)
	{
		this.postCheckMethodName = postCheckMethodName;
	}

	public AApplyExpIR consPostCheckCall(AMethodDeclIR method,
			AApplyExpIR postCondCall, AIdentifierVarExpIR resultVar,
			AStringLiteralExpIR methodName)
	{
		AExternalTypeIR externalType = new AExternalTypeIR();
		externalType.setName(JavaFormat.UTILS_FILE);

		AMethodTypeIR methodType = new AMethodTypeIR();
		methodType.setResult(new ABoolBasicTypeIR());
		methodType.getParams().add(method.getMethodType().getResult().clone());
		methodType.getParams().add(new ABoolBasicTypeIR());
		methodType.getParams().add(new AStringTypeIR());

		AExplicitVarExpIR explicitVar = new AExplicitVarExpIR();
		explicitVar.setType(methodType);
		explicitVar.setIsLambda(false);
		explicitVar.setIsLocal(false);
		explicitVar.setName(postCheckMethodName);
		explicitVar.setClassType(externalType);

		AApplyExpIR utilsCall = new AApplyExpIR();
		utilsCall.setRoot(explicitVar);
		utilsCall.setType(methodType.getResult().clone());
		utilsCall.getArgs().add(resultVar);
		utilsCall.getArgs().add(postCondCall);
		utilsCall.getArgs().add(methodName);

		return utilsCall;
	}
}
