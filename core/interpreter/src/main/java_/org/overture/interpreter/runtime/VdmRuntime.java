package org.overture.interpreter.runtime;

import java.util.HashMap;
import java.util.Map;

import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.interpreter.eval.StatementEvaluator;
import org.overture.interpreter.runtime.state.StateDefinitionRuntimeState;
import org.overture.interpreter.values.Value;

public class VdmRuntime
{
	private static IQuestionAnswer<Context, Value> expressionRuntime;
	private static IQuestionAnswer<Context, Value> statementRuntime;
	
	private static Map<INode,IRuntimeState> runtimeState = new HashMap<INode, IRuntimeState>();
	
	public static void initialize()
	{
		expressionRuntime = new StatementEvaluator(); 
		statementRuntime = expressionRuntime; 
	}
	
	public static IQuestionAnswer<Context, Value> getExpressionEvaluator()
	{
		if(expressionRuntime == null)
		{
			initialize();
		}
		
		return expressionRuntime;
	}
	
	public static IQuestionAnswer<Context, Value> getStatementEvaluator()
	{
		if(statementRuntime == null)
		{
			initialize();
		}
		
		return statementRuntime;
	}
	
	public static void setNodeState(INode node, IRuntimeState state)
	{
		runtimeState.put(node,state);
	}
	
	
	public static IRuntimeState getNodeState(INode node)
	{
		//TODO: probably needs something more
		return runtimeState.get(node);
	}
	
	public static StateDefinitionRuntimeState getNodeState(AStateDefinition node)
	{
		return (StateDefinitionRuntimeState) runtimeState.get(node);
	}
	
	public static SClassDefinitionRuntime getNodeState(SClassDefinition node)
	{
		SClassDefinitionRuntime state = (SClassDefinitionRuntime) runtimeState.get(node);
		
		if(state == null)
		{
			state = new SClassDefinitionRuntime();
			runtimeState.put(node, state );
		}
		
		return state;
	}
	
	
	
	
}
