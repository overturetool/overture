/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.vdm2java;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.expressions.AExternalExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.expressions.ANotUnaryExpIR;
import org.overture.codegen.ir.expressions.ASeqConcatBinaryExpIR;
import org.overture.codegen.ir.expressions.AStringLiteralExpIR;
import org.overture.codegen.ir.expressions.ATernaryIfExpIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;

public class JavaRecordCreator extends JavaClassCreatorBase
{
	private JavaFormat javaFormat;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public JavaRecordCreator(JavaFormat javaFormat)
	{
		this.javaFormat = javaFormat;
	}

	public AMethodDeclIR genRecConstructor(ARecordDeclIR record)
			throws AnalysisException
	{
		// Since Java does not have records but the OO AST does a record is generated as a Java class.
		// To make sure that the record can be instantiated we must explicitly add a constructor.

		AMethodDeclIR constructor = consDefaultCtorSignature(record.getName());

		AMethodTypeIR methodType = new AMethodTypeIR();
		methodType.setResult(consRecordType(record));

		ABlockStmIR body = new ABlockStmIR();
		constructor.setBody(body);

		LinkedList<AFormalParamLocalParamIR> formalParams = constructor.getFormalParams();
		LinkedList<SStmIR> bodyStms = body.getStatements();
		LinkedList<AFieldDeclIR> fields = record.getFields();

		for (AFieldDeclIR field : fields)
		{
			String name = field.getName();
			STypeIR type = field.getType();

			String paramName = "_" + name;

			AIdentifierPatternIR idPattern = new AIdentifierPatternIR();
			idPattern.setName(paramName);

			methodType.getParams().add(type.clone());

			// Construct formal parameter of the constructor
			AFormalParamLocalParamIR formalParam = new AFormalParamLocalParamIR();
			formalParam.setPattern(idPattern);
			formalParam.setType(type.clone());
			formalParams.add(formalParam);

			// Construct the initialization of the record field using the
			// corresponding formal parameter.
			AAssignToExpStmIR assignment = new AAssignToExpStmIR();
			AIdentifierVarExpIR id = new AIdentifierVarExpIR();
			id.setName(name);
			id.setType(type.clone());
			id.setIsLambda(false);
			id.setIsLocal(true);

			AIdentifierVarExpIR varExp = new AIdentifierVarExpIR();
			varExp.setType(type.clone());
			varExp.setName(paramName);
			varExp.setIsLocal(true);

			assignment.setTarget(id);

			if (!javaFormat.getIrInfo().getAssistantManager().getTypeAssistant().isBasicType(varExp.getType()))
			{
				// Example: b = (_b != null) ? _b.clone() : null;
				ATernaryIfExpIR checkedAssignment = new ATernaryIfExpIR();
				checkedAssignment.setType(new ABoolBasicTypeIR());
				checkedAssignment.setCondition(javaFormat.getJavaFormatAssistant().consParamNotNullComp(varExp));
				checkedAssignment.setTrueValue(varExp);
				checkedAssignment.setFalseValue(javaFormat.getIrInfo().getExpAssistant().consNullExp());
				assignment.setExp(checkedAssignment);
			} else
			{
				assignment.setExp(varExp);
			}

			bodyStms.add(assignment);
		}

		constructor.setMethodType(methodType);

		return constructor;
	}

	public AMethodDeclIR genCopyMethod(ARecordDeclIR record)
			throws AnalysisException
	{

		ADefaultClassDeclIR defClass = record.getAncestor(ADefaultClassDeclIR.class);
		ATypeNameIR typeName = new ATypeNameIR();
		typeName.setDefiningClass(defClass.getName());
		typeName.setName(record.getName());

		ARecordTypeIR returnType = new ARecordTypeIR();
		returnType.setName(typeName);

		AMethodTypeIR methodType = new AMethodTypeIR();
		methodType.setResult(returnType);

		AMethodDeclIR method = consCopySignature(methodType);

		ANewExpIR newExp = new ANewExpIR();
		newExp.setType(returnType.clone());
		newExp.setName(typeName.clone());
		List<SExpIR> args = newExp.getArgs();

		List<AFieldDeclIR> fields = record.getFields();
		for (AFieldDeclIR field : fields)
		{
			String name = field.getName();

			AIdentifierVarExpIR varExp = new AIdentifierVarExpIR();
			varExp.setName(name);
			varExp.setType(field.getType().clone());
			varExp.setIsLocal(false);

			args.add(varExp);
		}

		AReturnStmIR body = new AReturnStmIR();
		body.setExp(newExp);
		method.setBody(body);

		return method;
	}

	public AMethodDeclIR genEqualsMethod(ARecordDeclIR record)
			throws AnalysisException
	{
		String paramName = "obj";

		// Construct equals method to be used for comparing records using
		// "structural" equivalence
		AMethodDeclIR equalsMethod = consEqualMethodSignature(paramName);

		ABlockStmIR equalsMethodBody = new ABlockStmIR();
		LinkedList<SStmIR> equalsStms = equalsMethodBody.getStatements();

		AReturnStmIR returnTypeComp = new AReturnStmIR();
		if (record.getFields().isEmpty())
		{
			// If the record has no fields equality is simply:
			// return obj instanceof RecordType
			returnTypeComp.setExp(javaFormat.getJavaFormatAssistant().consInstanceOf(record, paramName));
			equalsStms.add(returnTypeComp);

		} else
		{

			// Construct the initial check:
			// if ((!obj instanceof RecordType))
			// return false;
			AIfStmIR ifStm = new AIfStmIR();
			ANotUnaryExpIR negated = new ANotUnaryExpIR();
			negated.setType(new ABoolBasicTypeIR());
			negated.setExp(javaFormat.getJavaFormatAssistant().consInstanceOf(record, paramName));
			ifStm.setIfExp(negated);

			returnTypeComp.setExp(javaFormat.getIrInfo().getAssistantManager().getExpAssistant().consBoolLiteral(false));
			ifStm.setThenStm(returnTypeComp);

			// If the inital check is passed we can safely cast the formal parameter
			// To the record type: RecordType other = ((RecordType) obj);
			String localVarName = "other";
			ABlockStmIR formalParamCasted = javaFormat.getJavaFormatAssistant().consVarFromCastedExp(record, paramName, localVarName);

			// Next compare the fields of the instance with the fields of the formal parameter "obj":
			// return (field1 == obj.field1) && (field2 == other.field2)...
			LinkedList<AFieldDeclIR> fields = record.getFields();
			SExpIR previousComparisons = javaFormat.getJavaFormatAssistant().consFieldComparison(record, fields.get(0), localVarName);

			for (int i = 1; i < fields.size(); i++)
			{
				previousComparisons = javaFormat.getJavaFormatAssistant().extendAndExp(record, fields.get(i), previousComparisons, localVarName);
			}

			AReturnStmIR fieldsComparison = new AReturnStmIR();
			fieldsComparison.setExp(previousComparisons);

			equalsStms.add(ifStm);
			equalsStms.add(formalParamCasted);
			equalsStms.add(fieldsComparison);
		}

		equalsMethod.setBody(equalsMethodBody);

		return equalsMethod;
	}

	public AMethodDeclIR genHashcodeMethod(ARecordDeclIR record)
			throws AnalysisException
	{
		AMethodDeclIR hashcodeMethod = consHashcodeMethodSignature();

		AReturnStmIR returnStm = new AReturnStmIR();

		if (record.getFields().isEmpty())
		{
			AExternalExpIR zero = new AExternalExpIR();
			zero.setType(hashcodeMethod.getMethodType().getResult().clone());
			zero.setTargetLangExp("0");
			returnStm.setExp(zero);
		} else
		{
			returnStm.setExp(javaFormat.getJavaFormatAssistant().consUtilCallUsingRecFields(record, hashcodeMethod.getMethodType().getResult(), hashcodeMethod.getName()));
		}

		hashcodeMethod.setBody(returnStm);

		return hashcodeMethod;
	}

	public AMethodDeclIR genToStringMethod(ARecordDeclIR record)
			throws AnalysisException
	{
		AMethodDeclIR toStringMethod = consToStringSignature();

		AReturnStmIR returnStm = new AReturnStmIR();

		ADefaultClassDeclIR enclosingClass = record.getAncestor(ADefaultClassDeclIR.class);
		String className = "";

		if (enclosingClass != null)
		{
			className = enclosingClass.getName();
		} else
		{
			log.error("Could not find enclosing class for record: "
					+ record.getName());
		}

		String recToStrPrefix = String.format("mk_%s%s", className
				+ "`", record.getName());

		AStringLiteralExpIR emptyRecStr = new AStringLiteralExpIR();
		emptyRecStr.setIsNull(false);
		STypeIR strType = toStringMethod.getMethodType().getResult();
		emptyRecStr.setType(strType.clone());

		if (record.getFields().isEmpty())
		{
			emptyRecStr.setValue(recToStrPrefix + "()");

			returnStm.setExp(emptyRecStr);
		} else
		{
			ASeqConcatBinaryExpIR stringBuffer = new ASeqConcatBinaryExpIR();
			stringBuffer.setType(strType.clone());
			stringBuffer.setLeft(javaFormat.getIrInfo().getExpAssistant().consStringLiteral(recToStrPrefix, false));
			stringBuffer.setRight(javaFormat.getJavaFormatAssistant().consUtilCallUsingRecFields(record, strType, "formatFields"));

			returnStm.setExp(stringBuffer);
		}

		toStringMethod.setBody(returnStm);

		return toStringMethod;
	}

	public ARecordTypeIR consRecordType(ARecordDeclIR record)
	{
		ADefaultClassDeclIR defClass = record.getAncestor(ADefaultClassDeclIR.class);
		ATypeNameIR typeName = new ATypeNameIR();
		typeName.setDefiningClass(defClass.getName());
		typeName.setName(record.getName());

		ARecordTypeIR returnType = new ARecordTypeIR();

		returnType.setName(typeName);
		return returnType;
	}
}
