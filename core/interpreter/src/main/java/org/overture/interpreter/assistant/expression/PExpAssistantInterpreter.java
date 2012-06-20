package org.overture.interpreter.assistant.expression;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.ASameBaseClassExp;
import org.overture.ast.expressions.ASameClassExp;
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

	/**
	 * Find an expression starting on the given line. Single expressions just
	 * compare their location to lineno, but expressions with sub-expressions
	 * iterate over their branches.
	 *
	 * @param lineno The line number to locate.
	 * @return An expression starting on the line, or null.
	 */
	public static PExp findExpression(PExp exp, int lineno)
	{
		switch (exp.kindPExp())
		{
			case APPLY:
				return AApplyExpAssistantInterpreter.findExpression((AApplyExp)exp,lineno);
			case BINARY:
				return SBinaryExpAssistantInterpreter.findExpression((SBinaryExp)exp,lineno);
			case CASES:
				return ACasesExpAssistantInterpreter.findExpression((ACasesExp)exp,lineno);
			case DEF:
				return ADefExpAssistantInterpreter.findExpression((ADefExp)exp,lineno);
			case ELSEIF:
				return AElseIfExpAssistantInterpreter.findExpression((AElseIfExp)exp,lineno);
			case EXISTS:
				return AExistsExpAssistantInterpreter.findExpression((AExistsExp)exp,lineno);
			case EXISTS1:
				return AExists1ExpAssistantInterpreter.findExpression((AExists1Exp)exp,lineno);
			case FIELD:
				return AFieldExpAssistantInterpreter.findExpression((AFieldExp)exp,lineno);
			case FIELDNUMBER:
				return AFieldNumberExpAssistantInterpreter.findExpression((AFieldNumberExp)exp,lineno);
			case FORALL:
				return AForAllExpAssistantInterpreter.findExpression((AForAllExp)exp,lineno);
			case FUNCINSTATIATION:
				return AFuncInstatiationExpAssistantInterpreter.findExpression((AFuncInstatiationExp)exp,lineno);
			case IF:
				return AIfExpAssistantInterpreter.findExpression((AIfExp)exp,lineno);
			case IOTA:
				return AIotaExpAssistantInterpreter.findExpression((AIotaExp)exp,lineno);
			case IS:
				return AIsExpAssistantInterpreter.findExpression((AIsExp)exp,lineno);
			case ISOFBASECLASS:
				return AIsOfBaseClassExpAssistantInterpreter.findExpression((AIsOfBaseClassExp)exp,lineno);
			case ISOFCLASS:
				return AIsOfClassExpAssistantInterpreter.findExpression((AIsOfClassExp)exp,lineno);
			case LAMBDA:
				return ALambdaExpAssistantInterpreter.findExpression((ALambdaExp)exp,lineno);
			case LETBEST:
				return ALetBeStExpAssistantInterpreter.findExpression((ALetBeStExp)exp,lineno);
			case LETDEF:
				return ALetDefExpAssistantInterpreter.findExpression((ALetDefExp)exp,lineno);
			case MAP:
				return SMapExpAssistantInterpreter.findExpression((SMapExp)exp,lineno);
			case MAPLET:
				return AMapletExpAssistantInterpreter.findExpression((AMapletExp)exp, lineno);
			case MKBASIC:
				return AMkBasicExpAssistantInterpreter.findExpression((AMkBasicExp)exp,lineno);
			case MKTYPE:
				return AMkTypeExpAssistantInterpreter.findExpression((AMkTypeExp)exp,lineno);
			case MU:
				return AMuExpAssistantInterpreter.findExpression((AMuExp)exp,lineno);
			case NEW:
				return ANewExpAssistantInterpreter.findExpression((ANewExp)exp,lineno);
			case POSTOP:
				return APostOpExpAssistantInterpreter.findExpression((APostOpExp)exp,lineno);
			case SAMEBASECLASS:
				return ASameBaseClassExpAssistantInterpreter.findExpression((ASameBaseClassExp)exp,lineno);
			case SAMECLASS:
				return ASameClassExpAssistantInterpreter.findExpression((ASameClassExp)exp,lineno);
			case SEQ:
				return SSeqExpAssistantInterpreter.findExpression((SSeqExp)exp,lineno);
			case SET:
				return SSetExpAssistantInterpreter.findExpression((SSetExp)exp,lineno);
			case SUBSEQ:
				return ASubseqExpAssistantInterpreter.findExpression((ASubseqExp)exp,lineno);
			case TUPLE:
				return ATupleExpAssistantInterpreter.findExpression((ATupleExp)exp,lineno);
			case UNARY:
				return SUnaryExpAssistantInterpreter.findExpression((SUnaryExp)exp,lineno);
			default:
				return findExpressionBaseCase(exp, lineno);
		}
	}
	
	public static PExp findExpressionBaseCase(PExp exp, int lineno)
	{
		return (exp.getLocation().startLine == lineno) ? exp : null;
	}

	public static List<PExp> getSubExpressions(PExp exp)
	{
		switch (exp.kindPExp())
		{
			case APPLY:
				return AApplyExpAssistantInterpreter.getSubExpressions((AApplyExp)exp);
			case BINARY:
				return SBinaryExpAssistantInterpreter.getSubExpressions((SBinaryExp)exp);
			case CASES:
				return ACasesExpAssistantInterpreter.getSubExpressions((ACasesExp)exp);
			case ELSEIF:
				return AElseIfExpAssistantInterpreter.getSubExpressions((AElseIfExp)exp);			
			case IF:
				return AIfExpAssistantInterpreter.getSubExpressions((AIfExp)exp);
			default:
				List<PExp> subs = new Vector<PExp>();
				subs.add(exp);
				return subs;
		}
		
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

	public static PExp findExpression(LinkedList<PExp> args, int lineno)
	{
		for (PExp exp: args)
		{
			PExp found = findExpression(exp,lineno);
			if (found != null) return found;
		}

		return null;
	}

}
