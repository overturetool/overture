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
import org.overture.ast.expressions.ANarrowExp;
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
			case AApplyExp.kindPExp:
				return AApplyExpAssistantInterpreter.getValues((AApplyExp)exp,ctxt);
			case SBinaryExp.kindPExp:
				return SBinaryExpAssistantInterpreter.getValues((SBinaryExp)exp,ctxt);
			case ACasesExp.kindPExp:
				return ACasesExpAssistantInterpreter.getValues((ACasesExp)exp,ctxt);
			case ADefExp.kindPExp:
				return ADefExpAssistantInterpreter.getValues((ADefExp)exp,ctxt);
			case AElseIfExp.kindPExp:
				return AElseIfExpAssistantInterpreter.getValues((AElseIfExp)exp,ctxt);
			case AExistsExp.kindPExp:
				return AExistsExpAssistantInterpreter.getValues((AExistsExp)exp,ctxt);
			case AExists1Exp.kindPExp:
				return AExists1ExpAssistantInterpreter.getValues((AExists1Exp)exp,ctxt);
			case AFieldExp.kindPExp:
				return AFieldExpAssistantInterpreter.getValues((AFieldExp)exp,ctxt);
			case AFieldNumberExp.kindPExp:
				return AFieldNumberExpAssistantInterpreter.getValues((AFieldNumberExp)exp,ctxt);
			case AForAllExp.kindPExp:
				return AForAllExpAssistantInterpreter.getValues((AForAllExp)exp,ctxt);
			case AFuncInstatiationExp.kindPExp:
				return AFuncInstatiationExpAssistantInterpreter.getValues((AFuncInstatiationExp)exp,ctxt);
			case AIfExp.kindPExp:
				return AIfExpAssistantInterpreter.getValues((AIfExp)exp,ctxt);
			case AIotaExp.kindPExp:
				return AIotaExpAssistantInterpreter.getValues((AIotaExp) exp,ctxt);
			case AIsExp.kindPExp:
				return AIsExpAssistantInterpreter.getValues((AIsExp)exp,ctxt);
			case AIsOfBaseClassExp.kindPExp:
				return AIsOfBaseClassExpAssistantInterpreter.getValues((AIsOfBaseClassExp)exp,ctxt);
			case AIsOfClassExp.kindPExp:
				return AIsOfClassExpAssistantInterpreter.getValues((AIsOfClassExp)exp,ctxt);
			case ALambdaExp.kindPExp:
				return ALambdaExpAssistantInterpreter.getValues((ALambdaExp)exp,ctxt);
			case ALetBeStExp.kindPExp:
				return ALetBeStExpAssistantInterpreter.getValues((ALetBeStExp)exp,ctxt);
			case ALetDefExp.kindPExp:
				return ALetDefExpAssistantInterpreter.getValues((ALetDefExp)exp,ctxt);
			case SMapExp.kindPExp:
				return SMapExpAssistantInterpreter.getValues((SMapExp)exp,ctxt);
			case AMapletExp.kindPExp:
				return AMapletExpAssistantInterpreter.getValues((AMapletExp)exp,ctxt);
			case AMkBasicExp.kindPExp:
				return AMkBasicExpAssistantInterpreter.getValues((AMkBasicExp)exp,ctxt);
			case AMkTypeExp.kindPExp:
				return AMkTypeExpAssistantInterpreter.getValues((AMkTypeExp)exp,ctxt);
			case AMuExp.kindPExp:
				return AMuExpAssistantInterpreter.getValues((AMuExp)exp,ctxt);
			case ANarrowExp.kindPExp:
				return ANarrowExpAssistantInterpreter.getValues((ANarrowExp) exp, ctxt);
			case ANewExp.kindPExp:
				return ANewExpAssistantInterpreter.getValues((ANewExp)exp,ctxt);
			case ASameBaseClassExp.kindPExp:
				return ASameBaseClassExpAssistantInterpreter.getValues((ASameBaseClassExp)exp,ctxt);
			case ASameClassExp.kindPExp:
				return ASameClassExpAssistantInterpreter.getValues((ASameBaseClassExp)exp,ctxt);
			case SSeqExp.kindPExp:
				return SSeqExpAssistantInterpreter.getValues((SSeqExp)exp,ctxt);
			case SSetExp.kindPExp:
				return SSetExpAssistantInterpreter.getValues((SSetExp)exp,ctxt);
			case ASubseqExp.kindPExp:
				return ASubseqExpAssistantInterpreter.getValues((ASubseqExp)exp,ctxt);
			case ATupleExp.kindPExp:
				return ATupleExpAssistantInterpreter.getValues((ATupleExp)exp,ctxt);
			case SUnaryExp.kindPExp:
				return SUnaryExpAssistantInterpreter.getValues((SUnaryExp)exp,ctxt);
			case AVariableExp.kindPExp:
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
			case AApplyExp.kindPExp:
				return AApplyExpAssistantInterpreter.findExpression((AApplyExp)exp,lineno);
			case SBinaryExp.kindPExp:
				return SBinaryExpAssistantInterpreter.findExpression((SBinaryExp)exp,lineno);
			case ACasesExp.kindPExp:
				return ACasesExpAssistantInterpreter.findExpression((ACasesExp)exp,lineno);
			case ADefExp.kindPExp:
				return ADefExpAssistantInterpreter.findExpression((ADefExp)exp,lineno);
			case AElseIfExp.kindPExp:
				return AElseIfExpAssistantInterpreter.findExpression((AElseIfExp)exp,lineno);
			case AExistsExp.kindPExp:
				return AExistsExpAssistantInterpreter.findExpression((AExistsExp)exp,lineno);
			case AExists1Exp.kindPExp:
				return AExists1ExpAssistantInterpreter.findExpression((AExists1Exp)exp,lineno);
			case AFieldExp.kindPExp:
				return AFieldExpAssistantInterpreter.findExpression((AFieldExp)exp,lineno);
			case AFieldNumberExp.kindPExp:
				return AFieldNumberExpAssistantInterpreter.findExpression((AFieldNumberExp)exp,lineno);
			case AForAllExp.kindPExp:
				return AForAllExpAssistantInterpreter.findExpression((AForAllExp)exp,lineno);
			case AFuncInstatiationExp.kindPExp:
				return AFuncInstatiationExpAssistantInterpreter.findExpression((AFuncInstatiationExp)exp,lineno);
			case AIfExp.kindPExp:
				return AIfExpAssistantInterpreter.findExpression((AIfExp)exp,lineno);
			case AIotaExp.kindPExp:
				return AIotaExpAssistantInterpreter.findExpression((AIotaExp)exp,lineno);
			case AIsExp.kindPExp:
				return AIsExpAssistantInterpreter.findExpression((AIsExp)exp,lineno);
			case AIsOfBaseClassExp.kindPExp:
				return AIsOfBaseClassExpAssistantInterpreter.findExpression((AIsOfBaseClassExp)exp,lineno);
			case AIsOfClassExp.kindPExp:
				return AIsOfClassExpAssistantInterpreter.findExpression((AIsOfClassExp)exp,lineno);
			case ALambdaExp.kindPExp:
				return ALambdaExpAssistantInterpreter.findExpression((ALambdaExp)exp,lineno);
			case ALetBeStExp.kindPExp:
				return ALetBeStExpAssistantInterpreter.findExpression((ALetBeStExp)exp,lineno);
			case ALetDefExp.kindPExp:
				return ALetDefExpAssistantInterpreter.findExpression((ALetDefExp)exp,lineno);
			case SMapExp.kindPExp:
				return SMapExpAssistantInterpreter.findExpression((SMapExp)exp,lineno);
			case AMapletExp.kindPExp:
				return AMapletExpAssistantInterpreter.findExpression((AMapletExp)exp, lineno);
			case AMkBasicExp.kindPExp:
				return AMkBasicExpAssistantInterpreter.findExpression((AMkBasicExp)exp,lineno);
			case AMkTypeExp.kindPExp:
				return AMkTypeExpAssistantInterpreter.findExpression((AMkTypeExp)exp,lineno);
			case AMuExp.kindPExp:
				return AMuExpAssistantInterpreter.findExpression((AMuExp)exp,lineno);
			case ANarrowExp.kindPExp:
				return ANarrowExpAssistantInterpreter.findExpression((ANarrowExp) exp, lineno);
			case ANewExp.kindPExp:
				return ANewExpAssistantInterpreter.findExpression((ANewExp)exp,lineno);
			case APostOpExp.kindPExp:
				return APostOpExpAssistantInterpreter.findExpression((APostOpExp)exp,lineno);
			case ASameBaseClassExp.kindPExp:
				return ASameBaseClassExpAssistantInterpreter.findExpression((ASameBaseClassExp)exp,lineno);
			case ASameClassExp.kindPExp:
				return ASameClassExpAssistantInterpreter.findExpression((ASameClassExp)exp,lineno);
			case SSeqExp.kindPExp:
				return SSeqExpAssistantInterpreter.findExpression((SSeqExp)exp,lineno);
			case SSetExp.kindPExp:
				return SSetExpAssistantInterpreter.findExpression((SSetExp)exp,lineno);
			case ASubseqExp.kindPExp:
				return ASubseqExpAssistantInterpreter.findExpression((ASubseqExp)exp,lineno);
			case ATupleExp.kindPExp:
				return ATupleExpAssistantInterpreter.findExpression((ATupleExp)exp,lineno);
			case SUnaryExp.kindPExp:
				return SUnaryExpAssistantInterpreter.findExpression((SUnaryExp)exp,lineno);
			default:
				return findExpressionBaseCase(exp, lineno);
		}
	}
	
	public static PExp findExpressionBaseCase(PExp exp, int lineno)
	{
		return (exp.getLocation().getStartLine() == lineno) ? exp : null;
	}

	public static List<PExp> getSubExpressions(PExp exp)
	{
		switch (exp.kindPExp())
		{
			case AApplyExp.kindPExp:
				return AApplyExpAssistantInterpreter.getSubExpressions((AApplyExp)exp);
			case SBinaryExp.kindPExp:
				return SBinaryExpAssistantInterpreter.getSubExpressions((SBinaryExp)exp);
			case ACasesExp.kindPExp:
				return ACasesExpAssistantInterpreter.getSubExpressions((ACasesExp)exp);
			case AElseIfExp.kindPExp:
				return AElseIfExpAssistantInterpreter.getSubExpressions((AElseIfExp)exp);			
			case AIfExp.kindPExp:
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
