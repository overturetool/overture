package org.overture.codegen.transform;

import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.AThrowStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;
import org.overture.codegen.ooast.OoAstInfo;

public class LetBeStAssistantCG extends TransformationAssistantCG
{
	public LetBeStAssistantCG(OoAstInfo info)
	{
		super(info);
	}

	private AThrowStmCG consThrowException()
	{
		AStringLiteralExpCG runtimeErrorMessage = new AStringLiteralExpCG();
		runtimeErrorMessage.setIsNull(false);
		runtimeErrorMessage.setType(new AStringTypeCG());
		runtimeErrorMessage.setValue("Let Be St found no applicable bindings");
		
		AClassTypeCG exceptionType = new AClassTypeCG();
		exceptionType.setName(IJavaCodeGenConstants.RUNTIME_EXCEPTION_TYPE_NAME);
		
		ATypeNameCG exceptionTypeName = new ATypeNameCG();
		exceptionTypeName.setDefiningClass(null);
		exceptionTypeName.setName(IJavaCodeGenConstants.RUNTIME_EXCEPTION_TYPE_NAME);
		
		ANewExpCG runtimeException = new ANewExpCG();
		runtimeException.setType(exceptionType);
		runtimeException.setName(exceptionTypeName);
		runtimeException.getArgs().add(runtimeErrorMessage);
		
		AThrowStmCG throwStm = new AThrowStmCG();
		throwStm.setExp(runtimeException);
		
		return throwStm;
	}
	
	public AIfStmCG consIfCheck(String successVarName)
	{
		AIfStmCG ifStm = new AIfStmCG();
		ifStm.setIfExp(consBoolCheck(successVarName, true));
		ifStm.setThenStm(consThrowException());
		
		return ifStm;
	}
}
