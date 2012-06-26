package org.overture.interpreter.assistant.pattern;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.assistant.pattern.AExpressionPatternAssistantTC;

public class AExpressionPatternAssistantInterpreter extends
		AExpressionPatternAssistantTC
{

	public static List<NameValuePairList> getAllNamedValues(
			AExpressionPattern p, Value expval, Context ctxt) throws PatternMatchException
	{
		List<NameValuePairList> result = new Vector<NameValuePairList>();

		
			
				try
				{
					if (!expval.equals(p.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt)))
					{
						VdmRuntimeError.patternFail(4110, "Expression pattern match failed",p.getLocation());
					}
				} catch (AnalysisException e)
				{
					if(e instanceof PatternMatchException)
					{
						throw (PatternMatchException)e;
					}
					e.printStackTrace();
				}
			
		

		result.add(new NameValuePairList());
		return result;		// NB no values for a match, as there's no definition
	}

}
