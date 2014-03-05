package org.overture.codegen.transform;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG;
import org.overture.codegen.cgast.expressions.AVariableExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.AThrowStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;
import org.overture.codegen.ooast.OoAstInfo;

public class LetBeStAssistantCG extends TransformationAssistantCG
{
	private OoAstInfo info;
	
	public LetBeStAssistantCG(OoAstInfo info)
	{
		this.info = info;
	}
	
	public ALocalVarDeclCG consSuccessVarDecl(String successVarName)
	{
		ALocalVarDeclCG successVarDecl = new ALocalVarDeclCG();
		
		successVarDecl.setType(new ABoolBasicTypeCG());
		successVarDecl.setName(successVarName);
		successVarDecl.setExp(info.getExpAssistant().consBoolLiteral(false));
		
		return successVarDecl;
	}
	
	public AAssignmentStmCG consSuccessAssignment(PExpCG suchThat, String successVarName)
	{
		AAssignmentStmCG successAssignment = new AAssignmentStmCG();

		successAssignment.setTarget(consIdentifier(successVarName));
		successAssignment.setExp(suchThat != null ? suchThat.clone() : info.getExpAssistant().consBoolLiteral(true));
		
		return successAssignment;
	}

	public PExpCG conForCondition(String iteratorName, String successVarName) throws AnalysisException
	{
		AAndBoolBinaryExpCG andExp = new AAndBoolBinaryExpCG();
		
		andExp.setType(new ABoolBasicTypeCG());
		andExp.setLeft(consInstanceCall(consIteratorType(), iteratorName, new ABoolBasicTypeCG(), IJavaCodeGenConstants.HAS_NEXT_ELEMENT_ITERATOR, null));
		andExp.setRight(consSuccessCheck(successVarName));
		
		return andExp;
	}

	private PExpCG consSuccessCheck(String successVarName)
	{
		AVariableExpCG successVarExp = new AVariableExpCG();
		successVarExp.setType(new ABoolBasicTypeCG());
		successVarExp.setOriginal(successVarName);
		
		ANotUnaryExpCG notSuccess = new ANotUnaryExpCG();
		notSuccess.setType(new ABoolBasicTypeCG());
		notSuccess.setExp(successVarExp);

		return notSuccess;
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
		ifStm.setIfExp(consSuccessCheck(successVarName));
		ifStm.setThenStm(consThrowException());
		
		return ifStm;
	}
}
