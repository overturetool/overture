package org.overture.codegen.assistant;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.utils.LexNameTokenWrapper;

public class DeclAssistantCG extends AssistantBase
{
	public DeclAssistantCG(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public void setLocalDefs(LinkedList<PDefinition> localDefs, LinkedList<AVarLocalDeclCG> localDecls, IRInfo question) throws AnalysisException
	{
		for (PDefinition def : localDefs)
		{
			if(def instanceof AValueDefinition)
			{
				AValueDefinition valueDef = (AValueDefinition) def;
				localDecls.add(constructLocalVarDecl(valueDef, question));
			}
		}
	}
	
	public AClassDeclCG findClass(List<AClassDeclCG> classes, String moduleName)
	{
		for(AClassDeclCG classDecl : classes)
		{
			if(classDecl.getName().equals(moduleName))
			{
				return classDecl;
			}
		}
		
		return null;
	}
	
	private AVarLocalDeclCG constructLocalVarDecl(AValueDefinition valueDef, IRInfo question) throws AnalysisException
	{
		STypeCG type = valueDef.getType().apply(question.getTypeVisitor(), question);
		SPatternCG pattern = valueDef.getPattern().apply(question.getPatternVisitor(), question);
		SExpCG exp = valueDef.getExpression().apply(question.getExpVisitor(), question);
		
		AVarLocalDeclCG localVarDecl = new AVarLocalDeclCG();
		localVarDecl.setType(type);
		localVarDecl.setPattern(pattern);
		localVarDecl.setExp(exp);
		
		return localVarDecl;
	}
	
	public AFieldDeclCG constructField(String access, String name, boolean isStatic, boolean isFinal, STypeCG type, SExpCG exp)
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
	
	public Set<ILexNameToken> getOverloadedMethodNames(AClassClassDefinition classDef)
	{
		List<LexNameTokenWrapper> methodNames = getMethodNames(classDef);
		Set<LexNameTokenWrapper> duplicates = findDuplicates(methodNames);

		Set<ILexNameToken> overloadedMethodNames = new HashSet<ILexNameToken>();
		
		for (LexNameTokenWrapper wrapper : methodNames)
		{
			if(duplicates.contains(wrapper))
				overloadedMethodNames.add(wrapper.getName());
		}
		
		return overloadedMethodNames; 
	}
	
	private Set<LexNameTokenWrapper> findDuplicates(List<LexNameTokenWrapper> nameWrappers)
	{
		Set<LexNameTokenWrapper> duplicates = new HashSet<LexNameTokenWrapper>();
		Set<LexNameTokenWrapper> temp = new HashSet<LexNameTokenWrapper>();

		for (LexNameTokenWrapper wrapper : nameWrappers)
		{
			if (!temp.add(wrapper))
			{
				duplicates.add(wrapper);
			}
		}
		
		return duplicates;
	}
	
	private List<LexNameTokenWrapper> getMethodNames(AClassClassDefinition classDef)
	{
		List<LexNameTokenWrapper> methodNames = new LinkedList<LexNameTokenWrapper>();

		LinkedList<PDefinition> defs = classDef.getDefinitions();
		
		for (PDefinition def : defs)
		{
			if(def instanceof SOperationDefinition || def instanceof SFunctionDefinition)
				methodNames.add(new LexNameTokenWrapper(def.getName()));
		}
		
		return methodNames;
	}
	
	public void setDefaultValue(AVarLocalDeclCG localDecl, STypeCG typeCg) throws AnalysisException
	{
		ExpAssistantCG expAssistant = assistantManager.getExpAssistant();
		
		if(typeCg instanceof AStringTypeCG)
		{
			localDecl.setExp(expAssistant.getDefaultStringlValue());
		}
		else if(typeCg instanceof ACharBasicTypeCG)
		{
			localDecl.setExp(expAssistant.getDefaultCharlValue());
		}
		else if(typeCg instanceof AIntNumericBasicTypeCG)
		{
			localDecl.setExp(expAssistant.getDefaultIntValue());
		}
		else if(typeCg instanceof ARealNumericBasicTypeCG)
		{
			localDecl.setExp(expAssistant.getDefaultRealValue());
		}
		else if(typeCg instanceof ABoolBasicTypeCG)
		{
			localDecl.setExp(expAssistant.getDefaultBoolValue());
		}
		else
		{
			localDecl.setExp(new ANullExpCG());
		}
	}

	public AFieldDeclCG getFieldDecl(List<AClassDeclCG> classes, ARecordTypeCG recordType, String memberName)
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
