package org.overture.ast.factory;

import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.LexNameToken;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

public class AstFactoryTC extends AstFactory
{
	
	
	public static SClassDefinition newACpuClassDefinition()
			throws ParserException, LexException
	{
		ITypeCheckerAssistantFactory assistantFactory = new TypeCheckerAssistantFactory();
		ACpuClassDefinition result = new ACpuClassDefinition();
		initClassDefinition(result, new LexNameToken("CLASS", "CPU", new LexLocation()), new LexNameList(), assistantFactory.createACpuClassDefinitionAssistant().operationDefs());

		return result;
	}

	public static SClassDefinition newABusClassDefinition()
			throws ParserException, LexException
	{
		ITypeCheckerAssistantFactory assistantFactory = new TypeCheckerAssistantFactory();
		ABusClassDefinition result = new ABusClassDefinition();
		initClassDefinition(result, new LexNameToken("CLASS", "BUS", new LexLocation()), new LexNameList(), assistantFactory.createABusClassDefinitionAssistant().operationDefs());

		result.setInstance(result);

		return result;
	}

}
