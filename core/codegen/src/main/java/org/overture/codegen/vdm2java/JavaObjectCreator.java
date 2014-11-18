package org.overture.codegen.vdm2java;

import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;

public class JavaObjectCreator
{
	public JavaObjectCreator()
	{
		super();
	}

	protected AMethodDeclCG consDefaultCtorSignature(String className)
	{
		AMethodDeclCG constructor = new AMethodDeclCG();
		constructor.setAccess(JavaFormat.JAVA_PUBLIC);
		constructor.setIsConstructor(true);
		constructor.setName(className);
		
		return constructor;
	}

	protected AMethodDeclCG consCloneSignature(AMethodTypeCG methodType)
	{
		AMethodDeclCG method = new AMethodDeclCG();
		method.setAccess(JavaFormat.JAVA_PUBLIC);
		method.setName("clone");
		method.setMethodType(methodType);
		
		return method;
	}

	protected AMethodDeclCG consEqualMethodSignature(String paramName)
	{
		AMethodDeclCG equalsMethod = new AMethodDeclCG();
		
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.getParams().add(new AObjectTypeCG());
	
		AExternalTypeCG returnType = new AExternalTypeCG();
		returnType.setInfo(null);
		returnType.setName("boolean");
	
		methodType.setResult(returnType);
	
		equalsMethod.setAccess(JavaFormat.JAVA_PUBLIC);
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

	protected AMethodDeclCG consHashcodeMethodSignature()
	{
		String hashCode = "hashCode";
	
		AMethodDeclCG hashcodeMethod = new AMethodDeclCG();
	
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

	protected AMethodDeclCG consToStringSignature()
	{
		AMethodDeclCG toStringMethod = new AMethodDeclCG();
	
		toStringMethod.setAccess(JavaFormat.JAVA_PUBLIC);
		toStringMethod.setName("toString");
	
		AStringTypeCG returnType = new AStringTypeCG();
	
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(returnType);
	
		toStringMethod.setMethodType(methodType);
		return toStringMethod;
	}

}