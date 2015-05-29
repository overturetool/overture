package org.overture.interpreter.runtime;


import java.util.HashMap;
import java.util.Map;

import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.eval.DelegateStatementEvaluator;
import org.overture.interpreter.runtime.state.AExplicitFunctionDefinitionRuntimeState;
import org.overture.interpreter.runtime.state.AImplicitFunctionDefinitionRuntimeState;
import org.overture.interpreter.runtime.state.AModuleModulesRuntime;
import org.overture.interpreter.runtime.state.ASystemClassDefinitionRuntime;
import org.overture.interpreter.runtime.state.SClassDefinitionRuntime;
import org.overture.interpreter.runtime.state.StateDefinitionRuntimeState;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * The runtime class must be re-initialized before an execution.
 * 
 * @author pvj
 */
public class VdmRuntime
{
	private static IQuestionAnswer<Context, Value> expressionRuntime;
	private static IQuestionAnswer<Context, Value> statementRuntime;

	final private static Map<INode, IRuntimeState> runtimeState = new HashMap<INode, IRuntimeState>();

	static
	{
		initialize();
	}

	public static CoverageToXML initialize()
	{

        DelegateStatementEvaluator dse = new DelegateStatementEvaluator();
        expressionRuntime = dse;
		statementRuntime = expressionRuntime;
		runtimeState.clear();
        return dse.ctx;
	}

	public static void initialize(
			IQuestionAnswer<Context, Value> expressionRuntime)
	{
		VdmRuntime.expressionRuntime = expressionRuntime;
		VdmRuntime.statementRuntime = expressionRuntime;
		VdmRuntime.runtimeState.clear();
	}

	public static IQuestionAnswer<Context, Value> getExpressionEvaluator()
	{
		if (expressionRuntime == null)
		{
			initialize(statementRuntime);
		}

		return expressionRuntime;
	}

	public static IQuestionAnswer<Context, Value> getStatementEvaluator()
	{
		if (statementRuntime == null)
		{
			initialize();
		}

		return statementRuntime;
	}

	public static void setNodeState(INode node, IRuntimeState state)
	{
		runtimeState.put(node, state);
	}

	public static IRuntimeState getNodeState(INode node)
	{
		return runtimeState.get(node);
	}

	public static StateDefinitionRuntimeState getNodeState(AStateDefinition node)
	{
		return (StateDefinitionRuntimeState) runtimeState.get(node);
	}

	public static AModuleModulesRuntime getNodeState(AModuleModules node,
			ITypeCheckerAssistantFactory af)
	{
		AModuleModulesRuntime state = (AModuleModulesRuntime) runtimeState.get(node);

		if (state == null)
		{
			state = new AModuleModulesRuntime(node, af);
			runtimeState.put(node, state);
		}

		return state;
	}

	public static SClassDefinitionRuntime getNodeState(
			IInterpreterAssistantFactory assistantFactory, SClassDefinition node)
	{
		SClassDefinitionRuntime state = (SClassDefinitionRuntime) runtimeState.get(node);

		if (state == null)
		{
			state = new SClassDefinitionRuntime(assistantFactory, node);
			runtimeState.put(node, state);
		}

		return state;
	}

	public static ASystemClassDefinitionRuntime getNodeState(
			IInterpreterAssistantFactory assistantFactory,
			ASystemClassDefinition node)
	{
		ASystemClassDefinitionRuntime state = (ASystemClassDefinitionRuntime) runtimeState.get(node);

		if (state == null)
		{
			state = new ASystemClassDefinitionRuntime(assistantFactory, node);
			runtimeState.put(node, state);
		}

		return state;
	}

	public static AImplicitFunctionDefinitionRuntimeState getNodeState(
			AImplicitFunctionDefinition node)
	{
		AImplicitFunctionDefinitionRuntimeState state = (AImplicitFunctionDefinitionRuntimeState) runtimeState.get(node);

		if (state == null)
		{
			state = new AImplicitFunctionDefinitionRuntimeState();
			runtimeState.put(node, state);
		}

		return state;
	}

	public static AExplicitFunctionDefinitionRuntimeState getNodeState(
			AExplicitFunctionDefinition node)
	{
		AExplicitFunctionDefinitionRuntimeState state = (AExplicitFunctionDefinitionRuntimeState) runtimeState.get(node);

		if (state == null)
		{
			state = new AExplicitFunctionDefinitionRuntimeState();
			runtimeState.put(node, state);
		}

		return state;
	}

}
