package org.overture.codegen.vdm2java;

import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.STypeCG;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.ir.declarations.AMethodDeclCG;
import org.overture.codegen.ir.expressions.AApplyExpCG;
import org.overture.codegen.ir.expressions.AExplicitVarExpCG;
import org.overture.codegen.ir.patterns.AIdentifierPatternCG;
import org.overture.codegen.ir.types.AExternalTypeCG;
import org.overture.codegen.ir.types.AMethodTypeCG;
import org.overture.codegen.ir.types.AObjectTypeCG;
import org.overture.codegen.ir.types.AStringTypeCG;
import org.overture.codegen.ir.types.ATemplateTypeCG;
import org.overture.codegen.ir.IRGeneratedTag;
import org.overture.codegen.logging.Logger;

abstract public class JavaClassCreatorBase
{
	private static final String COPY = "copy";
	
	public JavaClassCreatorBase()
	{
		super();
	}
	
	public AMethodDeclCG consDefaultCtorSignature(String className)
	{
		AMethodDeclCG constructor = new AMethodDeclCG();
		constructor.setImplicit(false);
		constructor.setAccess(IJavaConstants.PUBLIC);
		constructor.setIsConstructor(true);
		constructor.setName(className);
		
		return constructor;
	}

	public AMethodDeclCG consCopySignature(AMethodTypeCG methodType)
	{
		AMethodDeclCG method = new AMethodDeclCG();
		method.setIsConstructor(false);
		method.setImplicit(false);
		method.setAccess(IJavaConstants.PUBLIC);
		method.setName(COPY);
		method.setMethodType(methodType);
		
		return method;
	}

	public AMethodDeclCG consEqualMethodSignature(String paramName)
	{
		AMethodDeclCG equalsMethod = new AMethodDeclCG();
		
		equalsMethod.setImplicit(false);
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.getParams().add(new AObjectTypeCG());
	
		AExternalTypeCG returnType = new AExternalTypeCG();
		returnType.setInfo(null);
		returnType.setName(IJavaConstants.BOOLEAN);
	
		methodType.setResult(returnType);
	
		equalsMethod.setAccess(IJavaConstants.PUBLIC);
		equalsMethod.setIsConstructor(false);
		equalsMethod.setName(IJavaConstants.EQUALS);
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
		AMethodDeclCG hashcodeMethod = new AMethodDeclCG();
		hashcodeMethod.setImplicit(false);
		hashcodeMethod.setIsConstructor(false);
		hashcodeMethod.setAccess(IJavaConstants.PUBLIC);
		hashcodeMethod.setName(IJavaConstants.HASH_CODE);
	
		String intTypeName = IJavaConstants.INT;
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
		toStringMethod.setAccess(IJavaConstants.PUBLIC);
		toStringMethod.setStatic(false);
		toStringMethod.setName(IJavaConstants.TO_STRING);
	
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
	
	public AApplyExpCG consUtilCopyCall()
	{
		ATemplateTypeCG copyType = new ATemplateTypeCG();
		copyType.setName("T");
		
		AApplyExpCG copyCall = consUtilCall(copyType, COPY);
		
		SExpCG member = copyCall.getRoot();
		
		if (member instanceof AExplicitVarExpCG && ((AExplicitVarExpCG) member).getType() instanceof AMethodTypeCG)
		{
			AMethodTypeCG methodType = (AMethodTypeCG) member.getType();
			methodType.getParams().add(member.getType().clone());
		} else
		{
			Logger.getLog().printErrorln("Expected type of call expression to be a method type at this point in '"
					+ this.getClass().getSimpleName() + "'. Got: "
					+ copyCall.getType());
		}
		
		return copyCall;
	}
	
	public AApplyExpCG consUtilsToStringCall()
	{
		return consUtilCall(new AStringTypeCG(), IJavaConstants.TO_STRING);
	}
}