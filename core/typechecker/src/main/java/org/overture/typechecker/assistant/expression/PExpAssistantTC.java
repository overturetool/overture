package org.overture.typechecker.assistant.expression;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

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
//		ILexNameToken result = null;
//
//		if (expression instanceof AFuncInstatiationExp)
//		{
//			AFuncInstatiationExp func = AFuncInstatiationExp.class.cast(expression);
//			result = getPreName(func.getFunction());
//		} else if (expression instanceof AVariableExp)
//		{
//			AVariableExp var = AVariableExp.class.cast(expression);
//			PDefinition def = PDefinitionAssistantTC.deref(var.getVardef());
//
//			// TODO: This will not work if the functions is renamed more than one time, can this occur??
//			// if (def instanceof ARenamedDefinition)
//			// def = ((ARenamedDefinition) def).getDef();
//			// else if (def instanceof AInheritedDefinition)
//			// def = ((AInheritedDefinition) def).getSuperdef();
//			if (def instanceof AExplicitFunctionDefinition)
//			{
//				AExplicitFunctionDefinition ex = AExplicitFunctionDefinition.class.cast(def);
//				PDefinition predef = ex.getPredef();
//				result = predef == null ? NO_PRECONDITION : predef.getName();
//
//			} else if (def instanceof AImplicitFunctionDefinition)
//			{
//				AImplicitFunctionDefinition im = AImplicitFunctionDefinition.class.cast(def);
//				PDefinition predef = im.getPredef();
//				result = predef == null ? NO_PRECONDITION : predef.getName();
//			}
//		}
//		return result;
	}

}
