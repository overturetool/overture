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
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AAddrNotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AInstanceofExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.ir.IRInfo;

public class JavaFormatAssistant extends JavaClassCreatorBase
{
	private IRInfo info;

	public JavaFormatAssistant(IRInfo info)
	{
		this.info = info;
	}

	public ATypeNameCG consTypeName(ARecordDeclCG record)
			throws AnalysisException
	{
		ADefaultClassDeclCG classDef = record.getAncestor(ADefaultClassDeclCG.class);

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

	public ABlockStmCG consVarFromCastedExp(ARecordDeclCG record,
			String formalParamName, String varName) throws AnalysisException
	{
		// Construct a local var in a statement: RecordType varName = ((RecordType) formalParamName);
		ARecordTypeCG recordType = new ARecordTypeCG();
		recordType.setName(consTypeName(record));

		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(varName);

		ACastUnaryExpCG cast = new ACastUnaryExpCG();
		cast.setType(recordType.clone());
		
		AIdentifierVarExpCG varExp = new AIdentifierVarExpCG();
		varExp.setType(new AObjectTypeCG());
		varExp.setName(formalParamName);
		varExp.setIsLocal(true);
		
		cast.setExp(varExp);

		AVarDeclCG localVar = info.getDeclAssistant().consLocalVarDecl(recordType, idPattern, cast);
				
		ABlockStmCG stm = new ABlockStmCG();
		stm.getLocalDefs().add(localVar);

		return stm;
	}

	public AAndBoolBinaryExpCG extendAndExp(ARecordDeclCG record,
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

	public AInstanceofExpCG consInstanceOf(ARecordDeclCG record,
			String formalParamName)
	{
		// Example: objRef instanceof classType

		ADefaultClassDeclCG enclosingClass = record.getAncestor(ADefaultClassDeclCG.class);

		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setDefiningClass(enclosingClass.getName());
		typeName.setName(record.getName());

		ARecordTypeCG recordType = new ARecordTypeCG();
		recordType.setName(typeName);

		AIdentifierVarExpCG objRef = new AIdentifierVarExpCG();
		objRef.setType(new AObjectTypeCG());
		objRef.setIsLocal(true);
		objRef.setName(formalParamName);
		
		AInstanceofExpCG instanceOfExp = new AInstanceofExpCG();
		instanceOfExp.setType(new ABoolBasicTypeCG());
		instanceOfExp.setExp(objRef);
		instanceOfExp.setCheckedType(recordType);

		return instanceOfExp;
	}

	public AAddrNotEqualsBinaryExpCG consParamNotNullComp(
			AIdentifierVarExpCG param)
	{
		AAddrNotEqualsBinaryExpCG fieldComparison = new AAddrNotEqualsBinaryExpCG();

		fieldComparison.setType(new ABoolBasicTypeCG());

		AIdentifierVarExpCG instanceField = new AIdentifierVarExpCG();
		instanceField.setType(param.getType().clone());
		instanceField.setIsLocal(false);
		instanceField.setName(param.getName());

		fieldComparison.setLeft(instanceField);
		fieldComparison.setRight(info.getExpAssistant().consNullExp());

		return fieldComparison;
	}

	public APlainCallStmCG consCallStm(AFieldDeclCG field)
	{
		APlainCallStmCG call = new APlainCallStmCG();

		AExternalTypeCG classType = new AExternalTypeCG();
		classType.setName(JavaFormat.UTILS_FILE);

		AIdentifierVarExpCG argument = new AIdentifierVarExpCG();
		argument.setType(field.getType().clone());
		argument.setIsLocal(false);
		argument.setName(field.getName());

		call.setType(classType.clone());
		call.setName("hashcode");
		call.setClassType(classType.clone());
		call.getArgs().add(argument);

		return call;
	}

	public AEqualsBinaryExpCG consFieldComparison(ARecordDeclCG record,
			AFieldDeclCG field, String formalParamName)
			throws AnalysisException
	{
		// Example: fieldName == formalParamName.fieldName

		AEqualsBinaryExpCG fieldComparison = new AEqualsBinaryExpCG();
		fieldComparison.setType(new ABoolBasicTypeCG());

		AIdentifierVarExpCG instanceField = new AIdentifierVarExpCG();
		instanceField.setType(field.getType().clone());
		instanceField.setIsLocal(false);
		instanceField.setName(field.getName());

		AFieldExpCG formalParamField = new AFieldExpCG();
		formalParamField.setType(field.getType().clone());

		AIdentifierVarExpCG formalParam = new AIdentifierVarExpCG();
		ARecordTypeCG recordType = new ARecordTypeCG();
		recordType.setName(consTypeName(record));
		formalParam.setType(recordType);
		formalParam.setIsLocal(true);
		formalParam.setName(formalParamName);

		formalParamField.setObject(formalParam);
		formalParamField.setMemberName(field.getName());

		fieldComparison.setLeft(instanceField);
		fieldComparison.setRight(formalParamField);

		return fieldComparison;
	}

	public AApplyExpCG consUtilCallUsingRecFields(ARecordDeclCG record,
			STypeCG returnType, String memberName)
	{
		LinkedList<AFieldDeclCG> fields = record.getFields();

		AApplyExpCG call = consUtilCall(returnType, memberName);
		LinkedList<SExpCG> args = call.getArgs();

		for (AFieldDeclCG field : fields)
		{
			AIdentifierVarExpCG nextArg = new AIdentifierVarExpCG();
			nextArg.setName(field.getName());
			nextArg.setType(field.getType().clone());
			nextArg.setIsLocal(false);
			args.add(nextArg);
		}

		return call;
	}
}
