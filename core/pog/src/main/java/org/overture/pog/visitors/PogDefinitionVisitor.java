package org.overture.pog.visitors;

import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
import org.overture.ast.definitions.traces.PTraceCoreDefinition;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.PPatternAssistantTC;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistantTC;
import org.overture.ast.utils.PTypeSet;
import org.overture.pog.obligations.FuncPostConditionObligation;
import org.overture.pog.obligations.OperationPostConditionObligation;
import org.overture.pog.obligations.POContextStack;
import org.overture.pog.obligations.POFunctionDefinitionContext;
import org.overture.pog.obligations.POFunctionResultContext;
import org.overture.pog.obligations.PONameContext;
import org.overture.pog.obligations.POOperationDefinitionContext;
import org.overture.pog.obligations.ParameterPatternObligation;
import org.overture.pog.obligations.ProofObligationList;
import org.overture.pog.obligations.SatisfiabilityObligation;
import org.overture.pog.obligations.StateInvariantObligation;
import org.overture.pog.obligations.SubTypeObligation;
import org.overture.pog.obligations.ValueBindingObligation;
import org.overture.typecheck.TypeComparator;
import org.overturetool.vdmj.lex.LexNameList;

public class PogDefinitionVisitor extends
		QuestionAnswerAdaptor<POContextStack, ProofObligationList>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3086193431700309588L;
	final private QuestionAnswerAdaptor<POContextStack, ProofObligationList> rootVisitor;

	public PogDefinitionVisitor(PogVisitor pogVisitor)
	{
		this.rootVisitor = pogVisitor;
	}

	@Override
	// from [1] pg. 35 we have an:
	// explicit function definition = identifier,
	// [ type variable list ], �:�, function type,
	// identifier, parameters list, �==�,
	// function body,
	// [ �pre�, expression ],
	// [ �post�, expression ],
	// [ �measure�, name ] ;
	public ProofObligationList caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node, POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();
		LexNameList pids = new LexNameList();

		// add all defined names from the function parameter list
		for (List<PPattern> patterns : node.getParamPatternList())
			for (PPattern p : patterns)
				for (PDefinition def : p.getDefinitions())
					pids.add(def.getName());

		// check for duplicates
		if (pids.hasDuplicates())
		{
			obligations.add(new ParameterPatternObligation(node, question));
		}

		// do proof obligations for the pre-condition
		PExp precondition = node.getPrecondition();
		if (precondition != null)
		{
			question.push(new POFunctionDefinitionContext(node, false));
			obligations.addAll(precondition.apply(rootVisitor, question));
			question.pop();
		}

		// do proof obligations for the post-condition
		PExp postcondition = node.getPostcondition();
		if (postcondition != null)
		{
			question.push(new POFunctionDefinitionContext(node, false));
			obligations.add(new FuncPostConditionObligation(node, question));
			question.push(new POFunctionResultContext(node));
			obligations.addAll(postcondition.apply(rootVisitor, question));
			question.pop();
			question.pop();
		}

		// do proof obligations for the function body

		question.push(new POFunctionDefinitionContext(node, true));
		PExp body = node.getBody();
		int sizeBefore = question.size();
		obligations.addAll(body.apply(rootVisitor, question));
		assert sizeBefore <= question.size();

		// do proof obligation for the return type
		if (node.getIsUndefined()
				|| !TypeComparator.isSubType(node.getActualResult(), node.getExpectedResult()))
		{
			obligations.add(new SubTypeObligation(node, node.getExpectedResult(), node.getActualResult(), question));
		}
		question.pop();

		return obligations;
	}

	@Override
	public ProofObligationList defaultSClassDefinition(SClassDefinition node,
			POContextStack question)
	{

		ProofObligationList proofObligationList = new ProofObligationList();

		for (PDefinition def : node.getDefinitions())
		{
			proofObligationList.addAll(def.apply(this, question));
		}
		return proofObligationList;

	}

	@Override
	public ProofObligationList caseAClassInvariantDefinition(
			AClassInvariantDefinition node, POContextStack question)
	{

		ProofObligationList list = new ProofObligationList();

		if (!node.getClassDefinition().getHasContructors())
		{
			list.add(new StateInvariantObligation(node, question));
		}

		return list;
	}

	@Override
	public ProofObligationList caseAEqualsDefinition(AEqualsDefinition node,
			POContextStack question)
	{

		ProofObligationList list = new ProofObligationList();

		PPattern pattern = node.getPattern();
		if (pattern != null)
		{
			if (!(pattern instanceof AIdentifierPattern)
					&& !(pattern instanceof AIgnorePattern)
					&& node.getExpType() instanceof AUnionType)
			{
				PType patternType = PPatternAssistantTC.getPossibleType(pattern); // With unknowns
				AUnionType ut = (AUnionType) node.getExpType();
				PTypeSet set = new PTypeSet();

				for (PType u : ut.getTypes())
				{
					if (TypeComparator.compatible(u, patternType))
					{
						set.add(u);
					}
				}

				if (!set.isEmpty())
				{
					PType compatible = set.getType(node.getLocation());

					if (!TypeComparator.isSubType(question.checkType(node.getTest(), node.getExpType()), compatible))
					{
						list.add(new ValueBindingObligation(node, question));
						list.add(new SubTypeObligation(node.getTest(), compatible, node.getExpType(), question));
					}
				}
			}
		} else if (node.getTypebind() != null)
		{
			if (!TypeComparator.isSubType(question.checkType(node.getTest(), node.getExpType()), node.getDefType()))
			{
				list.add(new SubTypeObligation(node.getTest(), node.getDefType(), node.getExpType(), question));
			}
		} else if (node.getSetbind() != null)
		{
			list.addAll(node.getSetbind().getSet().apply(rootVisitor, question));
		}

		list.addAll(node.getTest().apply(rootVisitor, question));
		return list;

	}

	@Override
	public ProofObligationList caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node, POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();
		LexNameList pids = new LexNameList();

		for (APatternListTypePair pltp : node.getParamPatterns())
		{
			for (PPattern p : pltp.getPatterns())
			{
				for (PDefinition def : p.getDefinitions())
					pids.add(def.getName());
			}
		}

		if (pids.hasDuplicates())
		{
			obligations.add(new ParameterPatternObligation(node, question));
		}

		if (node.getPrecondition() != null)
		{
			obligations.addAll(node.getPrecondition().apply(rootVisitor, question));
		}

		if (node.getPostcondition() != null)
		{
			if (node.getBody() != null) // else satisfiability, below
			{
				question.push(new POFunctionDefinitionContext(node, false));
				obligations.add(new FuncPostConditionObligation(node, question));
				question.pop();
			}

			question.push(new POFunctionResultContext(node));
			obligations.addAll(node.getPostcondition().apply(rootVisitor, question));
			question.pop();
		}

		question.push(new POFunctionDefinitionContext(node, false));

		if (node.getBody() == null)
		{
			if (node.getPostcondition() != null)
			{
				obligations.add(new SatisfiabilityObligation(node, question));
			}
		} else
		{
			obligations.addAll(node.getBody().apply(rootVisitor, question));

			if (node.getIsUndefined()
					|| !TypeComparator.isSubType(node.getActualResult(), node.getType().getResult()))
			{
				obligations.add(new SubTypeObligation(node, node.getType().getResult(), node.getActualResult(), question));
			}
		}

		question.pop();

		return obligations;

	}

	@Override
	public ProofObligationList caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();
		LexNameList pids = new LexNameList();

		// add all defined names from the function parameter list
		for (PPattern p : node.getParameterPatterns())
			for (PDefinition def : p.getDefinitions())
				pids.add(def.getName());

		if (pids.hasDuplicates())
		{
			obligations.add(new ParameterPatternObligation(node, question));
		}

		if (node.getPrecondition() != null)
		{
			obligations.addAll(node.getPrecondition().apply(rootVisitor, question));
		}

		if (node.getPostcondition() != null)
		{
			obligations.addAll(node.getPostcondition().apply(rootVisitor, question));
			obligations.add(new OperationPostConditionObligation(node, question));
		}

		obligations.addAll(node.getBody().apply(rootVisitor, question));

		if (node.getIsConstructor() && node.getClassDefinition() != null
				&& node.getClassDefinition().getInvariant() != null)
		{
			obligations.add(new StateInvariantObligation(node, question));
		}

		if (!node.getIsConstructor()
				&& !TypeComparator.isSubType(node.getActualResult(), node.getType().getResult()))
		{
			obligations.add(new SubTypeObligation(node, node.getActualResult(), question));
		}

		return obligations;
	}

	@Override
	public ProofObligationList caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node, POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();
		LexNameList pids = new LexNameList();

		for (APatternListTypePair tp : node.getParameterPatterns())
		{
			for (PPattern p : tp.getPatterns())
			{
				for (PDefinition def : p.getDefinitions())
					pids.add(def.getName());
			}
		}

		if (pids.hasDuplicates())
		{
			obligations.add(new ParameterPatternObligation(node, question));
		}

		if (node.getPrecondition() != null)
		{
			obligations.addAll(node.getPrecondition().apply(rootVisitor, question));
		}

		if (node.getPostcondition() != null)
		{
			obligations.addAll(node.getPostcondition().apply(rootVisitor, question));
			obligations.add(new OperationPostConditionObligation(node, question));
		}

		if (node.getBody() != null)
		{
			obligations.addAll(node.getBody().apply(rootVisitor, question));

			if (node.getIsConstructor() && node.getClassDefinition() != null
					&& node.getClassDefinition().getInvariant() != null)
			{
				obligations.add(new StateInvariantObligation(node, question));
			}

			if (!node.getIsConstructor()
					&& !TypeComparator.isSubType(node.getActualResult(), node.getType().getResult()))
			{
				obligations.add(new SubTypeObligation(node, node.getActualResult(), question));
			}
		} else
		{
			if (node.getPostcondition() != null)
			{
				question.push(new POOperationDefinitionContext(node, false, node.getStateDefinition()));
				obligations.add(new SatisfiabilityObligation(node, node.getStateDefinition(), question));
				question.pop();
			}
		}

		return obligations;
	}
	
	@Override
	public ProofObligationList caseAAssignmentDefinition(AAssignmentDefinition node, 
			POContextStack question)
	{
		ProofObligationList obligations = new ProofObligationList();
		
		PExp expression = node.getExpression();
		PType type = node.getType();
		PType expType = node.getExpType();
		
		obligations.addAll(expression.apply(rootVisitor,question));

		if (!TypeComparator.isSubType(question.checkType(expression, expType), type))
		{
			obligations.add(
				new SubTypeObligation(expression, type, expType, question));
		}

		return obligations;
	}

	@Override
	public ProofObligationList defaultPDefinition(PDefinition node,
			POContextStack question)
	{

		return new ProofObligationList();
	}
	
	public ProofObligationList caseAInstanceVariableDefinition(AInstanceVariableDefinition node, POContextStack question)
	{
		ProofObligationList obligations = new ProofObligationList();
		
		PExp expression = node.getExpression();
		PType type = node.getType();
		PType expType = node.getExpType();
		
		obligations.addAll(expression.apply(rootVisitor,question));

		if (!TypeComparator.isSubType(question.checkType(expression, expType), type))
		{
			obligations.add(
				new SubTypeObligation(expression, type, expType, question));
		}

		return obligations;
	}

	@Override
	public ProofObligationList caseAPerSyncDefinition(APerSyncDefinition node,
			POContextStack question)
	{

		question.push(new PONameContext(new LexNameList(node.getOpname())));
		ProofObligationList list = node.getGuard().apply(rootVisitor, question);
		question.pop();
		return list;
	}

	@Override
	public ProofObligationList caseAStateDefinition(AStateDefinition node,
			POContextStack question)
	{

		ProofObligationList list = new ProofObligationList();

		if (node.getInvdef() != null)
		{
			list.addAll(node.getInvdef().apply(this, question));
		}

		return list;
	}

	@Override
	public ProofObligationList caseATypeDefinition(ATypeDefinition node,
			POContextStack question)
	{
		ProofObligationList list = new ProofObligationList();

		AExplicitFunctionDefinition invDef = node.getInvdef();

		if (invDef != null)
		{
			list.addAll(invDef.apply(this, question));
		}

		return list;
	}

	@Override
	public ProofObligationList caseAValueDefinition(AValueDefinition node,
			POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();

		PExp exp = node.getExpression();
		obligations.addAll(exp.apply(rootVisitor, question));

		PPattern pattern = node.getPattern();
		PType type = node.getType();

		if (!(pattern instanceof AIdentifierPattern)
				&& !(pattern instanceof AIgnorePattern)
				&& PTypeAssistantTC.isUnion(type))
		{
			PType patternType = PPatternAssistantTC.getPossibleType(pattern);
			AUnionType ut = PTypeAssistantTC.getUnion(type);
			PTypeSet set = new PTypeSet();

			for (PType u : ut.getTypes())
			{
				if (TypeComparator.compatible(u, patternType))
					set.add(u);
			}

			if (!set.isEmpty())
			{
				PType compatible = set.getType(node.getLocation());
				if (!TypeComparator.isSubType(type, compatible))
				{
					obligations.add(new ValueBindingObligation(node, question));
					obligations.add(new SubTypeObligation(exp, compatible, type, question));
				}
			}
		}

		if (!TypeComparator.isSubType(question.checkType(exp, node.getExpType()), type))
		{
			obligations.add(new SubTypeObligation(exp, type, node.getExpType(), question));
		}

		return obligations;

	}

	@Override
	public ProofObligationList defaultPTraceDefinition(PTraceDefinition node,
			POContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList defaultPTraceCoreDefinition(
			PTraceCoreDefinition node, POContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseAClassClassDefinition(
			AClassClassDefinition node, POContextStack question)
	{

		ProofObligationList proofObligationList = new ProofObligationList();

		for (PDefinition def : node.getDefinitions())
		{
			question.push(new PONameContext(PDefinitionAssistantTC.getVariableNames(def)));
			proofObligationList.addAll(def.apply(this, question));
			question.pop();
		}
		return proofObligationList;
	}

}
