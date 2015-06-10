package org.overture.interpreter.assistant.statement;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class PStmAssistantInterpreter implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PStmAssistantInterpreter(IInterpreterAssistantFactory af)
	{
	//	super(af);
		this.af = af;
	}

	public PExp findExpression(PStm stm, int lineno)
	{
		try
		{
			return stm.apply(af.getStatementExpressionFinder(), lineno);// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null; // Most have none
		}
	}

	/**
	 * Find a statement starting on the given line. Single statements just compare their location to lineno, but block
	 * statements and statements with sub-statements iterate over their branches.
	 * 
	 * @param stm
	 *            the statement
	 * @param lineno
	 *            The line number to locate.
	 * @return A statement starting on the line, or null.
	 */
	public PStm findStatement(PStm stm, int lineno)
	{
		try
		{
			return stm.apply(af.getStatementFinder(), lineno);// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null; // Most have none
		}

	}

}
