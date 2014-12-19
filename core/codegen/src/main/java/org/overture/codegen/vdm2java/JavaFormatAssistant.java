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
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AAddrNotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AInstanceofExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;

public class JavaFormatAssistant
{

	public static ATypeNameCG consTypeName(ARecordDeclCG record)
			throws AnalysisException
	{
		AClassDeclCG classDef = record.getAncestor(AClassDeclCG.class);

		ATypeNameCG typeName = new ATypeNameCG();

		if (classDef == null)
		{
			throw new AnalysisException("A Record declaration must always be defined inside a class");
		} else
		{
			typeName.setDefiningClass(classDef.getName());
		}

		typeName.setName(record.getName());

		return typeName;
	}

	public static ABlockStmCG consVarFromCastedExp(ARecordDeclCG record,
			String formalParamName, String varName) throws AnalysisException
	{
		// Construct a local var in a statement: RecordType varName = ((RecordType) formalParamName);

		AVarDeclCG localVar = new AVarDeclCG();
		localVar.setFinal(false);

		ARecordTypeCG recordType = new ARecordTypeCG();
		recordType.setName(consTypeName(record));
		localVar.setType(recordType);

		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(varName);
		localVar.setPattern(idPattern);

		ACastUnaryExpCG cast = new ACastUnaryExpCG();
		cast.setType(recordType.clone());
		AIdentifierVarExpCG varExp = new AIdentifierVarExpCG();
		varExp.setOriginal(formalParamName);
		varExp.setType(new AObjectTypeCG());
		cast.setExp(varExp);
		localVar.setExp(cast);

		ABlockStmCG stm = new ABlockStmCG();
		stm.getLocalDefs().add(localVar);

		return stm;
	}

	public static AAndBoolBinaryExpCG extendAndExp(ARecordDeclCG record,
			AFieldDeclCG field, SExpCG previous, String paramName)
			throws AnalysisException
	{
		// By recursively calling this method an "and chain" of field
		// comparisons can be constructed: fieldComp1 && fieldComp2 && fieldComp3 ....

		AAndBoolBinaryExpCG nextAnd = new AAndBoolBinaryExpCG();
		nextAnd.setType(new ABoolBasicTypeCG());
		nextAnd.setLeft(previous);
		nextAnd.setRight(consFieldComparison(record, field, paramName));
		return nextAnd;
	}

	public static AInstanceofExpCG consInstanceOf(ARecordDeclCG record,
			String formalParamName)
	{
		// Example: objRef instanceof classType

		AClassDeclCG enclosingClass = record.getAncestor(AClassDeclCG.class);

		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setDefiningClass(enclosingClass.getName());
		typeName.setName(record.getName());

		ARecordTypeCG recordType = new ARecordTypeCG();
		recordType.setName(typeName);

		AIdentifierVarExpCG objRef = new AIdentifierVarExpCG();
		objRef.setOriginal(formalParamName);
		objRef.setType(new AObjectTypeCG());

		AInstanceofExpCG instanceOfExp = new AInstanceofExpCG();
		instanceOfExp.setType(new ABoolBasicTypeCG());
		instanceOfExp.setExp(objRef);
		instanceOfExp.setCheckedType(recordType);

		return instanceOfExp;
	}

	public static AAddrNotEqualsBinaryExpCG consParamNotNullComp(
			AIdentifierVarExpCG param)
	{
		AAddrNotEqualsBinaryExpCG fieldComparison = new AAddrNotEqualsBinaryExpCG();

		fieldComparison.setType(new ABoolBasicTypeCG());

		AIdentifierVarExpCG instanceField = new AIdentifierVarExpCG();
		instanceField.setType(param.getType().clone());
		instanceField.setOriginal(param.getOriginal());

		fieldComparison.setLeft(instanceField);
		fieldComparison.setRight(new ANullExpCG());

		return fieldComparison;
	}

	public static APlainCallStmCG consCallStm(AFieldDeclCG field)
	{
		APlainCallStmCG call = new APlainCallStmCG();

		AExternalTypeCG classType = new AExternalTypeCG();
		classType.setName(JavaFormat.UTILS_FILE);

		AIdentifierVarExpCG root = new AIdentifierVarExpCG();
		root.setType(classType);
		root.setOriginal(field.getName());

		AIdentifierVarExpCG argument = new AIdentifierVarExpCG();
		argument.setType(field.getType().clone());
		argument.setOriginal(field.getName());

		call.setType(classType.clone());
		call.setName("hashcode");
		call.setClassType(classType.clone());
		call.getArgs().add(argument);

		return call;
	}

	public static AEqualsBinaryExpCG consFieldComparison(ARecordDeclCG record,
			AFieldDeclCG field, String formalParamName)
			throws AnalysisException
	{
		// Example: fieldName == formalParamName.fieldName

		AEqualsBinaryExpCG fieldComparison = new AEqualsBinaryExpCG();
		fieldComparison.setType(new ABoolBasicTypeCG());

		AIdentifierVarExpCG instanceField = new AIdentifierVarExpCG();
		instanceField.setType(field.getType().clone());
		instanceField.setOriginal(field.getName());

		AFieldExpCG formalParamField = new AFieldExpCG();
		formalParamField.setType(field.getType().clone());

		AIdentifierVarExpCG formalParam = new AIdentifierVarExpCG();
		ARecordTypeCG recordType = new ARecordTypeCG();
		recordType.setName(consTypeName(record));
		formalParam.setType(recordType);
		formalParam.setOriginal(formalParamName);

		formalParamField.setObject(formalParam);
		formalParamField.setMemberName(field.getName());

		fieldComparison.setLeft(instanceField);
		fieldComparison.setRight(formalParamField);

		return fieldComparison;
	}

	public static AApplyExpCG consUtilCallUsingRecFields(ARecordDeclCG record,
			STypeCG returnType, String memberName)
	{
		LinkedList<AFieldDeclCG> fields = record.getFields();

		AApplyExpCG call = consUtilCall(returnType, memberName);
		LinkedList<SExpCG> args = call.getArgs();

		for (AFieldDeclCG field : fields)
		{
			AIdentifierVarExpCG nextArg = new AIdentifierVarExpCG();
			nextArg.setOriginal(field.getName());
			nextArg.setType(field.getType().clone());
			args.add(nextArg);
		}

		return call;
	}

	public static AApplyExpCG consUtilCall(STypeCG returnType, String memberName)
	{
		AExplicitVarExpCG member = new AExplicitVarExpCG();

		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(returnType.clone());
		member.setType(methodType);
		AExternalTypeCG classType = new AExternalTypeCG();
		classType.setName(JavaFormat.UTILS_FILE);
		member.setClassType(classType);
		member.setName(memberName);
		AApplyExpCG call = new AApplyExpCG();
		call.setType(returnType.clone());
		call.setRoot(member);
		
		return call;
	}
}
