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
import org.overture.ast.expressions.ARecordModifier;
import org.overture.ast.expressions.ASameBaseClassExp;
import org.overture.ast.expressions.ASameClassExp;
import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.ASetEnumSetExp;
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
import org.overture.ast.patterns.PMultipleBind;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.definition.PDefinitionListAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AApplyExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ACaseAlternativeAssistantInterpreter;
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
import org.overture.interpreter.assistant.expression.AMapCompMapExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AMapEnumMapExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AMapletExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AMkBasicExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AMkTypeExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AMuExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ANarrowExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ANewExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ARecordModifierAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASameBaseClassExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASameClassExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASeqCompSeqExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASeqEnumSeqExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASetCompSetExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASetEnumSetExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASubseqExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ATupleExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AVariableExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.SBinaryExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.SMapExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.SSeqExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.SSetExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.SUnaryExpAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ASetBindAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.PBindAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.PMultipleBindAssistantInterpreter;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.values.UpdatableValue;
import org.overture.interpreter.values.Value;
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
		//return AApplyExpAssistantInterpreter.getValues(exp, ctxt);
		ValueList list = PExpAssistantInterpreter.getValues(exp.getArgs(), ctxt);
		list.addAll(PExpAssistantInterpreter.getValues(exp.getRoot(), ctxt));
		return list;
	}
	
	@Override
	public ValueList defaultSBinaryExp(SBinaryExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return SBinaryExpAssistantInterpreter.getValues(exp, ctxt);
		ValueList list = exp.getLeft().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getLeft(), ctxt);
		list.addAll(exp.getRight().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getRight(), ctxt));
		return list;
	}
	
	@Override
	public ValueList caseACasesExp(ACasesExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return ACasesExpAssistantInterpreter.getValues(exp, ctxt);
		ValueList list = exp.getExpression().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getExpression(), ctxt);

		for (ACaseAlternative c : exp.getCases())
		{
			list.addAll(c.apply(THIS, ctxt));//(ACaseAlternativeAssistantInterpreter.getValues(c, ctxt));
		}

		if (exp.getOthers() != null)
		{
			list.addAll(exp.getOthers().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getOthers(), ctxt));
		}

		return list;
	}
	
	@Override
	public ValueList caseADefExp(ADefExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return ADefExpAssistantInterpreter.getValues(exp, ctxt);
		ValueList list = PDefinitionListAssistantInterpreter.getValues(exp.getLocalDefs(), ctxt);
		list.addAll(exp.getExpression().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getExpression(), ctxt));
		return list;
	}
	
	@Override
	public ValueList caseAElseIfExp(AElseIfExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return AElseIfExpAssistantInterpreter.getValues(exp, ctxt);
		ValueList list = exp.getElseIf().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getElseIf(), ctxt);
		list.addAll(exp.getThen().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getThen(), ctxt));
		return list;
	}
	
	@Override
	public ValueList caseAExistsExp(AExistsExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return AExistsExpAssistantInterpreter.getValues(exp, ctxt);
		ValueList list = new ValueList();

		for (PMultipleBind mb : exp.getBindList())
		{
			list.addAll(PMultipleBindAssistantInterpreter.getValues(mb, ctxt));
		}

		list.addAll(exp.getPredicate().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getPredicate(), ctxt));
		return list;
	}
	
	@Override
	public ValueList caseAExists1Exp(AExists1Exp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return AExists1ExpAssistantInterpreter.getValues(exp, ctxt);
		ValueList list = PBindAssistantInterpreter.getValues(exp.getBind(), ctxt);
		list.addAll(exp.getPredicate().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getPredicate(), ctxt));
		return list;
	}
	
	@Override
	public ValueList caseAFieldExp(AFieldExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return AFieldExpAssistantInterpreter.getValues(exp, ctxt);
		ValueList values = exp.getObject().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getObject(), ctxt);

		try
		{
			// This evaluation should not affect scheduling as we are trying to
			// discover the sync variables to listen to only.

			ctxt.threadState.setAtomic(true);
			Value r = af.createAFieldExpAssistant().evaluate(exp, ctxt);

			if (r instanceof UpdatableValue)
			{
				values.add(r);
			}

			return values;
		}
		catch (ContextException e)
		{
			if (e.number == 4034)
			{
				return values; // Non existent variable
			} else
			{
				throw e;
			}
		}
		catch (ValueException e)
		{
			VdmRuntimeError.abort(exp.getLocation(), e);
			return null;
		}
		catch (AnalysisException e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			ctxt.threadState.setAtomic(false);
		}
	}
	
	@Override
	public ValueList caseAFieldNumberExp(AFieldNumberExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		//return AFieldNumberExpAssistantInterpreter.getValues(exp, ctxt);
		return exp.getTuple().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getTuple(), ctxt);
	}
	
	@Override
	public ValueList caseAForAllExp(AForAllExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return AForAllExpAssistantInterpreter.getValues(exp, ctxt);
		ValueList list = new ValueList();

		for (PMultipleBind mb : exp.getBindList())
		{
			list.addAll(PMultipleBindAssistantInterpreter.getValues(mb, ctxt));
		}

		list.addAll(exp.getPredicate().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getPredicate(), ctxt));
		return list;
	}
	
	@Override
	public ValueList caseAFuncInstatiationExp(AFuncInstatiationExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		//return AFuncInstatiationExpAssistantInterpreter.getValues(exp, ctxt);
		return exp.getFunction().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getFunction(), ctxt);
	}
	
	@Override
	public ValueList caseAIfExp(AIfExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return AIfExpAssistantInterpreter.getValues(exp, ctxt);
		ValueList list = exp.getTest().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getTest(), ctxt);
		list.addAll(exp.getThen().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getThen(), ctxt));

		for (AElseIfExp elif : exp.getElseList())
		{
			list.addAll(elif.apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(elif, ctxt));
		}

		if (exp.getElse() != null)
		{
			list.addAll(exp.getElse().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getElse(), ctxt));
		}

		return list;
	}
	
	@Override
	public ValueList caseAIotaExp(AIotaExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return AIotaExpAssistantInterpreter.getValues((AIotaExp) exp, ctxt);
		ValueList list = PBindAssistantInterpreter.getValues(exp.getBind(), ctxt);
		list.addAll(exp.getPredicate().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getPredicate(), ctxt));
		return list;
	}
	@Override
	public ValueList caseAIsExp(AIsExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return AIsExpAssistantInterpreter.getValues(exp, ctxt);
		return exp.getTest().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getTest(), ctxt);
				
	}
	
	@Override
	public ValueList caseAIsOfBaseClassExp(AIsOfBaseClassExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		//return AIsOfBaseClassExpAssistantInterpreter.getValues(exp, ctxt);
		return exp.getExp().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getExp(), ctxt);
	}
	
	@Override
	public ValueList caseAIsOfClassExp(AIsOfClassExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		//return AIsOfClassExpAssistantInterpreter.getValues(exp, ctxt);
		return exp.getExp().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getExp(), ctxt);
	}
	
	@Override
	public ValueList caseALambdaExp(ALambdaExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return ALambdaExpAssistantInterpreter.getValues(exp, ctxt);
		return exp.getExpression().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getExpression(), ctxt);
	}
	
	@Override
	public ValueList caseALetBeStExp(ALetBeStExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return ALetBeStExpAssistantInterpreter.getValues(exp, ctxt);
		ValueList list = PMultipleBindAssistantInterpreter.getValues(exp.getBind(), ctxt);

		if (exp.getSuchThat() != null)
		{
			list.addAll(exp.getSuchThat().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getSuchThat(), ctxt));
		}

		list.addAll(exp.getValue().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getValue(), ctxt));
		return list;
	}
	
	@Override
	public ValueList caseALetDefExp(ALetDefExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return ALetDefExpAssistantInterpreter.getValues(exp, ctxt);
		
		ValueList list = PDefinitionListAssistantInterpreter.getValues(exp.getLocalDefs(), ctxt);
		list.addAll(exp.getExpression().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getExpression(), ctxt));
		return list;
	}
	
	@Override
	public ValueList caseAMapCompMapExp(AMapCompMapExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		//return AMapCompMapExpAssistantInterpreter.getValues((AMapCompMapExp) exp, ctxt);
		ValueList list = exp.getFirst().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getFirst(), ctxt);

		for (PMultipleBind mb : exp.getBindings())
		{
			list.addAll(PMultipleBindAssistantInterpreter.getValues(mb, ctxt));
		}

		if (exp.getPredicate() != null)
		{
			list.addAll(exp.getPredicate().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getPredicate(), ctxt));
		}

		return list;
	}
	
	@Override
	public ValueList caseAMapEnumMapExp(AMapEnumMapExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		//return AMapEnumMapExpAssistantInterpreter.getValues((AMapEnumMapExp) exp, ctxt);
		ValueList list = new ValueList();

		for (AMapletExp maplet : exp.getMembers())
		{
			list.addAll(maplet.apply(THIS, ctxt));//(AMapletExpAssistantInterpreter.getValues(maplet, ctxt));
		}

		return list;
	}
	
	@Override
	public ValueList defaultSMapExp(SMapExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
			return new ValueList();
	}
	
	@Override
	public ValueList caseAMapletExp(AMapletExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return AMapletExpAssistantInterpreter.getValues(exp, ctxt);
		ValueList list = exp.apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getLeft(), ctxt);
		list.addAll(exp.getRight().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getRight(), ctxt));
		return list;
	}
	
	@Override
	public ValueList caseAMkBasicExp(AMkBasicExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return AMkBasicExpAssistantInterpreter.getValues(exp, ctxt);
		return PExpAssistantInterpreter.getValues(exp.getArg(), ctxt);
	}
	
	@Override
	public ValueList caseAMkTypeExp(AMkTypeExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return AMkTypeExpAssistantInterpreter.getValues(exp, ctxt);
		return PExpAssistantInterpreter.getValues(exp.getArgs(), ctxt);
	}
	
	@Override
	public ValueList caseAMuExp(AMuExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return AMuExpAssistantInterpreter.getValues(exp, ctxt);
		ValueList list = exp.getRecord().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getRecord(), ctxt);

		for (ARecordModifier rm : exp.getModifiers())
		{
			list.addAll(rm.getValue().apply(THIS, ctxt));//PExpAssistantInterpreter.getValues(rm.getValue(), ctxt);)//ARecordModifierAssistantInterpreter.getValues(rm, ctxt));
		}

		return list;
	}
	
	@Override
	public ValueList caseANarrowExp(ANarrowExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return ANarrowExpAssistantInterpreter.getValues(exp, ctxt);
		//return PExpAssistantInterpreter.getValues(exp.getTest(), ctxt);
		return exp.getTest().apply(THIS, ctxt);
	}
	
	@Override
	public ValueList caseANewExp(ANewExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return ANewExpAssistantInterpreter.getValues(exp, ctxt);
		return PExpAssistantInterpreter.getValues(exp.getArgs(), ctxt);
	}
	
	@Override
	public ValueList caseASameBaseClassExp(ASameBaseClassExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		//return ASameBaseClassExpAssistantInterpreter.getValues(exp, ctxt);
		ValueList list = exp.getLeft().apply(THIS, ctxt); //PExpAssistantInterpreter.getValues(exp.getLeft(), ctxt);
		list.addAll(exp.getRight().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getRight(), ctxt));
		return list;
	}
	
	@Override
	public ValueList caseASameClassExp(ASameClassExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		//return ASameClassExpAssistantInterpreter.getValues(exp, ctxt);
		ValueList list = exp.getLeft().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getLeft(), ctxt);
		list.addAll(exp.getRight().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getRight(), ctxt));
		return list;
	}
	
	@Override
	public ValueList caseASeqCompSeqExp(ASeqCompSeqExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		//return ASeqCompSeqExpAssistantInterpreter.getValues((ASeqCompSeqExp) exp, ctxt);
		ValueList list = exp.getFirst().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getFirst(), ctxt);
		list.addAll(ASetBindAssistantInterpreter.getValues(exp.getSetBind(), ctxt));

		if (exp.getPredicate() != null)
		{
			list.addAll(exp.getPredicate().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getPredicate(), ctxt));
		}

		return list;
	}
	
	@Override
	public ValueList caseASeqEnumSeqExp(ASeqEnumSeqExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		//return ASeqEnumSeqExpAssistantInterpreter.getValues((ASeqEnumSeqExp) exp, ctxt);
		return PExpAssistantInterpreter.getValues(exp.getMembers(), ctxt);
	}
	
	@Override
	public ValueList defaultSSeqExp(SSeqExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
			return new ValueList();
	}
	
	@Override
	public ValueList caseASetCompSetExp(ASetCompSetExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		//return ASetCompSetExpAssistantInterpreter.getValues(exp, ctxt);
		ValueList list = exp.getFirst().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getFirst(), ctxt);

		for (PMultipleBind mb : exp.getBindings())
		{
			list.addAll(PMultipleBindAssistantInterpreter.getValues(mb, ctxt));
		}

		if (exp.getPredicate() != null)
		{
			list.addAll(exp.getPredicate().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getPredicate(), ctxt));
		}

		return list;
	}
	
	@Override
	public ValueList caseASetEnumSetExp(ASetEnumSetExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		//return ASetEnumSetExpAssistantInterpreter.getValues(exp, ctxt);
		return PExpAssistantInterpreter.getValues(exp.getMembers(), ctxt);
	}
	
	@Override
	public ValueList defaultSSetExp(SSetExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return SSeqExpAssistantInterpreter.getValues(exp, ctxt);
			return new ValueList();
	}
	
	@Override
	public ValueList caseASubseqExp(ASubseqExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return ASubseqExpAssistantInterpreter.getValues(exp, ctxt);
		ValueList list = exp.getSeq().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getSeq(), ctxt);
		list.addAll(exp.getFrom().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getFrom(), ctxt));
		list.addAll(exp.getTo().apply(THIS, ctxt));//(PExpAssistantInterpreter.getValues(exp.getTo(), ctxt));
		return list;
	}
	
	@Override
	public ValueList caseATupleExp(ATupleExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return ATupleExpAssistantInterpreter.getValues(exp, ctxt);
		return PExpAssistantInterpreter.getValues(exp.getArgs(), ctxt);
	}
	
	@Override
	public ValueList defaultSUnaryExp(SUnaryExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return SUnaryExpAssistantInterpreter.getValues(exp, ctxt);
		return exp.getExp().apply(THIS, ctxt);//PExpAssistantInterpreter.getValues(exp.getExp(), ctxt);
	}
	
	@Override
	public ValueList caseAVariableExp(AVariableExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		//return AVariableExpAssistantInterpreter.getVariable(exp, ctxt);
		Value v = ctxt.check(exp.getName());

		if (v == null || !(v instanceof UpdatableValue))
		{
			return new ValueList();
		} else
		{
			return new ValueList(v);
		}
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
