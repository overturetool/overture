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

import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AAddrNotEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.AAndBoolBinaryExpIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.ACastUnaryExpIR;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AIsOfClassExpIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AObjectTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;

public class JavaFormatAssistant extends JavaClassCreatorBase
{
	private IRInfo info;

	public JavaFormatAssistant(IRInfo info)
	{
		this.info = info;
	}

	public ATypeNameIR consTypeName(ARecordDeclIR record)
			throws AnalysisException
	{
		ADefaultClassDeclIR classDef = record.getAncestor(ADefaultClassDeclIR.class);

		ATypeNameIR typeName = new ATypeNameIR();

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

	public ABlockStmIR consVarFromCastedExp(ARecordDeclIR record,
			String formalParamName, String varName) throws AnalysisException
	{
		// Construct a local var in a statement: RecordType varName = ((RecordType) formalParamName);
		ARecordTypeIR recordType = new ARecordTypeIR();
		recordType.setName(consTypeName(record));

		AIdentifierPatternIR idPattern = new AIdentifierPatternIR();
		idPattern.setName(varName);

		ACastUnaryExpIR cast = new ACastUnaryExpIR();
		cast.setType(recordType.clone());

		AIdentifierVarExpIR varExp = new AIdentifierVarExpIR();
		varExp.setType(new AObjectTypeIR());
		varExp.setName(formalParamName);
		varExp.setIsLocal(true);

		cast.setExp(varExp);

		AVarDeclIR localVar = info.getDeclAssistant().consLocalVarDecl(recordType, idPattern, cast);

		ABlockStmIR stm = new ABlockStmIR();
		stm.getLocalDefs().add(localVar);

		return stm;
	}

	public AAndBoolBinaryExpIR extendAndExp(ARecordDeclIR record,
			AFieldDeclIR field, SExpIR previous, String paramName)
			throws AnalysisException
	{
		// By recursively calling this method an "and chain" of field
		// comparisons can be constructed: fieldComp1 && fieldComp2 && fieldComp3 ....

		AAndBoolBinaryExpIR nextAnd = new AAndBoolBinaryExpIR();
		nextAnd.setType(new ABoolBasicTypeIR());
		nextAnd.setLeft(previous);
		nextAnd.setRight(consFieldComparison(record, field, paramName));
		return nextAnd;
	}

	public AIsOfClassExpIR consInstanceOf(ARecordDeclIR record,
                                          String formalParamName)
	{
		// Example: objRef instanceof classType

		ADefaultClassDeclIR enclosingClass = record.getAncestor(ADefaultClassDeclIR.class);

		ATypeNameIR typeName = new ATypeNameIR();
		typeName.setDefiningClass(enclosingClass.getName());
		typeName.setName(record.getName());

		ARecordTypeIR recordType = new ARecordTypeIR();
		recordType.setName(typeName);

		AIdentifierVarExpIR objRef = new AIdentifierVarExpIR();
		objRef.setType(new AObjectTypeIR());
		objRef.setIsLocal(true);
		objRef.setName(formalParamName);

		AIsOfClassExpIR instanceOfExp = new AIsOfClassExpIR();
		instanceOfExp.setType(new ABoolBasicTypeIR());
		instanceOfExp.setExp(objRef);
		instanceOfExp.setCheckedType(recordType);

		return instanceOfExp;
	}

	public AAddrNotEqualsBinaryExpIR consParamNotNullComp(
			AIdentifierVarExpIR param)
	{
		AAddrNotEqualsBinaryExpIR fieldComparison = new AAddrNotEqualsBinaryExpIR();

		fieldComparison.setType(new ABoolBasicTypeIR());

		AIdentifierVarExpIR instanceField = new AIdentifierVarExpIR();
		instanceField.setType(param.getType().clone());
		instanceField.setIsLocal(true);
		instanceField.setName(param.getName());

		fieldComparison.setLeft(instanceField);
		fieldComparison.setRight(info.getExpAssistant().consNullExp());

		return fieldComparison;
	}

	public APlainCallStmIR consCallStm(AFieldDeclIR field)
	{
		APlainCallStmIR call = new APlainCallStmIR();

		AExternalTypeIR classType = new AExternalTypeIR();
		classType.setName(JavaFormat.UTILS_FILE);

		AIdentifierVarExpIR argument = new AIdentifierVarExpIR();
		argument.setType(field.getType().clone());
		argument.setIsLocal(false);
		argument.setName(field.getName());

		call.setType(classType.clone());
		call.setName("hashcode");
		call.setClassType(classType.clone());
		call.getArgs().add(argument);

		return call;
	}

	public AEqualsBinaryExpIR consFieldComparison(ARecordDeclIR record,
			AFieldDeclIR field, String formalParamName) throws AnalysisException
	{
		// Example: fieldName == formalParamName.fieldName

		AEqualsBinaryExpIR fieldComparison = new AEqualsBinaryExpIR();
		fieldComparison.setType(new ABoolBasicTypeIR());

		AIdentifierVarExpIR instanceField = new AIdentifierVarExpIR();
		instanceField.setType(field.getType().clone());
		instanceField.setIsLocal(false);
		instanceField.setName(field.getName());

		AFieldExpIR formalParamField = new AFieldExpIR();
		formalParamField.setType(field.getType().clone());

		AIdentifierVarExpIR formalParam = new AIdentifierVarExpIR();
		ARecordTypeIR recordType = new ARecordTypeIR();
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

	public AApplyExpIR consUtilCallUsingRecFields(ARecordDeclIR record,
			STypeIR returnType, String memberName)
	{
		LinkedList<AFieldDeclIR> fields = record.getFields();

		AApplyExpIR call = consUtilCall(returnType, memberName);
		LinkedList<SExpIR> args = call.getArgs();

		for (AFieldDeclIR field : fields)
		{
			AIdentifierVarExpIR nextArg = new AIdentifierVarExpIR();
			nextArg.setName(field.getName());
			nextArg.setType(field.getType().clone());
			nextArg.setIsLocal(false);
			args.add(nextArg);
		}

		return call;
	}
}
