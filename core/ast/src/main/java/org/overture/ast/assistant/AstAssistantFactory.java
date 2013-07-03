package org.overture.ast.assistant;

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

public class AstAssistantFactory implements IAstAssistantFactory
{

	@Override
	public PAccessSpecifierAssistant createPAccessSpecifierAssistant()
	{
		return new PAccessSpecifierAssistant();
	}

	@Override
	public PDefinitionAssistant createPDefinitionAssistant()
	{
		return new PDefinitionAssistant();
	}

	@Override
	public PPatternAssistant createPPatternAssistant()
	{
		return new PPatternAssistant();
	}

	@Override
	public ABracketTypeAssistant createABracketTypeAssistant()
	{
		return new ABracketTypeAssistant();
	}

	@Override
	public ANamedInvariantTypeAssistant createANamedInvariantTypeAssistant()
	{
		return new ANamedInvariantTypeAssistant();
	}

	@Override
	public AOptionalTypeAssistant createAOptionalTypeAssistant()
	{
		return new AOptionalTypeAssistant();
	}

	@Override
	public AParameterTypeAssistant createAParameterTypeAssistant()
	{
		return new AParameterTypeAssistant();
	}

	@Override
	public AUnionTypeAssistant createAUnionTypeAssistant()
	{
		return new AUnionTypeAssistant();
	}

	@Override
	public AUnknownTypeAssistant createAUnknownTypeAssistant()
	{
		return new AUnknownTypeAssistant();
	}

	@Override
	public PTypeAssistant createPTypeAssistant()
	{
		return new PTypeAssistant();
	}

	@Override
	public SNumericBasicTypeAssistant createSNumericBasicTypeAssistant()
	{
		return new SNumericBasicTypeAssistant();
	}

}
