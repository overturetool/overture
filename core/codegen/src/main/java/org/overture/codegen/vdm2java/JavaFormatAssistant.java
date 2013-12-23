package org.overture.codegen.vdm2java;

import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AInstanceofExpCG;
import org.overture.codegen.cgast.expressions.AVariableExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;

public class JavaFormatAssistant
{
	public static AAndBoolBinaryExpCG extendAndExp(ARecordDeclCG record, AFieldDeclCG field, PExpCG previous, String paramName)
	{
		//By recursively calling this method an "and chain" of field 
		//comparisons can be constructed: fieldComp1 && fieldComp2 && fieldComp3 ....
		
		AAndBoolBinaryExpCG nextAnd = new AAndBoolBinaryExpCG();
		nextAnd.setType(new ABoolBasicTypeCG());
		nextAnd.setLeft(previous);
		nextAnd.setRight(constructFieldComparison(record, field, paramName));
		return nextAnd;
	}
	
	
	public static ABoolLiteralExpCG constructBoolLiteral(boolean val)
	{
		ABoolLiteralExpCG boolLiteral = new ABoolLiteralExpCG();
		boolLiteral.setType(new ABoolBasicTypeCG());
		boolLiteral.setValue(val + "");
		
		return boolLiteral;
	}
	
	public static AInstanceofExpCG constructInstanceOf(ARecordDeclCG record, String formalParamName)
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
	
	public static AEqualsBinaryExpCG constructFieldComparison(ARecordDeclCG record, AFieldDeclCG field, String formalParamName)
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
		recordType.setRecDecl(record.clone());
		formalParam.setType(recordType);
		formalParam.setOriginal(formalParamName);
		
		formalParamField.setObject(formalParam);
		formalParamField.setMemberName(field.getName());
		
		fieldComparison.setLeft(instanceField);
		fieldComparison.setRight(formalParamField);
		
		return fieldComparison;
	}
}
