package org.overture.typechecker.assistant.expression;

import java.util.LinkedList;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.PDefinition;
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
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
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

	public static ILexNameToken getPreName(PExp expression) {
		ILexNameToken result = null;
		
		if (expression instanceof AFuncInstatiationExp) {
			AFuncInstatiationExp func = AFuncInstatiationExp.class.cast(expression);
			result = getPreName(func.getFunction());
		} else if (expression instanceof AVariableExp) {
			AVariableExp var = AVariableExp.class.cast(expression);
                        PDefinition def = PDefinitionAssistantTC.deref(var.getVardef());

			//TODO: This will not work if the functions is renamed more than one time, can this occur??
			// if (def instanceof ARenamedDefinition)
			// 	def = ((ARenamedDefinition) def).getDef();
			// else if (def instanceof AInheritedDefinition)
			// 	def = ((AInheritedDefinition) def).getSuperdef();
                        if (def instanceof AExplicitFunctionDefinition)
                        {
                            AExplicitFunctionDefinition ex = AExplicitFunctionDefinition.class.cast(def);
                            PDefinition predef = ex.getPredef();
                            result = predef == null ? NO_PRECONDITION : predef.getName();

                        } else if (def instanceof AImplicitFunctionDefinition)
                        {
                            AImplicitFunctionDefinition im = AImplicitFunctionDefinition.class.cast(def);
                            PDefinition predef = im.getPredef();
                            result = predef == null ? NO_PRECONDITION
                                : predef.getName();
                        }
		}
		return result;
	}

	public static LexNameList getOldNames(PExp expression) {
		if (expression instanceof AApplyExp) {
			return AApplyExpAssistantTC.getOldNames((AApplyExp) expression);
		} else if (expression instanceof SBinaryExp) {
			return SBinaryExpAssistantTC.getOldNames((SBinaryExp) expression);
		} else if (expression instanceof ACasesExp) {
			return ACasesExpAssistantTC.getOldNames((ACasesExp) expression);
		} else if (expression instanceof AElseIfExp) {
			return AElseIfExpAssistantTC.getOldNames((AElseIfExp) expression);
		} else if (expression instanceof AExistsExp) {
			return AExistsExpAssistantTC.getOldNameS((AExistsExp) expression);
		} else if (expression instanceof AExists1Exp) {
			return AExists1ExpAssistantTC.getOldNames((AExists1Exp) expression);
		} else if (expression instanceof AFieldExp) {
			return AFieldExpAssistantTC.getOldNames((AFieldExp) expression);
		} else if (expression instanceof AFieldNumberExp) {
			return AFieldNumberExpAssistantTC.getOldNames((AFieldNumberExp) expression);
		} else if (expression instanceof AForAllExp) {
			return AForAllExpAssistantTC.getOldNames((AForAllExp) expression);
		} else if (expression instanceof AFuncInstatiationExp) {
			return AFuncInstatiationExpAssistantTC.getOldNames((AFuncInstatiationExp) expression);
		} else if (expression instanceof AIfExp) {
			return AIfExpAssistantTC.getOldNames((AIfExp) expression);
		} else if (expression instanceof AIotaExp) {
			return AIotaExpAssistantTC.getOldNames((AIotaExp) expression);
		} else if (expression instanceof AIsExp) {
			return AIsExpAssistantTC.getOldNames((AIsExp) expression);
		} else if (expression instanceof AIsOfBaseClassExp) {
			return AIsOfBaseClassExpAssistantTC.getOldNames((AIsOfBaseClassExp) expression);
		} else if (expression instanceof AIsOfClassExp) {
			return AIsOfClassExpAssistantTC.getOldNames((AIsOfClassExp) expression);
		} else if (expression instanceof ALambdaExp) {
			return ALambdaExpAssistantTC.getOldNames((ALambdaExp) expression);
		} else if (expression instanceof ALetBeStExp) {
			return ALetBeStExpAssistantTC.getOldNames((ALetBeStExp) expression);
		} else if (expression instanceof ALetDefExp) {
			return ALetDefExpAssistantTC.getOldNames((ALetDefExp) expression);
		} else if (expression instanceof SMapExp) {
			return SMapExpAssistantTC.getOldNames((SMapExp) expression);
		} else if (expression instanceof AMapletExp) {
			return AMapletExpAssistantTC.getOldNames((AMapletExp) expression);
		} else if (expression instanceof AMkBasicExp) {
			return AMkBasicExpAssistantTC.getOldNames((AMkBasicExp) expression);
		} else if (expression instanceof AMkTypeExp) {
			return AMkTypeExpAssistantTC.getOldNames((AMkTypeExp) expression);
		} else if (expression instanceof AMuExp) {
			return AMuExpAssistantTC.getOldNames((AMuExp) expression);
		} else if (expression instanceof ANarrowExp) {
			return ANarrowExpAssistantTC.getOldNames((ANarrowExp) expression);
		} else if (expression instanceof ANewExp) {
			return ANewExpAssistantTC.getOldNames((ANewExp) expression);
		} else if (expression instanceof APostOpExp) {
			return APostOpExpAssistantTC.getOldNames((APostOpExp) expression);
		} else if (expression instanceof ASameBaseClassExp) {
			return ASameBaseClassExpAssistantTC.getOldNames((ASameBaseClassExp) expression);
		} else if (expression instanceof ASameClassExp) {
			return ASameClassExpAssistantTC.getOldNames((ASameClassExp) expression);
		} else if (expression instanceof SSeqExp) {
			return SSeqExpAssistantTC.getOldNames((SSeqExp) expression);
		} else if (expression instanceof SSetExp) {
			return SSetExpAssistantTC.getOldNames((SSetExp) expression);
		} else if (expression instanceof ASubseqExp) {
			return ASubseqExpAssistantTC.getOldNames((ASubseqExp) expression);
		} else if (expression instanceof ATupleExp) {
			return ATupleExpAssistantTC.getOldNames((ATupleExp) expression);
		} else if (expression instanceof SUnaryExp) {
			return SUnaryExpAssistantTC.getOldNames((SUnaryExp)expression);
		} else if (expression instanceof AVariableExp) {
			return AVariableExpAssistantTC.getOldNames((AVariableExp) expression);
		} else {
			return new LexNameList();
		}
	}

	public static LexNameList getOldNames(LinkedList<PExp> args)
	{
		LexNameList list = new LexNameList();

		for (PExp exp : args)
		{
			list.addAll(getOldNames(exp));
		}

		return list;
	}
}
