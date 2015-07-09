package org.overture.interpreter.assistant.definition;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.StateContext;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.state.StateDefinitionRuntimeState;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.State;

public class AStateDefinitionAssistantInterpreter implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AStateDefinitionAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		//super(af);
		this.af = af;
	}

	public Context getStateContext(AStateDefinition state)
	{
		return VdmRuntime.getNodeState(state).moduleState.getContext();
	}

	public void initState(AStateDefinition sdef, StateContext initialContext)
	{
		StateDefinitionRuntimeState state = new StateDefinitionRuntimeState();
		VdmRuntime.setNodeState(sdef, state);
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

}
