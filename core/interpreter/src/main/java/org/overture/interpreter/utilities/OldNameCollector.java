package org.overture.interpreter.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.AElementsUnaryExp;
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
import org.overture.ast.expressions.SSetExp;
import org.overture.ast.expressions.SUnaryExp;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ASeqMultipleBind;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

/**
 * This class implements a way collect the old names from expressions
 * 
 * @author kel
 */
public class OldNameCollector extends AnswerAdaptor<LexNameList>
{

	protected IInterpreterAssistantFactory af;

	public OldNameCollector(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public LexNameList caseAApplyExp(AApplyExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		LexNameList list = af.createPExpAssistant().getOldNames(expression.getArgs());
		list.addAll(af.createPExpAssistant().getOldNames(expression.getRoot()));
		return list;
	}

	public LexNameList defaultSBinaryExp(SBinaryExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		LexNameList list = af.createPExpAssistant().getOldNames(expression.getLeft());
		list.addAll(af.createPExpAssistant().getOldNames(expression.getRight()));
		return list;
	}

	public LexNameList caseACasesExp(ACasesExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		LexNameList list = af.createPExpAssistant().getOldNames(expression.getExpression());

		for (ACaseAlternative c : expression.getCases())
		{
			list.addAll(c.apply(this));
		}

		if (expression.getOthers() != null)
		{
			list.addAll(af.createPExpAssistant().getOldNames(expression.getOthers()));
		}

		return list;
	}

	public LexNameList caseAElseIfExp(AElseIfExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		LexNameList list = af.createPExpAssistant().getOldNames(expression.getElseIf());
		list.addAll(af.createPExpAssistant().getOldNames(expression.getThen()));
		return list;
	}

	public LexNameList caseAExistsExp(AExistsExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		LexNameList list = new LexNameList();

		for (PMultipleBind mb : expression.getBindList())
		{
			list.addAll(mb.apply(this));
		}

		list.addAll(af.createPExpAssistant().getOldNames(expression.getPredicate()));
		return list;
	}

	public LexNameList caseAExists1Exp(AExists1Exp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		LexNameList list = expression.getBind().apply(this);
		list.addAll(af.createPExpAssistant().getOldNames(expression.getPredicate()));
		return list;
	}

	public LexNameList caseAFieldExp(AFieldExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		return af.createPExpAssistant().getOldNames(expression.getObject());
	}

	public LexNameList caseAFieldNumberExp(AFieldNumberExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		return af.createPExpAssistant().getOldNames(expression.getTuple());
	}

	public LexNameList caseAForAllExp(AForAllExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		LexNameList list = new LexNameList();

		for (PMultipleBind mb : expression.getBindList())
		{
			list.addAll(mb.apply(this));
		}

		list.addAll(af.createPExpAssistant().getOldNames(expression.getPredicate()));
		return list;
	}

	public LexNameList caseAFuncInstatiationExp(AFuncInstatiationExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		return af.createPExpAssistant().getOldNames(expression.getFunction());
	}

	public LexNameList caseAIfExp(AIfExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		LexNameList list = af.createPExpAssistant().getOldNames(expression.getTest());
		list.addAll(af.createPExpAssistant().getOldNames(expression.getThen()));

		for (AElseIfExp elif : expression.getElseList())
		{
			list.addAll(af.createPExpAssistant().getOldNames(elif));
		}

		if (expression.getElse() != null)
		{
			list.addAll(af.createPExpAssistant().getOldNames(expression.getElse()));
		}

		return list;
	}

	public LexNameList caseAIotaExp(AIotaExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		LexNameList list = expression.getBind().apply(this);
		list.addAll(af.createPExpAssistant().getOldNames(expression.getPredicate()));
		return list;
	}

	public LexNameList caseAIsExp(AIsExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		return af.createPExpAssistant().getOldNames(expression.getTest());
	}

	public LexNameList caseAIsOfBaseClassExp(AIsOfBaseClassExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		return af.createPExpAssistant().getOldNames(expression.getExp());
	}

	public LexNameList caseAIsOfClassExp(AIsOfClassExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		return af.createPExpAssistant().getOldNames(expression.getExp());
	}

	public LexNameList caseALambdaExp(ALambdaExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		return af.createPExpAssistant().getOldNames(expression.getExpression());
	}

	public LexNameList caseALetBeStExp(ALetBeStExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		LexNameList list = expression.getBind().apply(this);

		if (expression.getSuchThat() != null)
		{
			list.addAll(af.createPExpAssistant().getOldNames(expression.getSuchThat()));
		}

		list.addAll(af.createPExpAssistant().getOldNames(expression.getValue()));
		return list;
	}

	public LexNameList caseALetDefExp(ALetDefExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		LexNameList list = af.createPDefinitionListAssistant().getOldNames(expression.getLocalDefs());
		list.addAll(af.createPExpAssistant().getOldNames(expression.getExpression()));
		return list;
	}

	// public LexNameList defaultSMapExp(SMapExp expression)
	// throws org.overture.ast.analysis.AnalysisException
	// {
	// if (expression instanceof AMapCompMapExp)
	// {
	// return expression.apply(this);
	// } else if (expression instanceof AMapEnumMapExp)
	// {
	// return AMapEnumMapExpAssistantTC.getOldNames((AMapEnumMapExp) expression);
	// } else
	// {
	// assert false : "Should not happen";
	// return new LexNameList();
	// }
	// }

	public LexNameList caseAMapletExp(AMapletExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		LexNameList list = af.createPExpAssistant().getOldNames(expression.getLeft());
		list.addAll(af.createPExpAssistant().getOldNames(expression.getRight()));
		return list;
	}

	public LexNameList caseAMkBasicExp(AMkBasicExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		return af.createPExpAssistant().getOldNames(expression.getArg());
	}

	public LexNameList caseAMkTypeExp(AMkTypeExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		return af.createPExpAssistant().getOldNames(expression.getArgs());
	}

	public LexNameList caseAMuExp(AMuExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		LexNameList list = af.createPExpAssistant().getOldNames(expression.getRecord());

		for (ARecordModifier rm : expression.getModifiers())
		{
			list.addAll(rm.apply(this));
		}

		return list;
	}

	public LexNameList caseANarrowExp(ANarrowExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		return af.createPExpAssistant().getOldNames(expression.getTest());
	}

	public LexNameList caseANewExp(ANewExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		return af.createPExpAssistant().getOldNames(expression.getArgs());
	}

	public LexNameList caseAPostOpExp(APostOpExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		return af.createPExpAssistant().getOldNames(expression.getPostexpression());
	}

	public LexNameList caseASameBaseClassExp(ASameBaseClassExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		LexNameList list = af.createPExpAssistant().getOldNames(expression.getLeft());
		list.addAll(af.createPExpAssistant().getOldNames(expression.getRight()));
		return list;
	}

	public LexNameList caseASameClassExp(ASameClassExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		LexNameList list = af.createPExpAssistant().getOldNames(expression.getLeft());
		list.addAll(af.createPExpAssistant().getOldNames(expression.getRight()));
		return list;
	}

	// public LexNameList defaultSSeqExp(SSeqExp expression)
	// throws org.overture.ast.analysis.AnalysisException
	// {
	// if (expression instanceof ASeqCompSeqExp)
	// {
	// return ASeqCompSeqExpAssistantTC.getOldNames((ASeqCompSeqExp) expression);
	// } else if (expression instanceof ASeqEnumSeqExp)
	// {
	// return ASeqEnumSeqExpAssistantTC.getOldNames((ASeqEnumSeqExp) expression);
	// } else
	// {
	// assert false : "Should not happen";
	// return new LexNameList();
	// }
	// }

	public LexNameList defaultSSetExp(SSetExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		if (expression instanceof ASetCompSetExp
				|| expression instanceof ASetEnumSetExp)
		{
			return expression.apply(this);
		} else
		{
			return new LexNameList();
		}
	}

	public LexNameList caseASubseqExp(ASubseqExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		LexNameList list = af.createPExpAssistant().getOldNames(expression.getSeq());
		list.addAll(af.createPExpAssistant().getOldNames(expression.getFrom()));
		list.addAll(af.createPExpAssistant().getOldNames(expression.getTo()));
		return list;
	}

	public LexNameList caseATupleExp(ATupleExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		return af.createPExpAssistant().getOldNames(expression.getArgs());
	}

	public LexNameList defaultSUnaryExp(SUnaryExp expression)
			throws org.overture.ast.analysis.AnalysisException
	{
		if (expression instanceof AElementsUnaryExp)
		{
			return expression.apply(this);
		} else
		{
			return af.createPExpAssistant().getOldNames(expression.getExp());
		}
	}

	@Override
	public LexNameList caseAElementsUnaryExp(AElementsUnaryExp expression)
			throws AnalysisException
	{
		return af.createPExpAssistant().getOldNames(expression.getExp());
	}

	@Override
	public LexNameList caseAMapCompMapExp(AMapCompMapExp expression)
			throws AnalysisException
	{
		LexNameList list = af.createPExpAssistant().getOldNames(expression.getFirst());

		for (PMultipleBind mb : expression.getBindings())
		{
			list.addAll(mb.apply(this));
		}

		if (expression.getPredicate() != null)
		{
			list.addAll(af.createPExpAssistant().getOldNames(expression.getPredicate()));
		}

		return list;
	}

	@Override
	public LexNameList caseAMapEnumMapExp(AMapEnumMapExp expression)
			throws AnalysisException
	{
		LexNameList list = new LexNameList();

		for (AMapletExp maplet : expression.getMembers())
		{
			list.addAll(af.createPExpAssistant().getOldNames(maplet));
		}

		return list;
	}

	@Override
	public LexNameList caseASeqCompSeqExp(ASeqCompSeqExp expression)
			throws AnalysisException
	{
		LexNameList list = af.createPExpAssistant().getOldNames(expression.getFirst());
		list.addAll(expression.getBind().apply(this));

		if (expression.getPredicate() != null)
		{
			list.addAll(af.createPExpAssistant().getOldNames(expression.getPredicate()));
		}

		return list;
	}

	@Override
	public LexNameList caseASetCompSetExp(ASetCompSetExp expression)
			throws AnalysisException
	{
		LexNameList list = af.createPExpAssistant().getOldNames(expression.getFirst());

		for (PMultipleBind mb : expression.getBindings())
		{
			list.addAll(mb.apply(this));
		}

		if (expression.getPredicate() != null)
		{
			list.addAll(af.createPExpAssistant().getOldNames(expression.getPredicate()));
		}

		return list;
	}

	@Override
	public LexNameList caseASetEnumSetExp(ASetEnumSetExp expression)
			throws AnalysisException
	{
		return af.createPExpAssistant().getOldNames(expression.getMembers());
	}

	@Override
	public LexNameList caseASeqEnumSeqExp(ASeqEnumSeqExp expression)
			throws AnalysisException
	{
		return af.createPExpAssistant().getOldNames(expression.getMembers());
	}

	/**
	 * This is not an expression
	 */
	@Override
	public LexNameList caseACaseAlternative(ACaseAlternative c)
			throws AnalysisException
	{
		return af.createPExpAssistant().getOldNames(c.getResult());
	}

	/**
	 * This is not an expression
	 */
	@Override
	public LexNameList caseARecordModifier(ARecordModifier rm)
			throws AnalysisException
	{
		return af.createPExpAssistant().getOldNames(rm.getValue());
	}

	@Override
	public LexNameList caseASetBind(ASetBind bind) throws AnalysisException
	{
		return af.createPExpAssistant().getOldNames(bind.getSet());
	}

	@Override
	public LexNameList caseASetMultipleBind(ASetMultipleBind mb)
			throws AnalysisException
	{
		return af.createPExpAssistant().getOldNames(mb.getSet());
	}

	@Override
	public LexNameList caseASeqMultipleBind(ASeqMultipleBind mb)
			throws AnalysisException
	{
		return af.createPExpAssistant().getOldNames(mb.getSeq());
	}

	@Override
	public LexNameList defaultPBind(PBind bind) throws AnalysisException
	{
		if (bind instanceof ASetBind)
		{
			return bind.apply(this);
		} else if (bind instanceof ATypeBind)
		{
			return bind.apply(this);
		} else
		{
			assert false : "Should not happen";
			return null;
		}
	}

	@Override
	public LexNameList defaultPMultipleBind(PMultipleBind mb)
			throws AnalysisException
	{
		if (mb instanceof ASetMultipleBind)
		{
			return mb.apply(this);
		} else if (mb instanceof ATypeMultipleBind)
		{
			return mb.apply(this);
		} else
		{
			assert false : "Should not happen";
			return null;
		}
	}

	@Override
	public LexNameList caseAEqualsDefinition(AEqualsDefinition def)
			throws AnalysisException
	{
		LexNameList list = af.createPExpAssistant().getOldNames(def.getTest());

		if (def.getSetbind() != null)
		{
			list.addAll(def.getSetbind().apply(this));
		}
		else if (def.getSeqbind() != null)
		{
			list.addAll(def.getSeqbind().apply(this));
		}

		return list;
	}

	@Override
	public LexNameList caseATypeMultipleBind(ATypeMultipleBind node)
			throws AnalysisException
	{
		return new LexNameList();
	}

	@Override
	public LexNameList caseAValueDefinition(AValueDefinition def)
			throws AnalysisException
	{
		return af.createPExpAssistant().getOldNames(def.getExpression());
	}

	@Override
	public LexNameList defaultPDefinition(PDefinition d)
			throws AnalysisException
	{
		if (d instanceof AEqualsDefinition)
		{
			return d.apply(this);
		} else if (d instanceof AValueDefinition)
		{
			return d.apply(this);
		} else
		{
			return new LexNameList();
		}
	}

	@Override
	public LexNameList caseAVariableExp(AVariableExp expression)
			throws AnalysisException
	{
		if (expression.getName().getOld())
		{
			return new LexNameList(expression.getName());
		} else
		{
			return new LexNameList();
		}
	}

	@Override
	public LexNameList defaultPExp(PExp node) throws AnalysisException
	{
		return new LexNameList();
	}

	@Override
	public LexNameList createNewReturnValue(INode node)
	{
		assert false : "Should not happen";
		return new LexNameList();
	}

	@Override
	public LexNameList createNewReturnValue(Object node)
	{
		assert false : "Should not happen";
		return new LexNameList();
	}
}
