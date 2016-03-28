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
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.values.UpdatableValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;

/***************************************
 * This method collects and returns the values of an expression.
 * 
 * @author gkanos
 ****************************************/
public class ExpressionValueCollector extends
		QuestionAnswerAdaptor<ObjectContext, ValueList>
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
		ValueList list = af.createPExpAssistant().getValues(exp.getArgs(), ctxt);
		list.addAll(exp.getRoot().apply(THIS, ctxt));
		return list;
	}

	@Override
	public ValueList defaultSBinaryExp(SBinaryExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		ValueList list = exp.getLeft().apply(THIS, ctxt);
		list.addAll(exp.getRight().apply(THIS, ctxt));
		return list;
	}

	@Override
	public ValueList caseACasesExp(ACasesExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		ValueList list = exp.getExpression().apply(THIS, ctxt);

		for (ACaseAlternative c : exp.getCases())
		{
			list.addAll(c.apply(THIS, ctxt));
		}

		if (exp.getOthers() != null)
		{
			list.addAll(exp.getOthers().apply(THIS, ctxt));
		}

		return list;
	}

	@Override
	public ValueList caseADefExp(ADefExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		ValueList list = af.createPDefinitionListAssistant().getValues(exp.getLocalDefs(), ctxt);
		list.addAll(exp.getExpression().apply(THIS, ctxt));
		return list;
	}

	@Override
	public ValueList caseAElseIfExp(AElseIfExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		ValueList list = exp.getElseIf().apply(THIS, ctxt);
		list.addAll(exp.getThen().apply(THIS, ctxt));
		return list;
	}

	@Override
	public ValueList caseAExistsExp(AExistsExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		ValueList list = new ValueList();

		for (PMultipleBind mb : exp.getBindList())
		{
			list.addAll(ctxt.assistantFactory.createPMultipleBindAssistant().getValues(mb, ctxt));
		}

		list.addAll(exp.getPredicate().apply(THIS, ctxt));
		return list;
	}

	@Override
	public ValueList caseAExists1Exp(AExists1Exp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		ValueList list = af.createPBindAssistant().getValues(exp.getBind(), ctxt);
		list.addAll(exp.getPredicate().apply(THIS, ctxt));
		return list;
	}

	@Override
	public ValueList caseAFieldExp(AFieldExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		ValueList values = exp.getObject().apply(THIS, ctxt);

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
		} catch (ContextException e)
		{
			if (e.number == 4034)
			{
				return values; // Non existent variable
			} else
			{
				throw e;
			}
		} catch (ValueException e)
		{
			if (e.number == 4097 || e.number == 4105)
			{
				return values;	// Cannot get record/object value of ... 
			}
			VdmRuntimeError.abort(exp.getLocation(), e);
			return null;
		} catch (AnalysisException e)
		{
			e.printStackTrace();
			return null;
		} finally
		{
			ctxt.threadState.setAtomic(false);
		}
	}

	@Override
	public ValueList caseAFieldNumberExp(AFieldNumberExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return exp.getTuple().apply(THIS, ctxt);
	}

	@Override
	public ValueList caseAForAllExp(AForAllExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		ValueList list = new ValueList();

		for (PMultipleBind mb : exp.getBindList())
		{
			list.addAll(ctxt.assistantFactory.createPMultipleBindAssistant().getValues(mb, ctxt));
		}

		list.addAll(exp.getPredicate().apply(THIS, ctxt));
		return list;
	}

	@Override
	public ValueList caseAFuncInstatiationExp(AFuncInstatiationExp exp,
			ObjectContext ctxt) throws AnalysisException
	{
		return exp.getFunction().apply(THIS, ctxt);
	}

	@Override
	public ValueList caseAIfExp(AIfExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		ValueList list = exp.getTest().apply(THIS, ctxt);
		list.addAll(exp.getThen().apply(THIS, ctxt));

		for (AElseIfExp elif : exp.getElseList())
		{
			list.addAll(elif.apply(THIS, ctxt));
		}

		if (exp.getElse() != null)
		{
			list.addAll(exp.getElse().apply(THIS, ctxt));
		}

		return list;
	}

	@Override
	public ValueList caseAIotaExp(AIotaExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		ValueList list = af.createPBindAssistant().getValues(exp.getBind(), ctxt);
		list.addAll(exp.getPredicate().apply(THIS, ctxt));
		return list;
	}

	@Override
	public ValueList caseAIsExp(AIsExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return exp.getTest().apply(THIS, ctxt);

	}

	@Override
	public ValueList caseAIsOfBaseClassExp(AIsOfBaseClassExp exp,
			ObjectContext ctxt) throws AnalysisException
	{

		return exp.getExp().apply(THIS, ctxt);
	}

	@Override
	public ValueList caseAIsOfClassExp(AIsOfClassExp exp, ObjectContext ctxt)
			throws AnalysisException
	{

		return exp.getExp().apply(THIS, ctxt);
	}

	@Override
	public ValueList caseALambdaExp(ALambdaExp exp, ObjectContext ctxt)
			throws AnalysisException
	{

		return exp.getExpression().apply(THIS, ctxt);
	}

	@Override
	public ValueList caseALetBeStExp(ALetBeStExp exp, ObjectContext ctxt)
			throws AnalysisException
	{

		ValueList list = ctxt.assistantFactory.createPMultipleBindAssistant().getValues(exp.getBind(), ctxt);

		if (exp.getSuchThat() != null)
		{
			list.addAll(exp.getSuchThat().apply(THIS, ctxt));
		}

		list.addAll(exp.getValue().apply(THIS, ctxt));
		return list;
	}

	@Override
	public ValueList caseALetDefExp(ALetDefExp exp, ObjectContext ctxt)
			throws AnalysisException
	{

		ValueList list = af.createPDefinitionListAssistant().getValues(exp.getLocalDefs(), ctxt);
		list.addAll(exp.getExpression().apply(THIS, ctxt));
		return list;
	}

	@Override
	public ValueList caseAMapCompMapExp(AMapCompMapExp exp, ObjectContext ctxt)
			throws AnalysisException
	{

		ValueList list = exp.getFirst().apply(THIS, ctxt);

		for (PMultipleBind mb : exp.getBindings())
		{
			list.addAll(ctxt.assistantFactory.createPMultipleBindAssistant().getValues(mb, ctxt));
		}

		if (exp.getPredicate() != null)
		{
			list.addAll(exp.getPredicate().apply(THIS, ctxt));
		}

		return list;
	}

	@Override
	public ValueList caseAMapEnumMapExp(AMapEnumMapExp exp, ObjectContext ctxt)
			throws AnalysisException
	{

		ValueList list = new ValueList();

		for (AMapletExp maplet : exp.getMembers())
		{
			list.addAll(maplet.apply(THIS, ctxt));
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

		ValueList list = exp.apply(THIS, ctxt);
		list.addAll(exp.getRight().apply(THIS, ctxt));
		return list;
	}

	@Override
	public ValueList caseAMkBasicExp(AMkBasicExp exp, ObjectContext ctxt)
			throws AnalysisException
	{

		return exp.getArg().apply(THIS, ctxt);
	}

	@Override
	public ValueList caseAMkTypeExp(AMkTypeExp exp, ObjectContext ctxt)
			throws AnalysisException
	{

		return af.createPExpAssistant().getValues(exp.getArgs(), ctxt);
	}

	@Override
	public ValueList caseAMuExp(AMuExp exp, ObjectContext ctxt)
			throws AnalysisException
	{

		ValueList list = exp.getRecord().apply(THIS, ctxt);

		for (ARecordModifier rm : exp.getModifiers())
		{
			list.addAll(rm.getValue().apply(THIS, ctxt));
		}

		return list;
	}

	@Override
	public ValueList caseANarrowExp(ANarrowExp exp, ObjectContext ctxt)
			throws AnalysisException
	{

		return exp.getTest().apply(THIS, ctxt);
	}

	@Override
	public ValueList caseANewExp(ANewExp exp, ObjectContext ctxt)
			throws AnalysisException
	{

		return af.createPExpAssistant().getValues(exp.getArgs(), ctxt);
	}

	@Override
	public ValueList caseASameBaseClassExp(ASameBaseClassExp exp,
			ObjectContext ctxt) throws AnalysisException
	{

		ValueList list = exp.getLeft().apply(THIS, ctxt);
		list.addAll(exp.getRight().apply(THIS, ctxt));
		return list;
	}

	@Override
	public ValueList caseASameClassExp(ASameClassExp exp, ObjectContext ctxt)
			throws AnalysisException
	{

		ValueList list = exp.getLeft().apply(THIS, ctxt);
		list.addAll(exp.getRight().apply(THIS, ctxt));
		return list;
	}

	@Override
	public ValueList caseASeqCompSeqExp(ASeqCompSeqExp exp, ObjectContext ctxt)
			throws AnalysisException
	{

		ValueList list = exp.getFirst().apply(THIS, ctxt);
		list.addAll(af.createPBindAssistant().getBindValues(exp.getSetBind(), ctxt, false));

		if (exp.getPredicate() != null)
		{
			list.addAll(exp.getPredicate().apply(THIS, ctxt));
		}

		return list;
	}

	@Override
	public ValueList caseASeqEnumSeqExp(ASeqEnumSeqExp exp, ObjectContext ctxt)
			throws AnalysisException
	{

		return af.createPExpAssistant().getValues(exp.getMembers(), ctxt);
	}

	@Override
	public ValueList defaultSSeqExp(SSeqExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return new ValueList();
	}

	@Override
	public ValueList caseASetCompSetExp(ASetCompSetExp exp, ObjectContext ctxt)
			throws AnalysisException
	{

		ValueList list = exp.getFirst().apply(THIS, ctxt);

		for (PMultipleBind mb : exp.getBindings())
		{
			list.addAll(ctxt.assistantFactory.createPMultipleBindAssistant().getValues(mb, ctxt));
		}

		if (exp.getPredicate() != null)
		{
			list.addAll(exp.getPredicate().apply(THIS, ctxt));
		}

		return list;
	}

	@Override
	public ValueList caseASetEnumSetExp(ASetEnumSetExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return af.createPExpAssistant().getValues(exp.getMembers(), ctxt);
	}

	@Override
	public ValueList defaultSSetExp(SSetExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return new ValueList();
	}

	@Override
	public ValueList caseASubseqExp(ASubseqExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		ValueList list = exp.getSeq().apply(THIS, ctxt);
		list.addAll(exp.getFrom().apply(THIS, ctxt));
		list.addAll(exp.getTo().apply(THIS, ctxt));
		return list;
	}

	@Override
	public ValueList caseATupleExp(ATupleExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return af.createPExpAssistant().getValues(exp.getArgs(), ctxt);
	}

	@Override
	public ValueList defaultSUnaryExp(SUnaryExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
		return exp.getExp().apply(THIS, ctxt);
	}

	@Override
	public ValueList caseAVariableExp(AVariableExp exp, ObjectContext ctxt)
			throws AnalysisException
	{
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
