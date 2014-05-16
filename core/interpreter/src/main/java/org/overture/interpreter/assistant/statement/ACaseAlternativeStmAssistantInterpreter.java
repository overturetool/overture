package org.overture.interpreter.assistant.statement;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.pattern.PPatternAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.Value;

public class ACaseAlternativeStmAssistantInterpreter // extends
// ACaseAlternativeStmAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ACaseAlternativeStmAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		// super(af);
		this.af = af;
	}

	public static Value eval(ACaseAlternativeStm node, Value val, Context ctxt)
			throws AnalysisException
	{
		Context evalContext = new Context(af, node.getLocation(), "case alternative", ctxt);

		node.getPattern().getLocation().hit();
		node.getLocation().hit();
		try
		{
			evalContext.putList(af.createPPatternAssistant().getNamedValues(node.getPattern(), val, ctxt));
			return node.getResult().apply(VdmRuntime.getStatementEvaluator(), evalContext);
		} catch (PatternMatchException e)
		{
			// CasesStatement tries the others
		}

		return null;
	}

}
