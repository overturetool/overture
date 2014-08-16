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

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.expressions.AExternalExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG;
import org.overture.codegen.cgast.expressions.ATernaryIfExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;

public class JavaRecordCreator
{
	private JavaFormat javaFormat;
	
	public JavaRecordCreator(JavaFormat javaFormat)
	{
		this.javaFormat = javaFormat;
	}
	
	public String formatRecordConstructor(ARecordDeclCG record) throws AnalysisException
	{
		LinkedList<AFieldDeclCG> fields = record.getFields();
		
		AMethodDeclCG constructor = new AMethodDeclCG();
		//Since Java does not have records but the OO AST does a record is generated as a Java class.
		//To make sure that the record can be instantiated we must explicitly add a constructor.
		constructor.setAccess(JavaFormat.JAVA_PUBLIC);
		constructor.setIsConstructor(true);
		constructor.setName(record.getName());
		LinkedList<AFormalParamLocalParamCG> formalParams = constructor.getFormalParams();
	
		ABlockStmCG body = new ABlockStmCG();
		LinkedList<SStmCG> bodyStms = body.getStatements();
		constructor.setBody(body); 
		
		for (AFieldDeclCG field : fields)
		{
			String name = field.getName();
			STypeCG type = field.getType().clone();
			
			String paramName = "_" + name;
			
			AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
			idPattern.setName(paramName);

			//Construct formal parameter of the constructor
			AFormalParamLocalParamCG formalParam = new AFormalParamLocalParamCG();
			formalParam.setPattern(idPattern);
			formalParam.setType(type);
			formalParams.add(formalParam);
			
			//Construct the initialization of the record field using the
			//corresponding formal parameter.
			AAssignmentStmCG assignment = new AAssignmentStmCG();
			AIdentifierStateDesignatorCG id = new AIdentifierStateDesignatorCG();
			id.setName(name);
			
			AIdentifierVarExpCG varExp = new AIdentifierVarExpCG();
			varExp.setType(field.getType().clone());
			varExp.setOriginal(paramName);

			assignment.setTarget(id);
			
			if (!javaFormat.getIrInfo().getAssistantManager().getTypeAssistant().isBasicType(varExp.getType()))
			{
				//Example: b = (_b != null) ? _b.clone() : null;
				ATernaryIfExpCG checkedAssignment = new ATernaryIfExpCG();
				checkedAssignment.setType(new ABoolBasicTypeCG());
				checkedAssignment.setCondition(JavaFormatAssistant.consParamNotNullComp(varExp));
				checkedAssignment.setTrueValue(varExp);
				checkedAssignment.setFalseValue(new ANullExpCG());
				assignment.setExp(checkedAssignment);
			}
			else
			{
				assignment.setExp(varExp);
			}
			
			bodyStms.add(assignment);
		}
		
		record.getMethods().add(constructor);
		
		return javaFormat.format(constructor);
	}
	
	public String generateCloneMethod(ARecordDeclCG record) throws AnalysisException
	{
		AMethodDeclCG method = new AMethodDeclCG();

		method.setAccess(JavaFormat.JAVA_PUBLIC);
		method.setName("clone");
		
		AClassDeclCG defClass = record.getAncestor(AClassDeclCG.class);
		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setDefiningClass(defClass.getName());
		typeName.setName(record.getName());
		
		ARecordTypeCG returnType = new ARecordTypeCG();
		returnType.setName(typeName);
		
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(returnType);
		
		method.setMethodType(methodType);
		
		ANewExpCG newExp = new ANewExpCG();
		newExp.setType(returnType.clone());
		newExp.setName(typeName.clone());
		LinkedList<SExpCG> args = newExp.getArgs();
		
		LinkedList<AFieldDeclCG> fields = record.getFields();
		for (AFieldDeclCG field : fields)
		{
			String name = field.getName();
			
			AIdentifierVarExpCG varExp = new AIdentifierVarExpCG();
			varExp.setOriginal(name);
			varExp.setType(field.getType().clone());
			args.add(varExp);
		}
		
		AReturnStmCG body = new AReturnStmCG();
		body.setExp(newExp);
		method.setBody(body);

		record.getMethods().add(method);
		
		return javaFormat.format(method);
	}
	
	public String generateEqualsMethod(ARecordDeclCG record) throws AnalysisException
	{
		//Construct equals method to be used for comparing records using
		//"structural" equivalence
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
		
		//Add the formal parameter "Object obj" to the method
		AFormalParamLocalParamCG formalParam = new AFormalParamLocalParamCG();
		
		String paramName = "obj";
		
		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(paramName);
		
		formalParam.setPattern(idPattern);
		AObjectTypeCG paramType = new AObjectTypeCG();
		formalParam.setType(paramType);
		equalsMethod.getFormalParams().add(formalParam);
		
		ABlockStmCG equalsMethodBody = new ABlockStmCG();
		LinkedList<SStmCG> equalsStms = equalsMethodBody.getStatements();
		
		AReturnStmCG returnTypeComp = new AReturnStmCG();
		if (record.getFields().isEmpty())
		{
			//If the record has no fields equality is simply:
			//return obj instanceof RecordType
			returnTypeComp.setExp(JavaFormatAssistant.consInstanceOf(record, paramName));
			equalsStms.add(returnTypeComp);
			
		} else
		{

			// Construct the initial check:
			// if ((!obj instanceof RecordType))
			// return false;
			AIfStmCG ifStm = new AIfStmCG();
			ANotUnaryExpCG negated = new ANotUnaryExpCG();
			negated.setType(new ABoolBasicTypeCG());
			negated.setExp(JavaFormatAssistant.consInstanceOf(record, paramName));
			ifStm.setIfExp(negated);
			

			returnTypeComp.setExp(javaFormat.getIrInfo().getAssistantManager().getExpAssistant().consBoolLiteral(false));
			ifStm.setThenStm(returnTypeComp);

			// If the inital check is passed we can safely cast the formal parameter
			// To the record type: RecordType other = ((RecordType) obj);
			String localVarName = "other";
			ABlockStmCG formalParamCasted = JavaFormatAssistant.consVarFromCastedExp(record, paramName, localVarName);

			// Next compare the fields of the instance with the fields of the formal parameter "obj":
			// return (field1 == obj.field1) && (field2 == other.field2)...
			LinkedList<AFieldDeclCG> fields = record.getFields();
			SExpCG previousComparisons = JavaFormatAssistant.consFieldComparison(record, fields.get(0), localVarName);

			for (int i = 1; i < fields.size(); i++)
			{
				previousComparisons = JavaFormatAssistant.extendAndExp(record, fields.get(i), previousComparisons, localVarName);
			}

			AReturnStmCG fieldsComparison = new AReturnStmCG();
			fieldsComparison.setExp(previousComparisons);
			
			equalsStms.add(ifStm);
			equalsStms.add(formalParamCasted);
			equalsStms.add(fieldsComparison);
		}

		equalsMethod.setBody(equalsMethodBody);
		
		record.getMethods().add(equalsMethod);
		
		return javaFormat.format(equalsMethod);
	}
	
	public String generateHashcodeMethod(ARecordDeclCG record) throws AnalysisException
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
		
		AReturnStmCG returnStm = new AReturnStmCG();
		
		if(record.getFields().isEmpty())
		{
			AExternalExpCG zero = new AExternalExpCG();
			zero.setType(intBasicType.clone());
			zero.setTargetLangExp("0");
			returnStm.setExp(zero);
		}
		else
		{
			returnStm.setExp(JavaFormatAssistant.consUtilCallUsingRecFields(record, intBasicType, hashCode));
		}
		
		hashcodeMethod.setBody(returnStm);

		record.getMethods().add(hashcodeMethod);
		
		return javaFormat.format(hashcodeMethod);
	}
	
	public String generateToStringMethod(ARecordDeclCG record) throws AnalysisException
	{
		AMethodDeclCG toStringMethod = new AMethodDeclCG();
		
		toStringMethod.setAccess(JavaFormat.JAVA_PUBLIC);
		toStringMethod.setName("toString");

		AStringTypeCG returnType = new AStringTypeCG();
		
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(returnType);
		
		toStringMethod.setMethodType(methodType);
		
		AReturnStmCG returnStm = new AReturnStmCG();
		
		if (record.getFields().isEmpty())
		{
			AStringLiteralExpCG emptyRecStr = new AStringLiteralExpCG();
			emptyRecStr.setIsNull(false);
			emptyRecStr.setType(returnType.clone());
			emptyRecStr.setValue(String.format("mk_%s()", record.getName()));
			
			returnStm.setExp(emptyRecStr);
		} else
		{
			returnStm.setExp(JavaFormatAssistant.consRecToStringCall(record, returnType, "recordToString"));
		}
		
		toStringMethod.setBody(returnStm);
		
		record.getMethods().add(toStringMethod);
		
		return javaFormat.format(toStringMethod);
	}
}
