package org.overture.interpreter.assistant.statement;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.interpreter.assistant.pattern.PPatternAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.RuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueSet;

public class ATixeStmtAlternativeAssistantInterpreter
{

	public static Value eval( ATixeStmtAlternative node,
			LexLocation location, Value exval, Context ctxt) throws AnalysisException
	{
		Context evalContext = null;

		try
		{
			if (node.getPatternBind().getPattern() != null)
			{
				evalContext = new Context(location, "tixe pattern", ctxt);
				evalContext.putList(PPatternAssistantInterpreter.getNamedValues(node.getPatternBind().getPattern(),exval, ctxt));
			}
			else if (node.getPatternBind().getBind() instanceof ASetBind)
			{
				ASetBind setbind = (ASetBind)node.getPatternBind().getBind();
				ValueSet set = setbind.getSet().apply(VdmRuntime.getStatementEvaluator(),ctxt).setValue(ctxt);

				if (set.contains(exval))
				{
					evalContext = new Context(location, "tixe set", ctxt);
					evalContext.putList(PPatternAssistantInterpreter.getNamedValues(setbind.getPattern(),exval, ctxt));
				}
				else
				{
					RuntimeError.abort(setbind.getLocation(),4049, "Value " + exval + " is not in set bind", ctxt);
				}
			}
			else
			{
				ATypeBind typebind = (ATypeBind)node.getPatternBind().getBind();
				// Note we always perform DTC checks here...
				Value converted = exval.convertValueTo(typebind.getType(), ctxt);
				evalContext = new Context(location, "tixe type", ctxt);
				evalContext.putList(PPatternAssistantInterpreter.getNamedValues(typebind.getPattern(),converted, ctxt));
			}
		}
		catch (ValueException ve)	// Type bind convert failure
		{
			evalContext = null;
		}
		catch (PatternMatchException e)
		{
			evalContext = null;
		}

		return evalContext == null ? null : node.getStatement().apply(VdmRuntime.getStatementEvaluator(),evalContext);
	}

}
