package org.overture.interpreter.runtime;

import java.util.HashMap;
import java.util.Map;

import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.node.INode;
import org.overture.interpreter.values.Value;

public class VdmRuntime
{
	private static IQuestionAnswer<Context, Value> runtime;
	
	private static Map<INode,IRuntimeState> runtimeState = new HashMap<INode, IRuntimeState>();
	
	public static IQuestionAnswer<Context, Value> getExpressionEvaluator()
	{
		if(runtime == null)
		{
			//FIXME: create the runtime
			runtime = null; 
		}
		
		return runtime;
	}
	
	public static IQuestionAnswer<Context, Value> getStatementEvaluator()
	{
		if(runtime == null)
		{
			//FIXME: create the runtime
			runtime = null; 
		}
		
		return runtime;
	}
	
	
	public static IRuntimeState getNodeState(INode node)
	{
		//TODO: probably needs something more
		return runtimeState.get(node);
	}
	
}
