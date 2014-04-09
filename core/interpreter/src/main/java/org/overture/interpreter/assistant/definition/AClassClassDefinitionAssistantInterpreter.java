package org.overture.interpreter.assistant.definition;

import java.util.HashMap;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.ValueList;

public class AClassClassDefinitionAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AClassClassDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public static ObjectValue newInstance(AClassClassDefinition node,
			PDefinition ctorDefinition, ValueList argvals, Context ctxt)
			throws AnalysisException
	{
		if (node.getIsAbstract())
		{
			VdmRuntimeError.abort(node.getLocation(), 4000, "Cannot instantiate abstract class "
					+ node.getName(), ctxt);
		}

		return SClassDefinitionAssistantInterpreter.makeNewInstance(node, ctorDefinition, argvals, ctxt, new HashMap<ILexNameToken, ObjectValue>());
	}

}
