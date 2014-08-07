package org.overture.interpreter.runtime.state;

import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.ObjectValue;

public class ASystemClassDefinitionRuntime extends SClassDefinitionRuntime
{
	public ASystemClassDefinitionRuntime(
			IInterpreterAssistantFactory assistantFactory,
			ASystemClassDefinition def)
	{
		super(assistantFactory, def);
	}

	public static ObjectValue system = null;

	/**
	 * DESTECS
	 * 
	 * @return
	 */
	public static NameValuePairList getSystemMembers()
	{
		if (system != null)
		{
			return system.members.asList();
		}

		return null;
	}
}
