package org.overture.codegen.vdm2java;

import org.apache.log4j.Logger;
import org.overture.codegen.ir.IRGeneratedTag;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.AObjectTypeIR;
import org.overture.codegen.ir.types.AStringTypeIR;
import org.overture.codegen.ir.types.ATemplateTypeIR;

abstract public class JavaClassCreatorBase
{
	private static final String COPY = "copy";

	private Logger log = Logger.getLogger(this.getClass().getName());

	public JavaClassCreatorBase()
	{
		super();
	}

	public AMethodDeclIR consDefaultCtorSignature(String className)
	{
		AMethodDeclIR constructor = new AMethodDeclIR();
		constructor.setImplicit(false);
		constructor.setAccess(IJavaConstants.PUBLIC);
		constructor.setIsConstructor(true);
		constructor.setName(className);

		return constructor;
	}

	public AMethodDeclIR consCopySignature(AMethodTypeIR methodType)
	{
		AMethodDeclIR method = new AMethodDeclIR();
		method.setIsConstructor(false);
		method.setImplicit(false);
		method.setAccess(IJavaConstants.PUBLIC);
		method.setName(COPY);
		method.setMethodType(methodType);

		return method;
	}

	public AMethodDeclIR consEqualMethodSignature(String paramName)
	{
		AMethodDeclIR equalsMethod = new AMethodDeclIR();

		equalsMethod.setImplicit(false);
		AMethodTypeIR methodType = new AMethodTypeIR();
		methodType.getParams().add(new AObjectTypeIR());

		AExternalTypeIR returnType = new AExternalTypeIR();
		returnType.setInfo(null);
		returnType.setName(IJavaConstants.BOOLEAN);

		methodType.setResult(returnType);

		equalsMethod.setAccess(IJavaConstants.PUBLIC);
		equalsMethod.setIsConstructor(false);
		equalsMethod.setName(IJavaConstants.EQUALS);
		equalsMethod.setMethodType(methodType);

		// Add the formal parameter "Object obj" to the method
		AFormalParamLocalParamIR formalParam = new AFormalParamLocalParamIR();

		AIdentifierPatternIR idPattern = new AIdentifierPatternIR();
		idPattern.setName(paramName);

		formalParam.setPattern(idPattern);
		AObjectTypeIR paramType = new AObjectTypeIR();
		formalParam.setType(paramType);
		equalsMethod.getFormalParams().add(formalParam);

		return equalsMethod;
	}

	public AMethodDeclIR consHashcodeMethodSignature()
	{
		AMethodDeclIR hashcodeMethod = new AMethodDeclIR();
		hashcodeMethod.setImplicit(false);
		hashcodeMethod.setIsConstructor(false);
		hashcodeMethod.setAccess(IJavaConstants.PUBLIC);
		hashcodeMethod.setName(IJavaConstants.HASH_CODE);

		String intTypeName = IJavaConstants.INT;
		AExternalTypeIR intBasicType = new AExternalTypeIR();
		intBasicType.setName(intTypeName);

		AMethodTypeIR methodType = new AMethodTypeIR();
		methodType.setResult(intBasicType);

		hashcodeMethod.setMethodType(methodType);
		return hashcodeMethod;
	}

	public AMethodDeclIR consToStringSignature()
	{
		AMethodDeclIR toStringMethod = new AMethodDeclIR();
		toStringMethod.setTag(new IRGeneratedTag(getClass().getName()));

		toStringMethod.setIsConstructor(false);
		toStringMethod.setAccess(IJavaConstants.PUBLIC);
		toStringMethod.setStatic(false);
		toStringMethod.setName(IJavaConstants.TO_STRING);
		toStringMethod.setAbstract(false);

		AStringTypeIR returnType = new AStringTypeIR();

		AMethodTypeIR methodType = new AMethodTypeIR();
		methodType.setResult(returnType);

		toStringMethod.setMethodType(methodType);
		return toStringMethod;
	}

	public AApplyExpIR consUtilCall(STypeIR returnType, String memberName)
	{
		AExplicitVarExpIR member = new AExplicitVarExpIR();

		AMethodTypeIR methodType = new AMethodTypeIR();
		methodType.setResult(returnType.clone());
		member.setType(methodType);
		member.setIsLambda(false);
		member.setIsLocal(false);
		AExternalTypeIR classType = new AExternalTypeIR();
		classType.setName(JavaFormat.UTILS_FILE);
		member.setClassType(classType);
		member.setName(memberName);
		AApplyExpIR call = new AApplyExpIR();
		call.setType(returnType.clone());
		call.setRoot(member);

		return call;
	}

	public AApplyExpIR consUtilCopyCall()
	{
		ATemplateTypeIR copyType = new ATemplateTypeIR();
		copyType.setName("T");

		AApplyExpIR copyCall = consUtilCall(copyType, COPY);

		SExpIR member = copyCall.getRoot();

		if (member instanceof AExplicitVarExpIR
				&& ((AExplicitVarExpIR) member).getType() instanceof AMethodTypeIR)
		{
			AMethodTypeIR methodType = (AMethodTypeIR) member.getType();
			methodType.getParams().add(member.getType().clone());
		} else
		{
			log.error("Expected type of call expression to be a method type at this point. Got: "
					+ copyCall.getType());
		}

		return copyCall;
	}

	public AApplyExpIR consUtilsToStringCall()
	{
		return consUtilCall(new AStringTypeIR(), IJavaConstants.TO_STRING);
	}
}