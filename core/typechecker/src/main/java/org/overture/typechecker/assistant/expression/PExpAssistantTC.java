package org.overture.typechecker.assistant.expression;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PExpAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;
	// A LexNameToken to indicate that a function has no precondition name, rather than
	// that it is not a pure function (indicated by null).
	public final static LexNameToken NO_PRECONDITION = new LexNameToken("", "", null);

	@SuppressWarnings("static-access")
	public PExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static ILexNameToken getPreName(PExp expression)
	{
		try
		{
			return expression.apply(af.getPreNameFinder());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}
	}

}
