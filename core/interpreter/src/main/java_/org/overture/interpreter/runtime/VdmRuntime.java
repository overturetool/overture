package org.overture.interpreter.runtime;

import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.interpreter.values.Value;

public class VdmRuntime
{
	private static IQuestionAnswer<Context, Value> runtime;
	
	
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
}
