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

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.expressions.AExternalExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.ASeqConcatBinaryExpCG;
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG;
import org.overture.codegen.cgast.expressions.ATernaryIfExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.logging.Logger;

public class JavaRecordCreator extends JavaClassCreatorBase
{
	private JavaFormat javaFormat;

	public JavaRecordCreator(JavaFormat javaFormat)
	{
		this.javaFormat = javaFormat;
	}
	
	public AMethodDeclCG genRecConstructor(ARecordDeclCG record)
			throws AnalysisException
	{
		// Since Java does not have records but the OO AST does a record is generated as a Java class.
		// To make sure that the record can be instantiated we must explicitly add a constructor.
		
		AMethodDeclCG constructor = consDefaultCtorSignature(record.getName());
		
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(consRecordType(record));

		ABlockStmCG body = new ABlockStmCG();
		constructor.setBody(body);

		LinkedList<AFormalParamLocalParamCG> formalParams = constructor.getFormalParams();
		LinkedList<SStmCG> bodyStms = body.getStatements();
		LinkedList<AFieldDeclCG> fields = record.getFields();
		
		for (AFieldDeclCG field : fields)
		{
			String name = field.getName();
			STypeCG type = field.getType();

			String paramName = "_" + name;

			AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
			idPattern.setName(paramName);

			methodType.getParams().add(type.clone());
			
			// Construct formal parameter of the constructor
			AFormalParamLocalParamCG formalParam = new AFormalParamLocalParamCG();
			formalParam.setPattern(idPattern);
			formalParam.setType(type.clone());
			formalParams.add(formalParam);

			// Construct the initialization of the record field using the
			// corresponding formal parameter.
			AAssignToExpStmCG assignment = new AAssignToExpStmCG();
			AIdentifierVarExpCG id = new AIdentifierVarExpCG();
			id.setName(name);
			id.setType(type.clone());
			id.setIsLambda(false);
			id.setIsLocal(true);

			AIdentifierVarExpCG varExp = new AIdentifierVarExpCG();
			varExp.setType(type.clone());
			varExp.setName(paramName);
			varExp.setIsLocal(true);

			assignment.setTarget(id);

			if (!javaFormat.getIrInfo().getAssistantManager().getTypeAssistant().isBasicType(varExp.getType()))
			{
				// Example: b = (_b != null) ? _b.clone() : null;
				ATernaryIfExpCG checkedAssignment = new ATernaryIfExpCG();
				checkedAssignment.setType(new ABoolBasicTypeCG());
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

	public AMethodDeclCG genCopyMethod(ARecordDeclCG record)
			throws AnalysisException
	{

		ADefaultClassDeclCG defClass = record.getAncestor(ADefaultClassDeclCG.class);
		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setDefiningClass(defClass.getName());
		typeName.setName(record.getName());

		ARecordTypeCG returnType = new ARecordTypeCG();
		returnType.setName(typeName);

		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(returnType);

		AMethodDeclCG method = consCopySignature(methodType);

		ANewExpCG newExp = new ANewExpCG();
		newExp.setType(returnType.clone());
		newExp.setName(typeName.clone());
		List<SExpCG> args = newExp.getArgs();

		List<AFieldDeclCG> fields = record.getFields();
		for (AFieldDeclCG field : fields)
		{
			String name = field.getName();

			AIdentifierVarExpCG varExp = new AIdentifierVarExpCG();
			varExp.setName(name);
			varExp.setType(field.getType().clone());
			varExp.setIsLocal(false);
			
			args.add(varExp);
		}

		AReturnStmCG body = new AReturnStmCG();
		body.setExp(newExp);
		method.setBody(body);

		return method;
	}

	public AMethodDeclCG genEqualsMethod(ARecordDeclCG record)
			throws AnalysisException
	{
		String paramName = "obj";
		
		// Construct equals method to be used for comparing records using
		// "structural" equivalence
		AMethodDeclCG equalsMethod = consEqualMethodSignature(paramName);

		ABlockStmCG equalsMethodBody = new ABlockStmCG();
		LinkedList<SStmCG> equalsStms = equalsMethodBody.getStatements();
		
		AReturnStmCG returnTypeComp = new AReturnStmCG();
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
			AIfStmCG ifStm = new AIfStmCG();
			ANotUnaryExpCG negated = new ANotUnaryExpCG();
			negated.setType(new ABoolBasicTypeCG());
			negated.setExp(javaFormat.getJavaFormatAssistant().consInstanceOf(record, paramName));
			ifStm.setIfExp(negated);

			returnTypeComp.setExp(javaFormat.getIrInfo().getAssistantManager().getExpAssistant().consBoolLiteral(false));
			ifStm.setThenStm(returnTypeComp);

			// If the inital check is passed we can safely cast the formal parameter
			// To the record type: RecordType other = ((RecordType) obj);
			String localVarName = "other";
			ABlockStmCG formalParamCasted = javaFormat.getJavaFormatAssistant().consVarFromCastedExp(record, paramName, localVarName);

			// Next compare the fields of the instance with the fields of the formal parameter "obj":
			// return (field1 == obj.field1) && (field2 == other.field2)...
			LinkedList<AFieldDeclCG> fields = record.getFields();
			SExpCG previousComparisons = javaFormat.getJavaFormatAssistant().consFieldComparison(record, fields.get(0), localVarName);

			for (int i = 1; i < fields.size(); i++)
			{
				previousComparisons = javaFormat.getJavaFormatAssistant().extendAndExp(record, fields.get(i), previousComparisons, localVarName);
			}

			AReturnStmCG fieldsComparison = new AReturnStmCG();
			fieldsComparison.setExp(previousComparisons);

			equalsStms.add(ifStm);
			equalsStms.add(formalParamCasted);
			equalsStms.add(fieldsComparison);
		}

		equalsMethod.setBody(equalsMethodBody);

		return equalsMethod;
	}

	public AMethodDeclCG genHashcodeMethod(ARecordDeclCG record)
			throws AnalysisException
	{
		AMethodDeclCG hashcodeMethod = consHashcodeMethodSignature();

		AReturnStmCG returnStm = new AReturnStmCG();

		if (record.getFields().isEmpty())
		{
			AExternalExpCG zero = new AExternalExpCG();
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

	public AMethodDeclCG genToStringMethod(ARecordDeclCG record)
			throws AnalysisException
	{
		AMethodDeclCG toStringMethod = consToStringSignature();

		AReturnStmCG returnStm = new AReturnStmCG();

		ADefaultClassDeclCG enclosingClass = record.getAncestor(ADefaultClassDeclCG.class);
		String className = "";
		
		if(enclosingClass != null)
		{
			className = enclosingClass.getName();
		}
		else
		{
			Logger.getLog().printErrorln("Could not find enclosing class for record: " + record.getName());
		}
		
		String recToStrPrefix = String.format("mk_%s%s", className + "`" , record.getName());
		
		AStringLiteralExpCG emptyRecStr = new AStringLiteralExpCG();
		emptyRecStr.setIsNull(false);
		STypeCG strType = toStringMethod.getMethodType().getResult();
		emptyRecStr.setType(strType.clone());
		
		if (record.getFields().isEmpty())
		{
			emptyRecStr.setValue(recToStrPrefix + "()");

			returnStm.setExp(emptyRecStr);
		} else
		{
			ASeqConcatBinaryExpCG stringBuffer = new ASeqConcatBinaryExpCG();
			stringBuffer.setType(strType.clone());
			stringBuffer.setLeft(javaFormat.getIrInfo().getExpAssistant().consStringLiteral(recToStrPrefix, false));
			stringBuffer.setRight(javaFormat.getJavaFormatAssistant().consUtilCallUsingRecFields(record, strType, "formatFields"));
			
			returnStm.setExp(stringBuffer);
		}

		toStringMethod.setBody(returnStm);

		return toStringMethod;
	}
	
	public ARecordTypeCG consRecordType(ARecordDeclCG record)
	{
		ADefaultClassDeclCG defClass = record.getAncestor(ADefaultClassDeclCG.class);
		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setDefiningClass(defClass.getName());
		typeName.setName(record.getName());

		ARecordTypeCG returnType = new ARecordTypeCG();

		returnType.setName(typeName);
		return returnType;
	}
}
