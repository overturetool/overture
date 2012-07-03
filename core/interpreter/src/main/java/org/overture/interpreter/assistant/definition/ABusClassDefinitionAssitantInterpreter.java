package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactoryTC;
import org.overture.ast.types.AClassType;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.BUSValue;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.NameValuePairMap;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueSet;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

public class ABusClassDefinitionAssitantInterpreter
{

	public static BUSValue makeVirtualBUS(ValueSet cpus)
	{
		try
		{
			return new BUSValue((AClassType)AstFactoryTC.newABusClassDefinition().getType(), cpus);
		} catch (ParserException e)
		{
			
		} catch (LexException e)
		{
			
		}
		return null;
	}

	public static ObjectValue newInstance(ABusClassDefinition node,
			PDefinition ctorDefinition, ValueList argvals, Context ctxt)
	{
		NameValuePairList nvpl = PDefinitionListAssistantInterpreter.getNamedValues(node.getDefinitions(),ctxt);
		NameValuePairMap map = new NameValuePairMap();
		map.putAll(nvpl);

		return new BUSValue((AClassType)node.getClasstype(), map, argvals);
	}

}
