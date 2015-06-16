package org.overture.codegen.vdm2java;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AExternalExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AInstanceofExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ASuperVarExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class JavaQuoteValueCreator extends JavaClassCreatorBase
{
	private static final String GET_INSTANCE_METHOD = "getInstance";
	private static final String HASH_CODE_METHOD = "hashCode";

	private static final String INSTANCE_FIELD = "instance";
	private static final String HASHCODE_FIELD = "hc";
	private static final String EQUALS_METHOD_PARAM = "obj";
	
	private IRInfo info;
	private TransAssistantCG transformationAssistant;
	
	public JavaQuoteValueCreator(IRInfo info, TransAssistantCG transformationAssistant)
	{
		this.info = info;
		this.transformationAssistant = transformationAssistant;
	}
	
	public AClassDeclCG consQuoteValue(String quoteClassName, String quoteName, String userCodePackage)
	{
		AClassDeclCG decl = new AClassDeclCG();
		decl.setAbstract(false);
		decl.setAccess(JavaFormat.JAVA_PUBLIC);
		decl.setName(quoteClassName);
		decl.setStatic(false);
		
		// The package where the quotes are put is userCode.quotes
		if(JavaCodeGenUtil.isValidJavaPackage(userCodePackage))
		{
			String quotePackage = userCodePackage + "." + CodeGenBase.QUOTES;
			decl.setPackage(quotePackage);
		}
		else
		{
			decl.setPackage(CodeGenBase.QUOTES);
		}
		
		decl.getFields().add(consHashcodeField());
		decl.getFields().add(consInstanceField(quoteClassName));
		
		decl.getMethods().add(consQuoteCtor(quoteClassName));
		decl.getMethods().add(consGetInstanceMethod(quoteClassName));
		decl.getMethods().add(consHashcodeMethod());
		decl.getMethods().add(consEqualsMethod(quoteClassName));
		decl.getMethods().add(consToStringMethod(quoteName));
		
		return decl;
	}
	
	private AFieldDeclCG consHashcodeField()
	{
		AExternalTypeCG fieldType = new AExternalTypeCG();
		fieldType.setName(JavaFormat.JAVA_INT);
		
		AFieldDeclCG field = new AFieldDeclCG();
		field.setAccess(JavaFormat.JAVA_PRIVATE);
		field.setVolatile(false);
		field.setFinal(false);
		field.setStatic(true);
		field.setName(HASHCODE_FIELD);
		field.setType(fieldType);
		field.setInitial(consZero());
		
		return field;
	}
	
	private AFieldDeclCG consInstanceField(String name)
	{
		AClassTypeCG quoteClassType = new AClassTypeCG();
		quoteClassType.setName(name);

		AFieldDeclCG field = new AFieldDeclCG();
		field.setAccess(JavaFormat.JAVA_PRIVATE);
		field.setVolatile(false);
		field.setFinal(false);
		field.setStatic(true);
		field.setName(INSTANCE_FIELD);
		field.setType(quoteClassType);
		field.setInitial(info.getExpAssistant().consNullExp());
		
		return field;
	}
	
	private AMethodDeclCG consQuoteCtor(String name)
	{
		AExternalTypeCG fieldType = new AExternalTypeCG();
		fieldType.setName(JavaFormat.JAVA_INT);
		
		AIdentifierVarExpCG hashcodeVar = transformationAssistant.consIdentifierVar(HASHCODE_FIELD, fieldType);
		
		AEqualsBinaryExpCG hashcodeCompare = new AEqualsBinaryExpCG();
		hashcodeCompare.setType(new ABoolBasicTypeCG());
		hashcodeCompare.setLeft(hashcodeVar);
		hashcodeCompare.setRight(consZero());
		
		AIdentifierVarExpCG hashCodeId = transformationAssistant.consIdentifierVar(HASHCODE_FIELD, consFieldType());
		
		AMethodTypeCG hashCodeMethodType = new AMethodTypeCG();
		hashCodeMethodType.setResult(consFieldType());
		
		ASuperVarExpCG superVar = new ASuperVarExpCG();
		superVar.setName(HASH_CODE_METHOD);
		superVar.setType(hashCodeMethodType);
		superVar.setIsLambda(false);
		superVar.setIsLocal(false);
		
		AApplyExpCG superCall = new AApplyExpCG();
		superCall.setType(consFieldType());
		superCall.setRoot(superVar);
		
		AAssignToExpStmCG assignHashcode = new AAssignToExpStmCG();
		assignHashcode.setTarget(hashCodeId);
		assignHashcode.setExp(superCall);
		
		AIfStmCG hashcodeCheck = new AIfStmCG();
		hashcodeCheck.setIfExp(hashcodeCompare);
		hashcodeCheck.setThenStm(assignHashcode);
		
		ABlockStmCG body = new ABlockStmCG();
		body.getStatements().add(hashcodeCheck);
		
		AMethodDeclCG ctor = consDefaultCtorSignature(name);
		ctor.setBody(body);
		
		return ctor;
	}
	
	private AMethodDeclCG consGetInstanceMethod(String name)
	{
		AClassTypeCG quoteClassType = new AClassTypeCG();
		quoteClassType.setName(name);

		AIdentifierVarExpCG instanceVar = transformationAssistant.consIdentifierVar(INSTANCE_FIELD, quoteClassType);
		
		AEqualsBinaryExpCG nullCompare = new AEqualsBinaryExpCG();
		nullCompare.setType(new ABoolBasicTypeCG());
		nullCompare.setLeft(instanceVar);
		nullCompare.setRight(info.getExpAssistant().consNullExp());
		
		AIdentifierVarExpCG instanceId = transformationAssistant.consIdentifierVar(INSTANCE_FIELD,
				quoteClassType.clone());
		
		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setDefiningClass(null);
		typeName.setName(name);
		
		ANewExpCG newQuote = new ANewExpCG();
		newQuote.setName(typeName);
		newQuote.setType(quoteClassType);
		
		AAssignToExpStmCG assignInstance = new AAssignToExpStmCG();
		assignInstance.setTarget(instanceId);
		assignInstance.setExp(newQuote);
		
		AIfStmCG ensureInstance = new AIfStmCG();
		ensureInstance.setIfExp(nullCompare);
		ensureInstance.setThenStm(assignInstance);
		
		AReturnStmCG returnInstance = new AReturnStmCG();
		returnInstance.setExp(instanceVar.clone());
		
		ABlockStmCG body = new ABlockStmCG();
		body.getStatements().add(ensureInstance);
		body.getStatements().add(returnInstance);
		
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(quoteClassType.clone());
		
		AMethodDeclCG getInstanceMethod = new AMethodDeclCG();
		
		getInstanceMethod.setImplicit(false);
		getInstanceMethod.setAbstract(false);
		getInstanceMethod.setAccess(JavaFormat.JAVA_PUBLIC);
		getInstanceMethod.setIsConstructor(false);
		getInstanceMethod.setName(GET_INSTANCE_METHOD);
		getInstanceMethod.setStatic(true);
		
		getInstanceMethod.setMethodType(methodType);
		getInstanceMethod.setBody(body);
		
		return getInstanceMethod;
	}
	
	private AMethodDeclCG consHashcodeMethod()
	{
		AIdentifierVarExpCG hashCodeVar = transformationAssistant.consIdentifierVar(HASHCODE_FIELD, consFieldType());
		
		AReturnStmCG returnHashCode = new AReturnStmCG();
		returnHashCode.setExp(hashCodeVar);
		
		AMethodDeclCG hashCodeMethod = consHashcodeMethodSignature();
		
		ABlockStmCG body = new ABlockStmCG();
		body.getStatements().add(returnHashCode);
		
		hashCodeMethod.setBody(body);
		
		return hashCodeMethod;
	}
	private AMethodDeclCG consEqualsMethod(String name)
	{
		AIdentifierVarExpCG paramVar = transformationAssistant.consIdentifierVar(EQUALS_METHOD_PARAM, new AObjectTypeCG());
		
		AClassTypeCG quoteClass = new AClassTypeCG();
		quoteClass.setName(name);
		
		AInstanceofExpCG instanceCheck = new AInstanceofExpCG();
		instanceCheck.setType(new ABoolBasicTypeCG());
		instanceCheck.setExp(paramVar);
		instanceCheck.setCheckedType(quoteClass);
		
		AReturnStmCG checkReturned = new AReturnStmCG();
		checkReturned.setExp(instanceCheck);

		AMethodDeclCG equalsMethod = consEqualMethodSignature(EQUALS_METHOD_PARAM);
		
		ABlockStmCG body = new ABlockStmCG();
		body.getStatements().add(checkReturned);
		
		equalsMethod.setBody(body);
		
		return equalsMethod;
	}

	
	private AMethodDeclCG consToStringMethod(String name)
	{
		SExpCG stringLit = info.getExpAssistant().consStringLiteral("<" + name + ">", false);
		
		AReturnStmCG returnStr = new AReturnStmCG();
		returnStr.setExp(stringLit);

		AMethodDeclCG toStringMethod = consToStringSignature();
		
		ABlockStmCG body = new ABlockStmCG();
		body.getStatements().add(returnStr);
		
		toStringMethod.setBody(body);
		
		return toStringMethod;
	}
	
	private STypeCG consFieldType()
	{
		AExternalTypeCG fieldType = new AExternalTypeCG();
		fieldType.setName(JavaFormat.JAVA_INT);
		
		return fieldType;
	}
	
	private AExternalExpCG consZero()
	{
		AExternalExpCG zero = new AExternalExpCG();
		zero.setType(consFieldType());
		zero.setTargetLangExp("0");
		
		return zero;
	}
}
