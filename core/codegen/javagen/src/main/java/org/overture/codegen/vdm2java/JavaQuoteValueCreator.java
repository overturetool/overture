package org.overture.codegen.vdm2java;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.AExternalExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AIsOfClassExpIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.expressions.ASuperVarExpIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.AObjectTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class JavaQuoteValueCreator extends JavaClassCreatorBase
{
	public static final String JAVA_QUOTE_NAME_SUFFIX = "Quote";

	private static final String GET_INSTANCE_METHOD = "getInstance";
	private static final String HASH_CODE_METHOD = "hashCode";

	private static final String INSTANCE_FIELD = "instance";
	private static final String HASHCODE_FIELD = "hc";
	private static final String EQUALS_METHOD_PARAM = "obj";

	private IRInfo info;
	private TransAssistantIR transAssistant;

	public JavaQuoteValueCreator(IRInfo info,
			TransAssistantIR transformationAssistant)
	{
		this.info = info;
		this.transAssistant = transformationAssistant;
	}

	public ADefaultClassDeclIR consQuoteValue(String quoteClassName,
			String quoteName, String userCodePackage)
	{
		quoteClassName = quoteClassName
				+ JavaQuoteValueCreator.JAVA_QUOTE_NAME_SUFFIX;
		ADefaultClassDeclIR decl = new ADefaultClassDeclIR();
		decl.setAbstract(false);
		decl.setAccess(IJavaConstants.PUBLIC);
		decl.setName(quoteClassName);
		decl.setStatic(false);

		// The package where the quotes are put is userCode.quotes
		String quotePackage = consQuotePackage(userCodePackage);

		decl.setPackage(quotePackage);

		decl.getFields().add(consHashcodeField());
		decl.getFields().add(consInstanceField(quoteClassName));

		decl.getMethods().add(consQuoteCtor(quoteClassName));
		decl.getMethods().add(consGetInstanceMethod(quoteClassName));
		decl.getMethods().add(consHashcodeMethod());
		decl.getMethods().add(consEqualsMethod(quoteClassName));
		decl.getMethods().add(consToStringMethod(quoteName));

		List<ClonableString> imports = new LinkedList<>();
		imports.add(new ClonableString(JavaCodeGen.RUNTIME_IMPORT));
		decl.setDependencies(imports);

		return decl;
	}

	public static String consQuotePackage(String userCodePackage)
	{
		if (JavaCodeGenUtil.isValidJavaPackage(userCodePackage))
		{
			return userCodePackage + "." + JavaCodeGen.JAVA_QUOTES_PACKAGE;
		} else
		{
			return JavaCodeGen.JAVA_QUOTES_PACKAGE;
		}
	}

	public static String fullyQualifiedQuoteName(String userCodePackage,
			String vdmValueName)
	{
		return consQuotePackage(userCodePackage) + "." + vdmValueName
				+ JAVA_QUOTE_NAME_SUFFIX;
	}

	private AFieldDeclIR consHashcodeField()
	{
		AExternalTypeIR fieldType = new AExternalTypeIR();
		fieldType.setName(IJavaConstants.INT);

		AFieldDeclIR field = new AFieldDeclIR();
		field.setAccess(IJavaConstants.PRIVATE);
		field.setVolatile(false);
		field.setFinal(false);
		field.setStatic(true);
		field.setName(HASHCODE_FIELD);
		field.setType(fieldType);
		field.setInitial(consZero());

		return field;
	}

	private AFieldDeclIR consInstanceField(String name)
	{
		AClassTypeIR quoteClassType = new AClassTypeIR();
		quoteClassType.setName(name);

		AFieldDeclIR field = new AFieldDeclIR();
		field.setAccess(IJavaConstants.PRIVATE);
		field.setVolatile(false);
		field.setFinal(false);
		field.setStatic(true);
		field.setName(INSTANCE_FIELD);
		field.setType(quoteClassType);
		field.setInitial(info.getExpAssistant().consNullExp());

		return field;
	}

	private AMethodDeclIR consQuoteCtor(String name)
	{
		AExternalTypeIR fieldType = new AExternalTypeIR();
		fieldType.setName(IJavaConstants.INT);

		AIdentifierVarExpIR hashcodeVar = transAssistant.getInfo().getExpAssistant().consIdVar(HASHCODE_FIELD, fieldType);

		AEqualsBinaryExpIR hashcodeCompare = new AEqualsBinaryExpIR();
		hashcodeCompare.setType(new ABoolBasicTypeIR());
		hashcodeCompare.setLeft(hashcodeVar);
		hashcodeCompare.setRight(consZero());

		AIdentifierVarExpIR hashCodeId = transAssistant.getInfo().getExpAssistant().consIdVar(HASHCODE_FIELD, consFieldType());

		AMethodTypeIR hashCodeMethodType = new AMethodTypeIR();
		hashCodeMethodType.setResult(consFieldType());

		ASuperVarExpIR superVar = new ASuperVarExpIR();
		superVar.setName(HASH_CODE_METHOD);
		superVar.setType(hashCodeMethodType);
		superVar.setIsLambda(false);
		superVar.setIsLocal(false);

		AApplyExpIR superCall = new AApplyExpIR();
		superCall.setType(consFieldType());
		superCall.setRoot(superVar);

		AAssignToExpStmIR assignHashcode = new AAssignToExpStmIR();
		assignHashcode.setTarget(hashCodeId);
		assignHashcode.setExp(superCall);

		AIfStmIR hashcodeCheck = new AIfStmIR();
		hashcodeCheck.setIfExp(hashcodeCompare);
		hashcodeCheck.setThenStm(assignHashcode);

		ABlockStmIR body = new ABlockStmIR();
		body.getStatements().add(hashcodeCheck);

		AClassTypeIR quoteClassType = new AClassTypeIR();
		quoteClassType.setName(name);

		AMethodTypeIR constructorType = new AMethodTypeIR();
		constructorType.setResult(quoteClassType);

		AMethodDeclIR ctor = consDefaultCtorSignature(name);
		ctor.setMethodType(constructorType);
		ctor.setBody(body);

		return ctor;
	}

	private AMethodDeclIR consGetInstanceMethod(String name)
	{
		AClassTypeIR quoteClassType = new AClassTypeIR();
		quoteClassType.setName(name);

		AIdentifierVarExpIR instanceVar = transAssistant.getInfo().getExpAssistant().consIdVar(INSTANCE_FIELD, quoteClassType);

		AEqualsBinaryExpIR nullCompare = new AEqualsBinaryExpIR();
		nullCompare.setType(new ABoolBasicTypeIR());
		nullCompare.setLeft(instanceVar);
		nullCompare.setRight(info.getExpAssistant().consNullExp());

		AIdentifierVarExpIR instanceId = transAssistant.getInfo().getExpAssistant().consIdVar(INSTANCE_FIELD, quoteClassType.clone());

		ATypeNameIR typeName = new ATypeNameIR();
		typeName.setDefiningClass(null);
		typeName.setName(name);

		ANewExpIR newQuote = new ANewExpIR();
		newQuote.setName(typeName);
		newQuote.setType(quoteClassType);

		AAssignToExpStmIR assignInstance = new AAssignToExpStmIR();
		assignInstance.setTarget(instanceId);
		assignInstance.setExp(newQuote);

		AIfStmIR ensureInstance = new AIfStmIR();
		ensureInstance.setIfExp(nullCompare);
		ensureInstance.setThenStm(assignInstance);

		AReturnStmIR returnInstance = new AReturnStmIR();
		returnInstance.setExp(instanceVar.clone());

		ABlockStmIR body = new ABlockStmIR();
		body.getStatements().add(ensureInstance);
		body.getStatements().add(returnInstance);

		AMethodTypeIR methodType = new AMethodTypeIR();
		methodType.setResult(quoteClassType.clone());

		AMethodDeclIR getInstanceMethod = new AMethodDeclIR();

		getInstanceMethod.setImplicit(false);
		getInstanceMethod.setAbstract(false);
		getInstanceMethod.setAccess(IJavaConstants.PUBLIC);
		getInstanceMethod.setIsConstructor(false);
		getInstanceMethod.setName(GET_INSTANCE_METHOD);
		getInstanceMethod.setStatic(true);

		getInstanceMethod.setMethodType(methodType);
		getInstanceMethod.setBody(body);

		return getInstanceMethod;
	}

	private AMethodDeclIR consHashcodeMethod()
	{
		AIdentifierVarExpIR hashCodeVar = transAssistant.getInfo().getExpAssistant().consIdVar(HASHCODE_FIELD, consFieldType());

		AReturnStmIR returnHashCode = new AReturnStmIR();
		returnHashCode.setExp(hashCodeVar);

		AMethodDeclIR hashCodeMethod = consHashcodeMethodSignature();

		ABlockStmIR body = new ABlockStmIR();
		body.getStatements().add(returnHashCode);

		hashCodeMethod.setBody(body);

		return hashCodeMethod;
	}

	private AMethodDeclIR consEqualsMethod(String name)
	{
		AIdentifierVarExpIR paramVar = transAssistant.getInfo().getExpAssistant().consIdVar(EQUALS_METHOD_PARAM, new AObjectTypeIR());

		AClassTypeIR quoteClass = new AClassTypeIR();
		quoteClass.setName(name);

		AIsOfClassExpIR instanceCheck = new AIsOfClassExpIR();
		instanceCheck.setType(new ABoolBasicTypeIR());
		instanceCheck.setExp(paramVar);
		instanceCheck.setCheckedType(quoteClass);

		AReturnStmIR checkReturned = new AReturnStmIR();
		checkReturned.setExp(instanceCheck);

		AMethodDeclIR equalsMethod = consEqualMethodSignature(EQUALS_METHOD_PARAM);

		ABlockStmIR body = new ABlockStmIR();
		body.getStatements().add(checkReturned);

		equalsMethod.setBody(body);

		return equalsMethod;
	}

	private AMethodDeclIR consToStringMethod(String name)
	{
		SExpIR stringLit = info.getExpAssistant().consStringLiteral("<" + name
				+ ">", false);

		AReturnStmIR returnStr = new AReturnStmIR();
		returnStr.setExp(stringLit);

		AMethodDeclIR toStringMethod = consToStringSignature();

		ABlockStmIR body = new ABlockStmIR();
		body.getStatements().add(returnStr);

		toStringMethod.setBody(body);

		return toStringMethod;
	}

	private STypeIR consFieldType()
	{
		AExternalTypeIR fieldType = new AExternalTypeIR();
		fieldType.setName(IJavaConstants.INT);

		return fieldType;
	}

	private AExternalExpIR consZero()
	{
		AExternalExpIR zero = new AExternalExpIR();
		zero.setType(consFieldType());
		zero.setTargetLangExp("0");

		return zero;
	}
}
