package org.overture.interpreter.assistant.expression;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.AFieldNumberExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AIotaExp;
import org.overture.ast.expressions.AIsExp;
import org.overture.ast.expressions.AIsOfBaseClassExp;
import org.overture.ast.expressions.AIsOfClassExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AApplyExpAssistantTC;
import org.overture.typechecker.assistant.expression.ACasesExpAssistantTC;
import org.overture.typechecker.assistant.expression.AElseIfExpAssistantTC;
import org.overture.typechecker.assistant.expression.AExists1ExpAssistantTC;
import org.overture.typechecker.assistant.expression.AExistsExpAssistantTC;
import org.overture.typechecker.assistant.expression.AFieldExpAssistantTC;
import org.overture.typechecker.assistant.expression.AFieldNumberExpAssistantTC;
import org.overture.typechecker.assistant.expression.AForAllExpAssistantTC;
import org.overture.typechecker.assistant.expression.AFuncInstatiationExpAssistantTC;
import org.overture.typechecker.assistant.expression.AIfExpAssistantTC;
import org.overture.typechecker.assistant.expression.AIotaExpAssistantTC;
import org.overture.typechecker.assistant.expression.AIsExpAssistantTC;
import org.overture.typechecker.assistant.expression.AIsOfBaseClassExpAssistantTC;
import org.overture.typechecker.assistant.expression.AIsOfClassExpAssistantTC;
import org.overture.typechecker.assistant.expression.PExpAssistantTC;
import org.overture.typechecker.assistant.expression.SBinaryExpAssistantTC;

public class PExpAssistantInterpreter extends PExpAssistantTC
{

	/**
	 * Return a list of all the updatable values read by the expression. This
	 * is used to add listeners to values that affect permission guards, so
	 * that the guard may be efficiently re-evaluated when the values change.
	 *
	 * @param ctxt	The context in which to search for values.
	 * @return  A list of values read by the expression.
	 */
	public static ValueList getValues(PExp exp, ObjectContext ctxt)
	{
		switch (exp.kindPExp())
		{
			case APPLY:
				return AApplyExpAssistantInterpreter.getValues((AApplyExp)exp,ctxt);
			case BINARY:
				return SBinaryExpAssistantInterpreter.getValues((SBinaryExp)exp,ctxt);
			case CASES:
				return ACasesExpAssistantInterpreter.getValues((ACasesExp)exp,ctxt);
			case DEF:
			case ELSEIF:
				return AElseIfExpAssistantInterpreter.getValues((AElseIfExp)exp,ctxt);
			case EXISTS:
				return AExistsExpAssistantInterpreter.getValues((AExistsExp)exp,ctxt);
			case EXISTS1:
				return AExists1ExpAssistantInterpreter.getValues((AExists1Exp)exp,ctxt);
			case FIELD:
				return AFieldExpAssistantInterpreter.getValues((AFieldExp)exp,ctxt);
			case FIELDNUMBER:
				return AFieldNumberExpAssistantInterpreter.getValues((AFieldNumberExp)exp,ctxt);
			case FORALL:
				return AForAllExpAssistantInterpreter.getValues((AForAllExp)exp,ctxt);
			case FUNCINSTATIATION:
				return AFuncInstatiationExpAssistantInterpreter.getValues((AFuncInstatiationExp)exp,ctxt);
			case IF:
				return AIfExpAssistantInterpreter.getValues((AIfExp)exp,ctxt);
			case IOTA:
				return AIotaExpAssistantInterpreter.getValues((AIotaExp) exp,ctxt);
			case IS:
				return AIsExpAssistantInterpreter.getValues((AIsExp)exp,ctxt);
			case ISOFBASECLASS:
				return AIsOfBaseClassExpAssistantInterpreter.getValues((AIsOfBaseClassExp)exp,ctxt);
			case ISOFCLASS:
				return AIsOfClassExpAssistantInterpreter.getValues((AIsOfClassExp)exp,ctxt);
			case LAMBDA:
				dsadsa
			case LETBEST:
			case LETDEF:
			case MAP:
			case MAPLET:
			case MKBASIC:
			case MKTYPE:
			case MU:
			case NEW:
			case NIL:
			case NOTYETSPECIFIED:
			case POSTOP:
			case PRE:
			case PREOP:
			case QUOTELITERAL:
			case REALLITERAL:
			case SAMEBASECLASS:
			case SAMECLASS:
			case SELF:
			case SEQ:
			case SET:
			case STATEINIT:
			case STRINGLITERAL:
			case SUBCLASSRESPONSIBILITY:
			case SUBSEQ:
			case THREADID:
			case TIME:
			case TUPLE:
			case UNARY:
			case UNDEFINED:
			case VARIABLE:
			default:
				return new ValueList();  // Default, for expressions with no variables
		}
	}

	public static Object findExpression(PExp guard, int line)
	{
		//TODO: not implemented
		assert false : "not implemented";
		return null;
	}

	public static List<PExp> getSubExpressions(PExp guard)
	{
		//TODO: not implemented
		assert false : "not implemented";
		return null;
	}

	public static ValueList getValues(LinkedList<PExp> args, ObjectContext ctxt)
	{
		ValueList list = new ValueList();

		for (PExp exp: args)
		{
			list.addAll( getValues(exp,ctxt));
		}

		return list;
	}

}
