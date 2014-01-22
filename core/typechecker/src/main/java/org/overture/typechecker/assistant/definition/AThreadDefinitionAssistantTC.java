package org.overture.typechecker.assistant.definition;

import java.util.Vector;

import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AOperationType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AThreadDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AThreadDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public AExplicitOperationDefinition getThreadDefinition(AThreadDefinition d)
	{

		AOperationType type = AstFactory.newAOperationType(d.getLocation()); // () ==> ()

		AExplicitOperationDefinition def = AstFactory.newAExplicitOperationDefinition(d.getOperationName(), type, new Vector<PPattern>(), null, null, d.getStatement().clone());

		def.setAccess(d.getAccess().clone());
		def.setClassDefinition(d.getClassDefinition());
		return def;
	}

}
