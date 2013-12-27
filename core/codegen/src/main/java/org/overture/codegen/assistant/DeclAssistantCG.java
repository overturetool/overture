package org.overture.codegen.assistant;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG;
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.OoAstConstants;
import org.overture.codegen.visitor.OoAstInfo;

public class DeclAssistantCG
{

	public DeclAssistantCG()
	{	
	}
	
	public static AIntLiteralExpCG getDefaultIntValue()
	{
		AIntLiteralExpCG lit = new AIntLiteralExpCG();
		lit.setType(new AIntNumericBasicTypeCG());
		lit.setValue("0");
		
		return lit;
	}
	
	public static ARealLiteralExpCG getDefaultRealValue()
	{
		ARealLiteralExpCG lit = new ARealLiteralExpCG();
		lit.setType(new ARealNumericBasicTypeCG());
		lit.setValue("0.0");
		
		return lit;
	}
	
	public static ABoolLiteralExpCG getDefaultBoolValue()
	{
		ABoolLiteralExpCG lit = new ABoolLiteralExpCG();
		lit.setType(new ABoolBasicTypeCG());
		lit.setValue("false");
		
		return lit;
	}
	
	public static ACharLiteralExpCG getDefaultCharlValue()
	{
		ACharLiteralExpCG lit = new ACharLiteralExpCG();
		lit.setType(new ACharBasicTypeCG());
		lit.setValue("0");
		
		return lit;
	}
	
	public static AStringLiteralExpCG getDefaultStringlValue()
	{
		AStringLiteralExpCG lit = new AStringLiteralExpCG();
		lit.setIsNull(false);
		lit.setType(new AStringTypeCG());
		lit.setValue("");
		
		return lit;
	}
	
	public static ANullExpCG getDefaultClassValue()
	{
		return new ANullExpCG();
	}
	
	public static void setLocalDefs(LinkedList<PDefinition> localDefs, LinkedList<ALocalVarDeclCG> localDecls, OoAstInfo question) throws AnalysisException
	{
		for (PDefinition def : localDefs)
		{
			if(def instanceof AValueDefinition)
			{
				AValueDefinition valueDef = (AValueDefinition) def;
				localDecls.add(DeclAssistantCG.constructLocalVarDecl(valueDef, question));
			}
		}
	}
	
	private static ALocalVarDeclCG constructLocalVarDecl(AValueDefinition valueDef, OoAstInfo question) throws AnalysisException
	{
		PTypeCG type = valueDef.getType().apply(question.getTypeVisitor(), question);
		String name = valueDef.getPattern().toString();
		PExpCG exp = valueDef.getExpression().apply(question.getExpVisitor(), question);
		
		ALocalVarDeclCG localVarDecl = new ALocalVarDeclCG();
		localVarDecl.setType(type);
		localVarDecl.setName(name);
		localVarDecl.setExp(exp);
		
		return localVarDecl;
		
	}
	
	public AFieldDeclCG constructField(String access, String name, boolean isStatic, boolean isFinal, PTypeCG type, PExpCG exp)
	{
		
		AFieldDeclCG field = new AFieldDeclCG();
		field.setAccess(access);
		field.setName(name);
		field.setStatic(isStatic);
		field.setFinal(isFinal);
		field.setType(type);
		field.setInitial(exp);
		
		return field;
	}
	
	public static boolean isValidClassName(String className)
	{
		for(int i = 0; i < OoAstConstants.RESERVED_CLASS_NAMES.length; i++)
			if(OoAstConstants.RESERVED_CLASS_NAMES[i].equals(className))
				return false;
		
		return true; 
	}
	
	public static boolean causesMethodOverloading(LinkedList<AMethodDeclCG> methods, AMethodDeclCG method)
	{
		for (AMethodDeclCG aMethodDeclCG : methods)
			if(aMethodDeclCG.getName().equals(method.getName()))
				return true;
		
		return false;
	}

	public static void setDefaultValue(ALocalVarDeclCG localDecl, PTypeCG typeCg) throws AnalysisException
	{
		//Set initial value
		if(typeCg instanceof AStringTypeCG)
		{
			localDecl.setExp(DeclAssistantCG.getDefaultStringlValue());
		}
		else if(typeCg instanceof ACharBasicTypeCG)
		{
			localDecl.setExp(DeclAssistantCG.getDefaultCharlValue());
		}
		else if(typeCg instanceof AIntNumericBasicTypeCG)
		{
			localDecl.setExp(DeclAssistantCG.getDefaultIntValue());
		}
		else if(typeCg instanceof ARealNumericBasicTypeCG)
		{
			localDecl.setExp(DeclAssistantCG.getDefaultRealValue());
		}
		else if(typeCg instanceof ABoolBasicTypeCG)
		{
			localDecl.setExp(DeclAssistantCG.getDefaultBoolValue());
		}
		else if(typeCg instanceof AClassTypeCG)
		{
			localDecl.setExp(DeclAssistantCG.getDefaultClassValue());
		}
		else
		{
			throw new AnalysisException("Unexpected type in block statement: " + typeCg);
		}
	}

	public static AFieldDeclCG getFieldDecl(List<AClassDeclCG> classes, ARecordTypeCG recordType, String memberName)
	{
		ATypeNameCG name = recordType.getName();
		
		if(name == null)
			throw new IllegalArgumentException("Could not find type name for record type: " + recordType);
			
		String definingClassName = name.getDefiningClass();
		
		if(definingClassName == null)
			throw new IllegalArgumentException("Could not find defining class for record type: " + recordType);
		
		String recName = name.getName();
		
		if(recName == null)
			throw new IllegalArgumentException("Could not find record name for record type: " + recordType);
		
		AClassDeclCG definingClass = null;
		for (AClassDeclCG currentClass : classes)
		{
			if (currentClass.getName().equals(definingClassName))
			{
				definingClass = currentClass;
				break;
			}
		}
		
		if(definingClass == null)
			throw new IllegalArgumentException("Could not find defining class with name: " + definingClassName);
		
		LinkedList<ARecordDeclCG> records = definingClass.getRecords();
		
		ARecordDeclCG recordDecl = null;
		for (ARecordDeclCG currentRec : records)
		{
			if(currentRec.getName().equals(recName))
			{
				recordDecl = currentRec;
				break;
			}
		}
		
		if(recordDecl == null)
			throw new IllegalArgumentException("Could not find record with name '" + recName + "' in class '" + definingClassName + "'");
		
		LinkedList<AFieldDeclCG> fields = recordDecl.getFields();
		
		
		AFieldDeclCG field = null;
		for (AFieldDeclCG currentField : fields)
		{
			if(currentField.getName().equals(memberName))
			{
				field = currentField;
			}
		}
		
		if(field == null)
			throw new IllegalArgumentException("Could not find field '" + memberName + "' in record '" + recName + "' in class '" + definingClassName + "'");
		
		return field;
	}
}
