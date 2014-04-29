package org.overture.interpreter.utilities.expression;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
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
import org.overture.ast.node.INode;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.expression.AApplyExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ACasesExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ADefExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AElseIfExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AExists1ExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AExistsExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AFieldExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AFieldNumberExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AForAllExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AFuncInstatiationExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AIfExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AIotaExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AIsExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AIsOfBaseClassExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AIsOfClassExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ALambdaExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ALetBeStExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ALetDefExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AMapletExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AMkBasicExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AMkTypeExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AMuExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ANarrowExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ANewExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASameBaseClassExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASameClassExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASubseqExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ATupleExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AVariableExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.SBinaryExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.SMapExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.SSeqExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.SSetExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.SUnaryExpAssistantInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

/***************************************
 * 
 * This method collects and returns the values of an expression.
 * 
 * @author gkanos
 *
 ****************************************/
public class ExpressionValueCollector extends QuestionAnswerAdaptor<ObjectContext, ValueList>
{
	protected IInterpreterAssistantFactory af;
	
	public ExpressionValueCollector(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public ValueList caseAApplyExp(AApplyExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return AApplyExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList defaultSBinaryExp(SBinaryExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return SBinaryExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseACasesExp(ACasesExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return ACasesExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseADefExp(ADefExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return ADefExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseAElseIfExp(AElseIfExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return AElseIfExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseAExistsExp(AExistsExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return AExistsExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseAExists1Exp(AExists1Exp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return AExists1ExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseAFieldExp(AFieldExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return AFieldExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseAFieldNumberExp(AFieldNumberExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		return AFieldNumberExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseAForAllExp(AForAllExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return AForAllExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseAFuncInstatiationExp(AFuncInstatiationExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		return AFuncInstatiationExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseAIfExp(AIfExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return AIfExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseAIotaExp(AIotaExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return AIotaExpAssistantInterpreter.getValues((AIotaExp) exp, ctxt);
	}
	@Override
	public ValueList caseAIsExp(AIsExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return AIsExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseAIsOfBaseClassExp(AIsOfBaseClassExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		return AIsOfBaseClassExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseAIsOfClassExp(AIsOfClassExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		return AIsOfClassExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseALambdaExp(ALambdaExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return ALambdaExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseALetBeStExp(ALetBeStExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return ALetBeStExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseALetDefExp(ALetDefExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return ALetDefExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList defaultSMapExp(SMapExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return SMapExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseAMapletExp(AMapletExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return AMapletExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseAMkBasicExp(AMkBasicExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return AMkBasicExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseAMkTypeExp(AMkTypeExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return AMkTypeExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseAMuExp(AMuExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return AMuExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseANarrowExp(ANarrowExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return ANarrowExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseANewExp(ANewExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return ANewExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseASameBaseClassExp(ASameBaseClassExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		return ASameBaseClassExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseASameClassExp(ASameClassExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		return ASameClassExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList defaultSSeqExp(SSeqExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return SSeqExpAssistantInterpreter.getValues((SSeqExp) exp, ctxt);
	}
	
	@Override
	public ValueList defaultSSetExp(SSetExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return SSetExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseASubseqExp(ASubseqExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return ASubseqExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseATupleExp(ATupleExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return ATupleExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList defaultSUnaryExp(SUnaryExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return SUnaryExpAssistantInterpreter.getValues(exp, ctxt);
	}
	
	@Override
	public ValueList caseAVariableExp(AVariableExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return AVariableExpAssistantInterpreter.getVariable(exp, ctxt);
	} 
	
	@Override
	public ValueList defaultPExp(PExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return new ValueList(); // Default, for expressions with no variables
	}

	@Override
	public ValueList createNewReturnValue(INode node, ObjectContext question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueList createNewReturnValue(Object node, ObjectContext question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}


}
