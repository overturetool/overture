package org.overture.interpreter.utilities.expression;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ACaseAlternative;
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
import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.AMuExp;
import org.overture.ast.expressions.ANarrowExp;
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.ASameBaseClassExp;
import org.overture.ast.expressions.ASameClassExp;
import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.ASetRangeSetExp;
import org.overture.ast.expressions.ASubseqExp;
import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.expressions.SMapExp;
import org.overture.ast.expressions.SSeqExp;
import org.overture.ast.expressions.SSetExp;
import org.overture.ast.expressions.SUnaryExp;
import org.overture.ast.node.INode;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

/***************************************
 * 
 * This method find an expression in an expression. //that's what i think it does, check to change to correct form if needed.
 * 
 * @author gkanos
 *
 ****************************************/

public class ExpExpressionFinder extends QuestionAnswerAdaptor<Integer,PExp>
{
	protected IInterpreterAssistantFactory af;
	
	public ExpExpressionFinder(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public PExp caseAApplyExp(AApplyExp exp, Integer lineno)
			throws AnalysisException
	{
		//return AApplyExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		found = exp.getRoot().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getRoot(), lineno);
		if (found != null)
			return found;

		return af.createPExpAssistant().findExpression(exp.getArgs(), lineno);
	}
	
	@Override
	public PExp defaultSBinaryExp(SBinaryExp exp, Integer lineno)
			throws AnalysisException
	{
		//return SBinaryExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = exp.getLeft().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getLeft(), lineno);
		if (found != null)
			return found;

		return exp.getRight().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getRight(), lineno);
	}
	
	@Override
	public PExp caseACasesExp(ACasesExp exp, Integer lineno)
			throws AnalysisException
	{
		//return ACasesExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		found = exp.getExpression().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getExpression(), lineno);
		if (found != null)
			return found;

		for (ACaseAlternative c : exp.getCases())
		{
			found = c.getResult().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(c.getResult(), lineno);
			if (found != null)
				break;
		}

		return found != null ? found
				: exp.getOthers() != null ? exp.getOthers().apply(THIS, lineno)//PExpAssistantInterpreter.findExpression(exp.getOthers(), lineno)
						: null;
	}
	
	@Override
	public PExp caseADefExp(ADefExp exp, Integer lineno)
			throws AnalysisException
	{
		//return ADefExpAssistantInterpreter.findExpression(exp, lineno);
		
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		found = af.createPDefinitionListAssistant().findExpression(exp.getLocalDefs(), lineno);
		if (found != null)
			return found;

		return exp.getExpression().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getExpression(), lineno);
	}
	
	@Override
	public PExp caseAElseIfExp(AElseIfExp exp, Integer lineno)
			throws AnalysisException
	{
		//return AElseIfExpAssistantInterpreter.findExpression((AElseIfExp) exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return exp.getThen().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getThen(), lineno);
	}
	
	@Override
	public PExp caseAExistsExp(AExistsExp exp, Integer lineno)
			throws AnalysisException
	{
		//return AExistsExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return exp.getPredicate().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getPredicate(), lineno);
	}
	
	@Override
	public PExp caseAExists1Exp(AExists1Exp exp, Integer lineno)
			throws AnalysisException
	{
		//return AExists1ExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return exp.getPredicate().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getPredicate(), lineno);
	}
	
	@Override
	public PExp caseAFieldExp(AFieldExp exp, Integer lineno)
			throws AnalysisException
	{
		//return AFieldExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return exp.getObject().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getObject(), lineno);
	}
	
	@Override
	public PExp caseAFieldNumberExp(AFieldNumberExp exp, Integer lineno)
			throws AnalysisException
	{
		//return AFieldNumberExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return exp.getTuple().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getTuple(), lineno);
	}
	
	@Override
	public PExp caseAForAllExp(AForAllExp exp, Integer lineno)
			throws AnalysisException
	{
		//return AForAllExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return exp.getPredicate().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getPredicate(), lineno);
	}
	
	@Override
	public PExp caseAFuncInstatiationExp(AFuncInstatiationExp exp,
			Integer lineno) throws AnalysisException
	{
		//return AFuncInstatiationExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return exp.getFunction().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getFunction(), lineno);
	}
	
	@Override
	public PExp caseAIfExp(AIfExp exp, Integer lineno)
			throws AnalysisException
	{
		//return AIfExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;
		found = exp.getTest().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getTest(), lineno);
		if (found != null)
			return found;
		found = exp.getThen().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getThen(), lineno);
		if (found != null)
			return found;

		for (AElseIfExp stmt : exp.getElseList())
		{
			found = stmt.apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(stmt, lineno);
			if (found != null)
				return found;
		}

		if (exp.getElse() != null)
		{
			found = exp.getElse().apply(THIS, lineno); //PExpAssistantInterpreter.findExpression(exp.getElse(), lineno);
		}

		return found;
	}
	
	@Override
	public PExp caseAIotaExp(AIotaExp exp, Integer lineno)
			throws AnalysisException
	{
		//return AIotaExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return exp.getPredicate().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getPredicate(), lineno);
	}
	
	@Override
	public PExp caseAIsExp(AIsExp exp, Integer lineno)
			throws AnalysisException
	{
		//return AIsExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return  exp.getTest().apply(THIS, lineno); //PExpAssistantInterpreter.findExpression(exp.getTest(), lineno);
	}
	
	@Override
	public PExp caseAIsOfBaseClassExp(AIsOfBaseClassExp exp, Integer lineno)
			throws AnalysisException
	{
		//return AIsOfBaseClassExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return exp.getExp().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getExp(), lineno);
	}
	
	@Override
	public PExp caseAIsOfClassExp(AIsOfClassExp exp, Integer lineno)
			throws AnalysisException
	{
		//return AIsOfClassExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return exp.getExp().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getExp(), lineno);
	}
	
	@Override
	public PExp caseALambdaExp(ALambdaExp exp, Integer lineno)
			throws AnalysisException
	{
		//return ALambdaExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return exp.getExpression().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getExpression(), lineno);
	}

	@Override
	public PExp caseALetBeStExp(ALetBeStExp exp, Integer lineno)
			throws AnalysisException
	{
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		if (exp.getSuchThat() != null)
		{
			found = exp.getSuchThat().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getSuchThat(), lineno);
			if (found != null)
				return found;
		}

		return exp.getValue().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getValue(), lineno);//return ALetBeStExpAssistantInterpreter.findExpression(exp, lineno);
	}
	
	@Override
	public PExp caseALetDefExp(ALetDefExp exp, Integer lineno)
			throws AnalysisException
	{
		//return ALetDefExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		found = af.createPDefinitionListAssistant().findExpression(exp.getLocalDefs(), lineno);
		if (found != null)
			return found;

		return exp.getExpression().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getExpression(), lineno);
	}
	
	@Override
	public PExp caseAMapCompMapExp(AMapCompMapExp exp, Integer lineno)
			throws AnalysisException
	{
		//return AMapCompMapExpAssistantInterpreter.findExpression((AMapCompMapExp) exp, lineno);
		PExp found =findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		found = exp.getFirst().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getFirst(), lineno);
		if (found != null)
			return found;

		return exp.getPredicate() == null ? null
				: exp.getPredicate().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getPredicate(), lineno);
	}
	
	@Override
	public PExp caseAMapEnumMapExp(AMapEnumMapExp exp, Integer lineno)
			throws AnalysisException
	{
		//return AMapEnumMapExpAssistantInterpreter.findExpression((AMapEnumMapExp) exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		for (AMapletExp m : exp.getMembers())
		{
			found = m.apply(THIS, lineno);//AMapletExpAssistantInterpreter.findExpression(m, lineno);
			if (found != null)
				return found;
		}

		return null;
	}
	
	@Override
	public PExp defaultSMapExp(SMapExp exp, Integer lineno)
			throws AnalysisException
	{
//		return SMapExpAssistantInterpreter.findExpression(exp, lineno);
		return null;
	}
	
	@Override
	public PExp caseAMapletExp(AMapletExp exp, Integer lineno)
			throws AnalysisException
	{
		//return AMapletExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = exp.getLeft().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(m.getLeft(), lineno);
		return (found == null) ? exp.getRight().apply(THIS, lineno)//PExpAssistantInterpreter.findExpression(m.getRight(), lineno)
				: found;
	}
	
	@Override
	public PExp caseAMkBasicExp(AMkBasicExp exp, Integer lineno)
			throws AnalysisException
	{
		//return AMkBasicExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return exp.getArg().apply(THIS, lineno); //PExpAssistantInterpreter.findExpression(exp.getArg(), lineno);
	}
	
	@Override
	public PExp caseAMkTypeExp(AMkTypeExp exp, Integer lineno)
			throws AnalysisException
	{
		//return AMkTypeExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return af.createPExpAssistant().findExpression(exp.getArgs(), lineno);
	}
	
	@Override
	public PExp caseAMuExp(AMuExp exp, Integer lineno)
			throws AnalysisException
	{
		//return AMuExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return exp.getRecord().apply(THIS, lineno); //PExpAssistantInterpreter.findExpression(exp.getRecord(), lineno);
	}
	
	@Override
	public PExp caseANarrowExp(ANarrowExp exp, Integer lineno)
			throws AnalysisException
	{
		//return ANarrowExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = af.createPExpAssistant().findExpression(exp, lineno);

		if (found != null)
			return found;

		return exp.getTest().apply(THIS, lineno); //PExpAssistantInterpreter.findExpression(exp.getTest(), lineno);
	}

	@Override
	public PExp caseANewExp(ANewExp exp, Integer lineno)
			throws AnalysisException
	{
		//return ANewExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return af.createPExpAssistant().findExpression(exp.getArgs(), lineno);
	}
	
	@Override
	public PExp caseAPostOpExp(APostOpExp exp, Integer lineno)
			throws AnalysisException
	{
		//return APostOpExpAssistantInterpreter.findExpression(exp, lineno);
		//return PExpAssistantInterpreter.findExpression(exp.getPostexpression(), lineno);
		return exp.getPostexpression().apply(THIS, lineno);
	}

	@Override
	public PExp caseASameBaseClassExp(ASameBaseClassExp exp, Integer lineno)
			throws AnalysisException
	{
		//return ASameBaseClassExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		found = exp.getLeft().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getLeft(), lineno);
		if (found != null)
			return found;

		found = exp.getRight().apply(THIS, lineno); //PExpAssistantInterpreter.findExpression(exp.getRight(), lineno);
		if (found != null)
			return found;

		return null;
	}
	
	@Override
	public PExp caseASameClassExp(ASameClassExp exp, Integer lineno)
			throws AnalysisException
	{
		//return ASameClassExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		found = exp.getLeft().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getLeft(), lineno);
		if (found != null)
			return found;

		found = exp.getRight().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getRight(), lineno);
		if (found != null)
			return found;

		return null;
	}
	
	@Override
	public PExp caseASeqCompSeqExp(ASeqCompSeqExp exp, Integer lineno)
			throws AnalysisException
	{
		//return ASeqCompSeqExpAssistantInterpreter.findExpression((ASeqCompSeqExp) exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		found = exp.getFirst().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getFirst(), lineno);
		if (found != null)
			return found;

		return exp.getPredicate() == null ? null
				: exp.getPredicate().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getPredicate(), lineno);
		
	}
	
	@Override
	public PExp caseASeqEnumSeqExp(ASeqEnumSeqExp exp, Integer lineno)
			throws AnalysisException
	{
		//return ASeqEnumSeqExpAssistantInterpreter.findExpression((ASeqEnumSeqExp) exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return af.createPExpAssistant().findExpression(exp.getMembers(), lineno);
	}
	
	@Override
	public PExp defaultSSeqExp(SSeqExp exp, Integer lineno)
			throws AnalysisException
	{
		//return SSeqExpAssistantInterpreter.findExpression(exp, lineno);
		return null;
	}
	
	@Override
	public PExp caseASetCompSetExp(ASetCompSetExp exp, Integer lineno)
			throws AnalysisException
	{
		//return ASetCompSetExpAssistantInterpreter.findExpression((ASetCompSetExp) exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		found = exp.getFirst().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getFirst(), lineno);
		if (found != null)
			return found;

		return exp.getPredicate() == null ? null
				: exp.getPredicate().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getPredicate(), lineno);
	}
	
	@Override
	public PExp caseASetEnumSetExp(ASetEnumSetExp exp, Integer lineno)
			throws AnalysisException
	{
		//return ASetEnumSetExpAssistantInterpreter.findExpression((ASetEnumSetExp) exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return af.createPExpAssistant().findExpression(exp.getMembers(), lineno);
		
	}
	@Override
	public PExp caseASetRangeSetExp(ASetRangeSetExp exp, Integer lineno)
			throws AnalysisException
	{
		//return ASetRangeSetExpAssistantInterpreter.findExpression((ASetRangeSetExp) exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		found = exp.getFirst().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getFirst(), lineno);
		if (found != null)
			return found;

		found = exp.getLast().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getLast(), lineno);
		if (found != null)
			return found;

		return null;
	}
	
	@Override
	public PExp defaultSSetExp(SSetExp exp, Integer lineno)
			throws AnalysisException
	{
		//return SSetExpAssistantInterpreter.findExpression(exp, lineno);
		return null;
	}
	
	@Override
	public PExp caseASubseqExp(ASubseqExp exp, Integer lineno)
			throws AnalysisException
	{
		//return ASubseqExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		found = exp.getSeq().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getSeq(), lineno);
		if (found != null)
			return found;

		found = exp.getFrom().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getFrom(), lineno);
		if (found != null)
			return found;

		found = exp.getTo().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getTo(), lineno);
		if (found != null)
			return found;

		return null;
	}
	
	@Override
	public PExp caseATupleExp(ATupleExp exp, Integer lineno)
			throws AnalysisException
	{
		//return ATupleExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return af.createPExpAssistant().findExpression(exp.getArgs(), lineno);
	}
	
	@Override
	public PExp defaultSUnaryExp(SUnaryExp exp, Integer lineno)
			throws AnalysisException
	{
		//return SUnaryExpAssistantInterpreter.findExpression(exp, lineno);
		PExp found = findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return exp.getExp().apply(THIS, lineno);//PExpAssistantInterpreter.findExpression(exp.getExp(), lineno);
	}
	@Override
	public PExp defaultPExp(PExp exp, Integer lineno)
			throws AnalysisException
	{
		return findExpressionBaseCase(exp, lineno);
	}

	@Override
	public PExp createNewReturnValue(INode node, Integer question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PExp createNewReturnValue(Object node, Integer question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public static PExp findExpressionBaseCase(PExp exp, int lineno)
	{
		return (exp.getLocation().getStartLine() == lineno) ? exp : null;
	}

}
