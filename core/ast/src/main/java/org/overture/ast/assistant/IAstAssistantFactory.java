package org.overture.ast.assistant;

import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.assistant.definition.PAccessSpecifierAssistant;
import org.overture.ast.assistant.definition.PDefinitionAssistant;
import org.overture.ast.assistant.pattern.PPatternAssistant;
import org.overture.ast.assistant.type.ABracketTypeAssistant;
import org.overture.ast.assistant.type.ANamedInvariantTypeAssistant;
import org.overture.ast.assistant.type.AOptionalTypeAssistant;
import org.overture.ast.assistant.type.AParameterTypeAssistant;
import org.overture.ast.assistant.type.AUnionTypeAssistant;
import org.overture.ast.assistant.type.AUnknownTypeAssistant;
import org.overture.ast.assistant.type.PTypeAssistant;
import org.overture.ast.assistant.type.SNumericBasicTypeAssistant;
import org.overture.ast.lex.LexNameList;

public interface IAstAssistantFactory
{
	PAccessSpecifierAssistant createPAccessSpecifierAssistant();
	PDefinitionAssistant createPDefinitionAssistant();
	
	PPatternAssistant createPPatternAssistant();
	//PTypeList??? thats not an assistant
	
	ABracketTypeAssistant createABracketTypeAssistant();
	ANamedInvariantTypeAssistant createANamedInvariantTypeAssistant();
	AOptionalTypeAssistant createAOptionalTypeAssistant();
	AParameterTypeAssistant createAParameterTypeAssistant();
	AUnionTypeAssistant createAUnionTypeAssistant();
	AUnknownTypeAssistant createAUnknownTypeAssistant();
	PTypeAssistant createPTypeAssistant();
	SNumericBasicTypeAssistant createSNumericBasicTypeAssistant();
	
	//visitors
	IAnswer<LexNameList> getAllVariableNameLocator();
	IAnswer<Boolean> getNumericFinder();
}
