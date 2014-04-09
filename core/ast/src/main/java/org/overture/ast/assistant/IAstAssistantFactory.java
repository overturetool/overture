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
import org.overture.ast.types.SNumericBasicType;

//TODO Add assistant Javadoc
/**
 * This is the main Assistant factory interface. Everyone will see this
 * so it should be well documented.
 * @author ldc
 *
 */
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
	IAnswer<SNumericBasicType> getNumericBasisChecker();
	IAnswer<Integer> getHashChecker();
}
