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
import org.overture.ast.lex.LexNameList;

public class PExpAssistantTC {

	public static String getPreName(PExp root) {
		String result = null;
		switch (root.kindPExp()) {
		case AFuncInstatiationExp.kindPExp: {
			AFuncInstatiationExp func = AFuncInstatiationExp.class.cast(root);
			result = getPreName(func.getFunction());
		}
			break;
		case AVariableExp.kindPExp: {
			AVariableExp var = AVariableExp.class.cast(root);

			PDefinition def = var.getVardef();
			
			//TODO: This will not work if the functions is renamed more than one time, can this occur??
			if (def instanceof ARenamedDefinition)
				def = ((ARenamedDefinition) def).getDef();
			else if (def instanceof AInheritedDefinition)
				def = ((AInheritedDefinition) def).getSuperdef();
			
			if (def instanceof AExplicitFunctionDefinition) {
				AExplicitFunctionDefinition ex = AExplicitFunctionDefinition.class
						.cast(def);
				PDefinition predef = ex.getPredef();
				result = predef == null ? "" : predef.getName().name;

			} else if (def instanceof AImplicitFunctionDefinition) {
				AImplicitFunctionDefinition im = AImplicitFunctionDefinition.class
						.cast(def);
				PDefinition predef = im.getPredef();
				result = predef == null ? "" : predef.getName().name;
			}
			break;
		}
		}
		return result;
	}

	public static LexNameList getOldNames(PExp expression) {
		
		switch (expression.kindPExp()) {					
		case AApplyExp.kindPExp:
			return AApplyExpAssistantTC.getOldNames((AApplyExp) expression);
		case SBinaryExp.kindPExp:
			return SBinaryExpAssistantTC.getOldNames((SBinaryExp) expression);
		case ACasesExp.kindPExp:
			return ACasesExpAssistantTC.getOldNames((ACasesExp) expression);		
		case AElseIfExp.kindPExp:
			return AElseIfExpAssistantTC.getOldNames((AElseIfExp) expression);
		case AExistsExp.kindPExp:
			return AExistsExpAssistantTC.getOldNameS((AExistsExp) expression);
		case AExists1Exp.kindPExp:
			return AExists1ExpAssistantTC.getOldNames((AExists1Exp) expression);
		case AFieldExp.kindPExp:
			return AFieldExpAssistantTC.getOldNames((AFieldExp) expression);
		case AFieldNumberExp.kindPExp:
			return AFieldNumberExpAssistantTC.getOldNames((AFieldNumberExp) expression);
		case AForAllExp.kindPExp:
			return AForAllExpAssistantTC.getOldNames((AForAllExp) expression);
		case AFuncInstatiationExp.kindPExp:
			return AFuncInstatiationExpAssistantTC.getOldNames((AFuncInstatiationExp) expression);
		case AIfExp.kindPExp:
			return AIfExpAssistantTC.getOldNames((AIfExp) expression);
		case AIotaExp.kindPExp:
			return AIotaExpAssistantTC.getOldNames((AIotaExp) expression);
		case AIsExp.kindPExp:
			return AIsExpAssistantTC.getOldNames((AIsExp) expression);
		case AIsOfBaseClassExp.kindPExp:
			return AIsOfBaseClassExpAssistantTC.getOldNames((AIsOfBaseClassExp) expression);
		case AIsOfClassExp.kindPExp:
			return AIsOfClassExpAssistantTC.getOldNames((AIsOfClassExp) expression);
		case ALambdaExp.kindPExp:
			return ALambdaExpAssistantTC.getOldNames((ALambdaExp) expression);
		case ALetBeStExp.kindPExp:
			return ALetBeStExpAssistantTC.getOldNames((ALetBeStExp) expression);
		case ALetDefExp.kindPExp:
			return ALetDefExpAssistantTC.getOldNames((ALetDefExp) expression);
		case SMapExp.kindPExp:
			return SMapExpAssistantTC.getOldNames((SMapExp) expression);
		case AMapletExp.kindPExp:
			return AMapletExpAssistantTC.getOldNames((AMapletExp) expression);
		case AMkBasicExp.kindPExp:
			return AMkBasicExpAssistantTC.getOldNames((AMkBasicExp) expression);
		case AMkTypeExp.kindPExp:
			return AMkTypeExpAssistantTC.getOldNames((AMkTypeExp) expression);
		case AMuExp.kindPExp:
			return AMuExpAssistantTC.getOldNames((AMuExp) expression);
		case ANarrowExp.kindPExp:
			return ANarrowExpAssistantTC.getOldNames((ANarrowExp) expression);
		case ANewExp.kindPExp:
			return ANewExpAssistantTC.getOldNames((ANewExp) expression);
		case APostOpExp.kindPExp:
			return APostOpExpAssistantTC.getOldNames((APostOpExp) expression);		
		case ASameBaseClassExp.kindPExp:
			return ASameBaseClassExpAssistantTC.getOldNames((ASameBaseClassExp) expression);
		case ASameClassExp.kindPExp:
			return ASameClassExpAssistantTC.getOldNames((ASameClassExp) expression);
		case SSeqExp.kindPExp:
			return SSeqExpAssistantTC.getOldNames((SSeqExp) expression);
		case SSetExp.kindPExp:
			return SSetExpAssistantTC.getOldNames((SSetExp) expression);
		case ASubseqExp.kindPExp:
			return ASubseqExpAssistantTC.getOldNames((ASubseqExp) expression);
		case ATupleExp.kindPExp:
			return ATupleExpAssistantTC.getOldNames((ATupleExp) expression);
		case SUnaryExp.kindPExp:
			return SUnaryExpAssistantTC.getOldNames((SUnaryExp)expression);
		case AVariableExp.kindPExp:
			return AVariableExpAssistantTC.getOldNames((AVariableExp) expression);
		default:
			return new LexNameList();
		}
	}

	public static LexNameList getOldNames(LinkedList<PExp> args) {
		LexNameList list = new LexNameList();

		for (PExp exp: args)
		{
			list.addAll(getOldNames(exp));
		}

		return list;
	}
}
