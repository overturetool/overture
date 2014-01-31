package org.overture.interpreter.assistant.expression;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
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
import org.overture.ast.lex.LexNameList;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.PExpAssistantTC;

public class PExpAssistantInterpreter extends PExpAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	/**
	 * Return a list of all the updatable values read by the expression. This is used to add listeners to values that
	 * affect permission guards, so that the guard may be efficiently re-evaluated when the values change.
	 * 
	 * @param ctxt
	 *            The context in which to search for values.
	 * @return A list of values read by the expression.
	 */
	public static ValueList getValues(PExp exp, ObjectContext ctxt)
	{
		if (exp instanceof AApplyExp)
		{
			return AApplyExpAssistantInterpreter.getValues((AApplyExp) exp, ctxt);
		} else if (exp instanceof SBinaryExp)
		{
			return SBinaryExpAssistantInterpreter.getValues((SBinaryExp) exp, ctxt);
		} else if (exp instanceof ACasesExp)
		{
			return ACasesExpAssistantInterpreter.getValues((ACasesExp) exp, ctxt);
		} else if (exp instanceof ADefExp)
		{
			return ADefExpAssistantInterpreter.getValues((ADefExp) exp, ctxt);
		} else if (exp instanceof AElseIfExp)
		{
			return AElseIfExpAssistantInterpreter.getValues((AElseIfExp) exp, ctxt);
		} else if (exp instanceof AExistsExp)
		{
			return AExistsExpAssistantInterpreter.getValues((AExistsExp) exp, ctxt);
		} else if (exp instanceof AExists1Exp)
		{
			return AExists1ExpAssistantInterpreter.getValues((AExists1Exp) exp, ctxt);
		} else if (exp instanceof AFieldExp)
		{
			return AFieldExpAssistantInterpreter.getValues((AFieldExp) exp, ctxt);
		} else if (exp instanceof AFieldNumberExp)
		{
			return AFieldNumberExpAssistantInterpreter.getValues((AFieldNumberExp) exp, ctxt);
		} else if (exp instanceof AForAllExp)
		{
			return AForAllExpAssistantInterpreter.getValues((AForAllExp) exp, ctxt);
		} else if (exp instanceof AFuncInstatiationExp)
		{
			return AFuncInstatiationExpAssistantInterpreter.getValues((AFuncInstatiationExp) exp, ctxt);
		} else if (exp instanceof AIfExp)
		{
			return AIfExpAssistantInterpreter.getValues((AIfExp) exp, ctxt);
		} else if (exp instanceof AIotaExp)
		{
			return AIotaExpAssistantInterpreter.getValues((AIotaExp) exp, ctxt);
		} else if (exp instanceof AIsExp)
		{
			return AIsExpAssistantInterpreter.getValues((AIsExp) exp, ctxt);
		} else if (exp instanceof AIsOfBaseClassExp)
		{
			return AIsOfBaseClassExpAssistantInterpreter.getValues((AIsOfBaseClassExp) exp, ctxt);
		} else if (exp instanceof AIsOfClassExp)
		{
			return AIsOfClassExpAssistantInterpreter.getValues((AIsOfClassExp) exp, ctxt);
		} else if (exp instanceof ALambdaExp)
		{
			return ALambdaExpAssistantInterpreter.getValues((ALambdaExp) exp, ctxt);
		} else if (exp instanceof ALetBeStExp)
		{
			return ALetBeStExpAssistantInterpreter.getValues((ALetBeStExp) exp, ctxt);
		} else if (exp instanceof ALetDefExp)
		{
			return ALetDefExpAssistantInterpreter.getValues((ALetDefExp) exp, ctxt);
		} else if (exp instanceof SMapExp)
		{
			return SMapExpAssistantInterpreter.getValues((SMapExp) exp, ctxt);
		} else if (exp instanceof AMapletExp)
		{
			return AMapletExpAssistantInterpreter.getValues((AMapletExp) exp, ctxt);
		} else if (exp instanceof AMkBasicExp)
		{
			return AMkBasicExpAssistantInterpreter.getValues((AMkBasicExp) exp, ctxt);
		} else if (exp instanceof AMkTypeExp)
		{
			return AMkTypeExpAssistantInterpreter.getValues((AMkTypeExp) exp, ctxt);
		} else if (exp instanceof AMuExp)
		{
			return AMuExpAssistantInterpreter.getValues((AMuExp) exp, ctxt);
		} else if (exp instanceof ANarrowExp)
		{
			return ANarrowExpAssistantInterpreter.getValues((ANarrowExp) exp, ctxt);
		} else if (exp instanceof ANewExp)
		{
			return ANewExpAssistantInterpreter.getValues((ANewExp) exp, ctxt);
		} else if (exp instanceof ASameBaseClassExp)
		{
			return ASameBaseClassExpAssistantInterpreter.getValues((ASameBaseClassExp) exp, ctxt);
		} else if (exp instanceof ASameClassExp)
		{
			return ASameClassExpAssistantInterpreter.getValues((ASameBaseClassExp) exp, ctxt);
		} else if (exp instanceof SSeqExp)
		{
			return SSeqExpAssistantInterpreter.getValues((SSeqExp) exp, ctxt);
		} else if (exp instanceof SSetExp)
		{
			return SSetExpAssistantInterpreter.getValues((SSetExp) exp, ctxt);
		} else if (exp instanceof ASubseqExp)
		{
			return ASubseqExpAssistantInterpreter.getValues((ASubseqExp) exp, ctxt);
		} else if (exp instanceof ATupleExp)
		{
			return ATupleExpAssistantInterpreter.getValues((ATupleExp) exp, ctxt);
		} else if (exp instanceof SUnaryExp)
		{
			return SUnaryExpAssistantInterpreter.getValues((SUnaryExp) exp, ctxt);
		} else if (exp instanceof AVariableExp)
		{
			return AVariableExpAssistantInterpreter.getVariable((AVariableExp) exp, ctxt);
		} else
		{
			return new ValueList(); // Default, for expressions with no variables
		}
	}

	/**
	 * Find an expression starting on the given line. Single expressions just compare their location to lineno, but
	 * expressions with sub-expressions iterate over their branches.
	 * 
	 * @param lineno
	 *            The line number to locate.
	 * @return An expression starting on the line, or null.
	 */
	public static PExp findExpression(PExp exp, int lineno)
	{
		if (exp instanceof AApplyExp)
		{
			return AApplyExpAssistantInterpreter.findExpression((AApplyExp) exp, lineno);
		} else if (exp instanceof SBinaryExp)
		{
			return SBinaryExpAssistantInterpreter.findExpression((SBinaryExp) exp, lineno);
		} else if (exp instanceof ACasesExp)
		{
			return ACasesExpAssistantInterpreter.findExpression((ACasesExp) exp, lineno);
		} else if (exp instanceof ADefExp)
		{
			return ADefExpAssistantInterpreter.findExpression((ADefExp) exp, lineno);
		} else if (exp instanceof AElseIfExp)
		{
			return AElseIfExpAssistantInterpreter.findExpression((AElseIfExp) exp, lineno);
		} else if (exp instanceof AExistsExp)
		{
			return AExistsExpAssistantInterpreter.findExpression((AExistsExp) exp, lineno);
		} else if (exp instanceof AExists1Exp)
		{
			return AExists1ExpAssistantInterpreter.findExpression((AExists1Exp) exp, lineno);
		} else if (exp instanceof AFieldExp)
		{
			return AFieldExpAssistantInterpreter.findExpression((AFieldExp) exp, lineno);
		} else if (exp instanceof AFieldNumberExp)
		{
			return AFieldNumberExpAssistantInterpreter.findExpression((AFieldNumberExp) exp, lineno);
		} else if (exp instanceof AForAllExp)
		{
			return AForAllExpAssistantInterpreter.findExpression((AForAllExp) exp, lineno);
		} else if (exp instanceof AFuncInstatiationExp)
		{
			return AFuncInstatiationExpAssistantInterpreter.findExpression((AFuncInstatiationExp) exp, lineno);
		} else if (exp instanceof AIfExp)
		{
			return AIfExpAssistantInterpreter.findExpression((AIfExp) exp, lineno);
		} else if (exp instanceof AIotaExp)
		{
			return AIotaExpAssistantInterpreter.findExpression((AIotaExp) exp, lineno);
		} else if (exp instanceof AIsExp)
		{
			return AIsExpAssistantInterpreter.findExpression((AIsExp) exp, lineno);
		} else if (exp instanceof AIsOfBaseClassExp)
		{
			return AIsOfBaseClassExpAssistantInterpreter.findExpression((AIsOfBaseClassExp) exp, lineno);
		} else if (exp instanceof AIsOfClassExp)
		{
			return AIsOfClassExpAssistantInterpreter.findExpression((AIsOfClassExp) exp, lineno);
		} else if (exp instanceof ALambdaExp)
		{
			return ALambdaExpAssistantInterpreter.findExpression((ALambdaExp) exp, lineno);
		} else if (exp instanceof ALetBeStExp)
		{
			return ALetBeStExpAssistantInterpreter.findExpression((ALetBeStExp) exp, lineno);
		} else if (exp instanceof ALetDefExp)
		{
			return ALetDefExpAssistantInterpreter.findExpression((ALetDefExp) exp, lineno);
		} else if (exp instanceof SMapExp)
		{
			return SMapExpAssistantInterpreter.findExpression((SMapExp) exp, lineno);
		} else if (exp instanceof AMapletExp)
		{
			return AMapletExpAssistantInterpreter.findExpression((AMapletExp) exp, lineno);
		} else if (exp instanceof AMkBasicExp)
		{
			return AMkBasicExpAssistantInterpreter.findExpression((AMkBasicExp) exp, lineno);
		} else if (exp instanceof AMkTypeExp)
		{
			return AMkTypeExpAssistantInterpreter.findExpression((AMkTypeExp) exp, lineno);
		} else if (exp instanceof AMuExp)
		{
			return AMuExpAssistantInterpreter.findExpression((AMuExp) exp, lineno);
		} else if (exp instanceof ANarrowExp)
		{
			return ANarrowExpAssistantInterpreter.findExpression((ANarrowExp) exp, lineno);
		} else if (exp instanceof ANewExp)
		{
			return ANewExpAssistantInterpreter.findExpression((ANewExp) exp, lineno);
		} else if (exp instanceof APostOpExp)
		{
			return APostOpExpAssistantInterpreter.findExpression((APostOpExp) exp, lineno);
		} else if (exp instanceof ASameBaseClassExp)
		{
			return ASameBaseClassExpAssistantInterpreter.findExpression((ASameBaseClassExp) exp, lineno);
		} else if (exp instanceof ASameClassExp)
		{
			return ASameClassExpAssistantInterpreter.findExpression((ASameClassExp) exp, lineno);
		} else if (exp instanceof SSeqExp)
		{
			return SSeqExpAssistantInterpreter.findExpression((SSeqExp) exp, lineno);
		} else if (exp instanceof SSetExp)
		{
			return SSetExpAssistantInterpreter.findExpression((SSetExp) exp, lineno);
		} else if (exp instanceof ASubseqExp)
		{
			return ASubseqExpAssistantInterpreter.findExpression((ASubseqExp) exp, lineno);
		} else if (exp instanceof ATupleExp)
		{
			return ATupleExpAssistantInterpreter.findExpression((ATupleExp) exp, lineno);
		} else if (exp instanceof SUnaryExp)
		{
			return SUnaryExpAssistantInterpreter.findExpression((SUnaryExp) exp, lineno);
		} else
		{
			return findExpressionBaseCase(exp, lineno);
		}
	}

	public static PExp findExpressionBaseCase(PExp exp, int lineno)
	{
		return (exp.getLocation().getStartLine() == lineno) ? exp : null;
	}

	public static List<PExp> getSubExpressions(PExp exp)
	{
		if (exp instanceof AApplyExp)
		{
			return AApplyExpAssistantInterpreter.getSubExpressions((AApplyExp) exp);
		} else if (exp instanceof SBinaryExp)
		{
			return SBinaryExpAssistantInterpreter.getSubExpressions((SBinaryExp) exp);
		} else if (exp instanceof ACasesExp)
		{
			return ACasesExpAssistantInterpreter.getSubExpressions((ACasesExp) exp);
		} else if (exp instanceof AElseIfExp)
		{
			return AElseIfExpAssistantInterpreter.getSubExpressions((AElseIfExp) exp);
		} else if (exp instanceof AIfExp)
		{
			return AIfExpAssistantInterpreter.getSubExpressions((AIfExp) exp);
		} else
		{
			List<PExp> subs = new Vector<PExp>();
			subs.add(exp);
			return subs;
		}

	}

	public static ValueList getValues(LinkedList<PExp> args, ObjectContext ctxt)
	{
		ValueList list = new ValueList();

		for (PExp exp : args)
		{
			list.addAll(getValues(exp, ctxt));
		}

		return list;
	}

	public static PExp findExpression(LinkedList<PExp> args, int lineno)
	{
		for (PExp exp : args)
		{
			PExp found = findExpression(exp, lineno);
			if (found != null)
				return found;
		}

		return null;
	}

	public   LexNameList getOldNames(PExp expression)
	{
		try
		{
			return expression.apply(af.getOldNameCollector());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return new LexNameList();
		}
	}

	public  LexNameList getOldNames(LinkedList<PExp> args)
	{
		LexNameList list = new LexNameList();

		for (PExp exp : args)
		{
			list.addAll(getOldNames(exp));
		}

		return list;
	}

}
