package org.overture.codegen.vdm2java;

import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.ir.IRGeneratedTag;

abstract public class JavaClassCreatorBase
{
	public JavaClassCreatorBase()
	{
		super();
	}
	
	public AMethodDeclCG consDefaultCtorSignature(String className)
	{
		AMethodDeclCG constructor = new AMethodDeclCG();
		constructor.setAccess(JavaFormat.JAVA_PUBLIC);
		constructor.setIsConstructor(true);
		constructor.setName(className);
		
		return constructor;
	}

	public AMethodDeclCG consCopySignature(AMethodTypeCG methodType)
	{
		AMethodDeclCG method = new AMethodDeclCG();
		method.setIsConstructor(false);
		method.setAccess(JavaFormat.JAVA_PUBLIC);
		method.setName("copy");
		method.setMethodType(methodType);
		
		return method;
	}

	public AMethodDeclCG consEqualMethodSignature(String paramName)
	{
		AMethodDeclCG equalsMethod = new AMethodDeclCG();
		
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.getParams().add(new AObjectTypeCG());
	
		AExternalTypeCG returnType = new AExternalTypeCG();
		returnType.setInfo(null);
		returnType.setName("boolean");
	
		methodType.setResult(returnType);
	
		equalsMethod.setAccess(JavaFormat.JAVA_PUBLIC);
		equalsMethod.setIsConstructor(false);
		equalsMethod.setName("equals");
		equalsMethod.setMethodType(methodType);
	
		// Add the formal parameter "Object obj" to the method
		AFormalParamLocalParamCG formalParam = new AFormalParamLocalParamCG();
	
		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(paramName);
	
		formalParam.setPattern(idPattern);
		AObjectTypeCG paramType = new AObjectTypeCG();
		formalParam.setType(paramType);
		equalsMethod.getFormalParams().add(formalParam);
	
		return equalsMethod;
	}

	public AMethodDeclCG consHashcodeMethodSignature()
	{
		String hashCode = "hashCode";
	
		AMethodDeclCG hashcodeMethod = new AMethodDeclCG();
		hashcodeMethod.setIsConstructor(false);
		hashcodeMethod.setAccess(JavaFormat.JAVA_PUBLIC);
		hashcodeMethod.setName(hashCode);
	
		String intTypeName = JavaFormat.JAVA_INT;
		AExternalTypeCG intBasicType = new AExternalTypeCG();
		intBasicType.setName(intTypeName);
	
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(intBasicType);
	
		hashcodeMethod.setMethodType(methodType);
		return hashcodeMethod;
	}

	public AMethodDeclCG consToStringSignature()
	{
		AMethodDeclCG toStringMethod = new AMethodDeclCG();
		toStringMethod.setTag(new IRGeneratedTag(getClass().getName()));
	
		toStringMethod.setIsConstructor(false);
		toStringMethod.setAccess(JavaFormat.JAVA_PUBLIC);
		toStringMethod.setStatic(false);
		toStringMethod.setName("toString");
	
		AStringTypeCG returnType = new AStringTypeCG();
	
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(returnType);
	
		toStringMethod.setMethodType(methodType);
		return toStringMethod;
	}
	
	public AApplyExpCG consUtilCall(STypeCG returnType, String memberName)
	{
		AExplicitVarExpCG member = new AExplicitVarExpCG();

		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(returnType.clone());
		member.setType(methodType);
		member.setIsLambda(false);
		member.setIsLocal(false);
		AExternalTypeCG classType = new AExternalTypeCG();
		classType.setName(JavaFormat.UTILS_FILE);
		member.setClassType(classType);
		member.setName(memberName);
		AApplyExpCG call = new AApplyExpCG();
		call.setType(returnType.clone());
		call.setRoot(member);
		
		return call;
	}
	
	public AApplyExpCG consUtilsToStringCall()
	{
		return consUtilCall(new AStringTypeCG(), "toString");
	}
}