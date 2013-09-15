package org.overture.typechecker.assistant.definition;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AOperationType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AThreadDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AThreadDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static List<PDefinition> getDefinitions(AThreadDefinition d)
	{
		List<PDefinition> result = new Vector<PDefinition>();
		result.add(d.getOperationDef());
		return result;
	}
	
	public static void implicitDefinitions(AThreadDefinition d, Environment env)
	{
		d.setOperationDef(getThreadDefinition(d));

	}

	private static AExplicitOperationDefinition getThreadDefinition(
			AThreadDefinition d)
	{

		AOperationType type = AstFactory.newAOperationType(d.getLocation()); // () ==> ()

		AExplicitOperationDefinition def = AstFactory.newAExplicitOperationDefinition(d.getOperationName(), type, new Vector<PPattern>(), null, null, d.getStatement().clone());

		def.setAccess(d.getAccess().clone());
		def.setClassDefinition(d.getClassDefinition());
		return def;
	}

}
