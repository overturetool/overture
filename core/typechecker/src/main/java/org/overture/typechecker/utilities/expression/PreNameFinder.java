package org.overture.typechecker.utilities.expression;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.node.INode;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

/**
 * Used to find the Name of an expression or a definition.
 * 
 * @author kel
 */

public class PreNameFinder extends AnswerAdaptor<ILexNameToken>
{
	protected ITypeCheckerAssistantFactory af;
	// A LexNameToken to indicate that a function has no precondition name, rather than
	// that it is not a pure function (indicated by null).
	public final static LexNameToken NO_PRECONDITION = new LexNameToken("", "", null);

	public PreNameFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public ILexNameToken caseAFuncInstatiationExp(
			AFuncInstatiationExp expression) throws AnalysisException
	{
		AFuncInstatiationExp func = AFuncInstatiationExp.class.cast(expression);
		return func.getFunction().apply(this); // getPreName(func.getFunction());
	}

	@Override
	public ILexNameToken caseAVariableExp(AVariableExp expression)
			throws AnalysisException
	{
		ILexNameToken result = null;

		AVariableExp var = AVariableExp.class.cast(expression);
		PDefinition def = PDefinitionAssistantTC.deref(var.getVardef());
		if (def instanceof AExplicitFunctionDefinition)
		{
			AExplicitFunctionDefinition ex = AExplicitFunctionDefinition.class.cast(def);
			PDefinition predef = ex.getPredef();
			result = predef == null ? NO_PRECONDITION : predef.getName();

		} else if (def instanceof AImplicitFunctionDefinition)
		{
			AImplicitFunctionDefinition im = AImplicitFunctionDefinition.class.cast(def);
			PDefinition predef = im.getPredef();
			result = predef == null ? NO_PRECONDITION : predef.getName();
		}
		return result;
	}

	@Override
	public ILexNameToken createNewReturnValue(INode node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILexNameToken createNewReturnValue(Object node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
