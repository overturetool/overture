package org.overture.codegen.vdm2java;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.expressions.AAddrNotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AInstanceofExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.AVariableExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;

public class JavaFormatAssistant
{

	public static ATypeNameCG consTypeName(ARecordDeclCG record) throws AnalysisException
	{
		AClassDeclCG classDef = record.getAncestor(AClassDeclCG.class);
		
		ATypeNameCG typeName = new ATypeNameCG();
		
		if(classDef == null)
			throw new AnalysisException("A Record declaration must always be defined inside a class");
		else
			typeName.setDefiningClass(classDef.getName());
		
		typeName.setName(record.getName());
		
		return typeName;
	}
	
	public static ABlockStmCG consVarFromCastedExp(ARecordDeclCG record, String formalParamName, String varName) throws AnalysisException
	{
		//Construct a local var in a statement:  RecordType varName = ((RecordType) formalParamName);
		
		ALocalVarDeclCG localVar = new ALocalVarDeclCG();
		
		ARecordTypeCG recordType = new ARecordTypeCG();
		recordType.setName(consTypeName(record));
		localVar.setType(recordType);
		
		localVar.setName(varName);
		
		ACastUnaryExpCG cast = new ACastUnaryExpCG();
		cast.setType(recordType.clone());
		AVariableExpCG varExp = new AVariableExpCG();
		varExp.setOriginal(formalParamName);
		varExp.setType(new AObjectTypeCG());
		cast.setExp(varExp);
		localVar.setExp(cast);

		ABlockStmCG stm = new ABlockStmCG();
		stm.getLocalDefs().add(localVar);
		
		return stm;
	}
	
	public static AAndBoolBinaryExpCG extendAndExp(ARecordDeclCG record, AFieldDeclCG field, PExpCG previous, String paramName) throws AnalysisException
	{
		//By recursively calling this method an "and chain" of field 
		//comparisons can be constructed: fieldComp1 && fieldComp2 && fieldComp3 ....
		
		AAndBoolBinaryExpCG nextAnd = new AAndBoolBinaryExpCG();
		nextAnd.setType(new ABoolBasicTypeCG());
		nextAnd.setLeft(previous);
		nextAnd.setRight(consFieldComparison(record, field, paramName));
		return nextAnd;
	}
	
	public static AInstanceofExpCG consInstanceOf(ARecordDeclCG record, String formalParamName)
	{
		//Example: objRef instanceof classType
		
		AInstanceofExpCG instanceOfExp = new AInstanceofExpCG();

		instanceOfExp.setType(new ABoolBasicTypeCG());

		AVariableExpCG objRef = new AVariableExpCG();
		objRef.setOriginal(formalParamName);
		objRef.setType(new AObjectTypeCG());
		instanceOfExp.setObjRef(objRef);
		
		AClassTypeCG classType = new AClassTypeCG();
		classType.setName(record.getName());
		instanceOfExp.setClassType(classType);
		
		return instanceOfExp;
	}
	
	public static AAddrNotEqualsBinaryExpCG consParamNotNullComp(AVariableExpCG param)
	{
		AAddrNotEqualsBinaryExpCG fieldComparison = new AAddrNotEqualsBinaryExpCG();
		
		fieldComparison.setType(new ABoolBasicTypeCG());
		
		AVariableExpCG instanceField = new AVariableExpCG();
		instanceField.setType(param.getType().clone());
		instanceField.setOriginal(param.getOriginal());
		
		fieldComparison.setLeft(instanceField);
		fieldComparison.setRight(new ANullExpCG());
		
		return fieldComparison;
	}
	
	public static ACallStmCG consCallStm(AFieldDeclCG field)
	{
		ACallStmCG call = new ACallStmCG();
		
		AClassTypeCG classType = new AClassTypeCG();
		classType.setName(IJavaCodeGenConstants.UTILS_FILE);
		
		AVariableExpCG root = new AVariableExpCG();
		root.setType(classType);
		root.setOriginal(field.getName());
		
		AVariableExpCG argument = new AVariableExpCG();
		//argument.setType(field.getType().clone());
		argument.setOriginal(field.getName());

		call.setType(classType.clone());
		call.setName("hashcode");
		call.setClassType(classType.clone());
		call.getArgs().add(argument);

		return call;
	}
	
	public static AEqualsBinaryExpCG consFieldComparison(ARecordDeclCG record, AFieldDeclCG field, String formalParamName) throws AnalysisException
	{
		//Example: fieldName == formalParamName.fieldName
		
		AEqualsBinaryExpCG fieldComparison = new AEqualsBinaryExpCG();
		fieldComparison.setType(new ABoolBasicTypeCG());
		
		AVariableExpCG instanceField = new AVariableExpCG();
		instanceField.setType(field.getType().clone());
		instanceField.setOriginal(field.getName());

		AFieldExpCG formalParamField = new AFieldExpCG();
		formalParamField.setType(field.getType().clone());

		AVariableExpCG formalParam = new AVariableExpCG();
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
}
