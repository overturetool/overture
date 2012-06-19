package org.overture.interpreter.assistant.expression;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.ADefExp;
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
import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.AMuExp;
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.ASameBaseClassExp;
import org.overture.ast.expressions.ASubseqExp;
import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.expressions.SMapExp;
import org.overture.ast.expressions.SSeqExp;
import org.overture.ast.expressions.SSetExp;
import org.overture.ast.expressions.SUnaryExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.PExpAssistantTC;

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
				return ADefExpAssistantInterpreter.getValues((ADefExp)exp,ctxt);
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
				return ALambdaExpAssistantInterpreter.getValues((ALambdaExp)exp,ctxt);
			case LETBEST:
				return ALetBeStExpAssistantInterpreter.getValues((ALetBeStExp)exp,ctxt);
			case LETDEF:
				return ALetDefExpAssistantInterpreter.getValues((ALetDefExp)exp,ctxt);
			case MAP:
				return SMapExpAssistantInterpreter.getValues((SMapExp)exp,ctxt);
			case MAPLET:
				return AMapletExpAssistantInterpreter.getValues((AMapletExp)exp,ctxt);
			case MKBASIC:
				return AMkBasicExpAssistantInterpreter.getValues((AMkBasicExp)exp,ctxt);
			case MKTYPE:
				return AMkTypeExpAssistantInterpreter.getValues((AMkTypeExp)exp,ctxt);
			case MU:
				return AMuExpAssistantInterpreter.getValues((AMuExp)exp,ctxt);
			case NEW:
				return ANewExpAssistantInterpreter.getValues((ANewExp)exp,ctxt);
			case SAMEBASECLASS:
				return ASameBaseClassExpAssistantInterpreter.getValues((ASameBaseClassExp)exp,ctxt);
			case SAMECLASS:
				return ASameClassExpAssistantInterpreter.getValues((ASameBaseClassExp)exp,ctxt);
			case SEQ:
				return SSeqExpAssistantInterpreter.getValues((SSeqExp)exp,ctxt);
			case SET:
				return SSetExpAssistantInterpreter.getValues((SSetExp)exp,ctxt);
			case SUBSEQ:
				return ASubseqExpAssistantInterpreter.getValues((ASubseqExp)exp,ctxt);
			case TUPLE:
				return ATupleExpAssistantInterpreter.getValues((ATupleExp)exp,ctxt);
			case UNARY:
				return SUnaryExpAssistantInterpreter.getValues((SUnaryExp)exp,ctxt);
			case VARIABLE:
				return AVariableExpAssistantInterpreter.getVariable((AVariableExp)exp,ctxt);
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
