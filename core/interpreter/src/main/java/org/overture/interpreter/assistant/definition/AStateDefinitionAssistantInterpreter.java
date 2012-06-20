package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.StateContext;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.state.StateDefinitionRuntimeState;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.State;
import org.overture.typechecker.assistant.definition.AStateDefinitionAssistantTC;

public class AStateDefinitionAssistantInterpreter extends
		AStateDefinitionAssistantTC
{

	public static Context getStateContext(AStateDefinition state)
	{
		return VdmRuntime.getNodeState(state).moduleState.getContext();
	}

	public static void initState(AStateDefinition sdef,
			StateContext initialContext)
	{
		StateDefinitionRuntimeState state = new StateDefinitionRuntimeState();
		if (sdef.getInvdef() != null)
		{
			state.invfunc = new FunctionValue(sdef.getInvdef(), null, null, initialContext);
			initialContext.put(sdef.getName().getInvName(sdef.getLocation()), state.invfunc);
		}
	
		if (sdef.getInitdef() != null)
		{
			state.initfunc = new FunctionValue(sdef.getInitdef(), null, null, initialContext);
			initialContext.put(sdef.getName().getInitName(sdef.getLocation()), state.initfunc);
		}
	
		state.moduleState = new State(sdef);
		state.moduleState.initialize(initialContext);
	}

	public static PExp findExpression(AStateDefinition d, int lineno)
	{
		if (d.getInitExpression() != null)
		{
			PExp found = PExpAssistantInterpreter.findExpression(d.getInvExpression(),lineno);
			if (found != null) return found;
		}

		if (d.getInitExpression() != null)
		{
			if (d.getInitExpression() instanceof AEqualsBinaryExp)
			{
				AEqualsBinaryExp ee = (AEqualsBinaryExp)d.getInitExpression();
				PExp found = PExpAssistantInterpreter.findExpression(ee.getRight(),lineno);
				if (found != null) return found;
			}
		}

		return null;
	}
	


}
