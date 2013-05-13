package org.overture.ide.plugins.codegen.visitor;

import java.util.HashMap;

import org.overture.ast.expressions.PExp;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class CodeGenAssistant
{

	private static final String UNKNOWN = "UNKNOWN";
	
	private HashMap<String, String> numericTypeMappings;
	
	private static CodeGenAssistant Instance;
	
	private CodeGenAssistant()
	{
		initNumericTypeMappings();
	}
	
	public static CodeGenAssistant GetInstance()
	{
		if(Instance == null)
			Instance = new CodeGenAssistant();
		
		return Instance;
	}
	
	public String formatExpression(PExp exp)
	{
		return "NOT IMPLEMENTED";
	}
	
	public void initNumericTypeMappings()
	{
		
		numericTypeMappings = new HashMap<String, String>();
		//There are five numeric types:
		numericTypeMappings.put("real", "double");
		numericTypeMappings.put("rat", "double"); //TODO: Reconsider this mapping
		numericTypeMappings.put("int", "long");
		numericTypeMappings.put("nat", "int");
		numericTypeMappings.put("nat1", "int"); //TODO: Int can be negative
	}
	
	public String formatType(PType typeToFormat)
	{
		
		if(PTypeAssistantTC.isClass(typeToFormat))
		{
			return PTypeAssistantTC.getClassType(typeToFormat).getName().getName();
		}
		else if(PTypeAssistantTC.isNumeric(typeToFormat))
		{
			return formatNumericType(typeToFormat);
		}
		
		return UNKNOWN;
	}

	private String formatNumericType(PType typeToFormat)
	{
		return numericTypeMappings.get(typeToFormat.toString());
	}
	
}
