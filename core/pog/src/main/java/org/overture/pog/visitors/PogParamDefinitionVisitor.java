package org.overture.pog.visitors;

import java.util.Iterator;
import java.util.List;

import org.junit.experimental.theories.internal.Assignments;
import org.overture.ast.analysis.AnalysisException;
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
import org.overture.ast.definitions.traces.PTraceCoreDefinition;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ANotYetSpecifiedStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.util.PTypeSet;
import org.overture.pog.obligation.FuncPostConditionObligation;
import org.overture.pog.obligation.OperationPostConditionObligation;
import org.overture.pog.obligation.POFunctionDefinitionContext;
import org.overture.pog.obligation.POFunctionResultContext;
import org.overture.pog.obligation.PONameContext;
import org.overture.pog.obligation.POOperationDefinitionContext;
import org.overture.pog.obligation.ParameterPatternObligation;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.obligation.SatisfiabilityObligation;
import org.overture.pog.obligation.StateInvariantObligation;
import org.overture.pog.obligation.SubTypeObligation;
import org.overture.pog.obligation.ValueBindingObligation;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.utility.POException;
import org.overture.pog.utility.PatternAlwaysMatchesVisitor;
import org.overture.pog.utility.PogAssistantFactory;
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class PogParamDefinitionVisitor<Q extends IPOContextStack, A extends IProofObligationList>
		extends QuestionAnswerAdaptor<IPOContextStack, IProofObligationList>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3086193431700309588L;
	final private QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> rootVisitor;
	final private QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> mainVisitor;

	final private IPogAssistantFactory assistantFactory;
	
	public PogParamDefinitionVisitor(
			QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> parentVisitor,
			QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> mainVisitor,
			IPogAssistantFactory assistantFactory)
	{
		this.rootVisitor = parentVisitor;
		this.mainVisitor = mainVisitor;
		this.assistantFactory = assistantFactory;
	}

	public PogParamDefinitionVisitor(
			QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> parentVisitor)
	{
		this.rootVisitor = parentVisitor;
		this.mainVisitor = this;
		this.assistantFactory =  new PogAssistantFactory();
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
	public IProofObligationList caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node, IPOContextStack question)
			throws AnalysisException
	{
		try
		{
			// skip not yet specified
			if (node.getBody() instanceof ANotYetSpecifiedExp)
			{
				return new ProofObligationList();
			}
			IProofObligationList obligations = new ProofObligationList();
			LexNameList pids = new LexNameList();

			// add all defined names from the function parameter list
			boolean alwaysMatches = true;

			for (List<PPattern> params : node.getParamPatternList())
			{
				alwaysMatches = params.isEmpty() && alwaysMatches;
			}

			if (alwaysMatches)
			{
				// no arguments. skip to next
			} else
			{

				alwaysMatches = true;
				PatternAlwaysMatchesVisitor amVisitor = new PatternAlwaysMatchesVisitor();
				for (List<PPattern> patterns : node.getParamPatternList())
					for (PPattern p : patterns)
					{
						for (PDefinition def : p.getDefinitions())
							pids.add(def.getName());

						alwaysMatches = alwaysMatches && p.apply(amVisitor);
					}
			}

			// check for duplicates
			if (pids.hasDuplicates() || !alwaysMatches)
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
				SubTypeObligation sto = SubTypeObligation.newInstance(node, node.getExpectedResult(), node.getActualResult(), question);
				if (sto != null)
				{
					obligations.add(sto);
				}
			}
			question.pop();

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList defaultSClassDefinition(SClassDefinition node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList proofObligationList = new ProofObligationList();

			for (PDefinition def : node.getDefinitions())
			{
				proofObligationList.addAll(def.apply(mainVisitor, question));
			}
			return proofObligationList;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAClassInvariantDefinition(
			AClassInvariantDefinition node, IPOContextStack question)
			throws AnalysisException
	{
		try
		{
			IProofObligationList list = new ProofObligationList();

			if (!node.getClassDefinition().getHasContructors())
			{
				list.add(new StateInvariantObligation(node, question));
			}

			return list;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAEqualsDefinition(AEqualsDefinition node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList list = new ProofObligationList();

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
							SubTypeObligation sto = SubTypeObligation.newInstance(node.getTest(), compatible, node.getExpType(), question);
							if (sto != null)
							{
								list.add(sto);
							}
						}
					}
				}
			} else if (node.getTypebind() != null)
			{
				if (!TypeComparator.isSubType(question.checkType(node.getTest(), node.getExpType()), node.getDefType()))
				{
					SubTypeObligation sto = SubTypeObligation.newInstance(node.getTest(), node.getDefType(), node.getExpType(), question);
					if (sto != null)
					{
						list.add(sto);
					}
				}
			} else if (node.getSetbind() != null)
			{
				list.addAll(node.getSetbind().getSet().apply(rootVisitor, question));
			}

			list.addAll(node.getTest().apply(rootVisitor, question));
			return list;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}

	}

	@Override
	public IProofObligationList caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node, IPOContextStack question)
			throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();
			LexNameList pids = new LexNameList();

			AFunctionType ftype = (AFunctionType) node.getType();
			Iterator<PType> typeIter = ftype.getParameters().iterator();

			boolean alwaysMatches;

			if (node.getParamPatterns().isEmpty())
			{
				alwaysMatches = true;
			} else
			{
				alwaysMatches = false;
				PatternAlwaysMatchesVisitor amVisitor = new PatternAlwaysMatchesVisitor();
				for (APatternListTypePair pltp : node.getParamPatterns())
				{
					for (PPattern p : pltp.getPatterns())
					{
						for (PDefinition def : PPatternAssistantTC.getDefinitions(p, typeIter.next(), NameScope.LOCAL))
							pids.add(def.getName());
						alwaysMatches = alwaysMatches && p.apply(amVisitor);
					}

				}
			}

			if (pids.hasDuplicates() || !alwaysMatches)
			{
				obligations.add(new ParameterPatternObligation(node, question));
			}

			question.push(new POFunctionDefinitionContext(node, false));

			if (node.getPrecondition() != null)
			{
				obligations.addAll(node.getPrecondition().apply(rootVisitor, question));
			}

			if (node.getPostcondition() != null)
			{
				if (node.getBody() != null) // else satisfiability, below
				{
					obligations.add(new FuncPostConditionObligation(node, question));
				}

				question.push(new POFunctionResultContext(node));
				obligations.addAll(node.getPostcondition().apply(rootVisitor, question));
				question.pop();
			}

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
						|| !TypeComparator.isSubType(node.getActualResult(), ((AFunctionType) node.getType()).getResult()))
				{
					SubTypeObligation sto = SubTypeObligation.newInstance(node, ((AFunctionType) node.getType()).getResult(), node.getActualResult(), question);
					if (sto != null)
					{
						obligations.add(sto);
					}
				}
			}

			question.pop();

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, IPOContextStack question)
			throws AnalysisException
	{
		try
		{
			if (node.getBody() instanceof ANotYetSpecifiedStm)
			{
				return new ProofObligationList();
			}
			IProofObligationList obligations = new ProofObligationList();
			LexNameList pids = new LexNameList();

			// add all defined names from the function parameter list
			AOperationType otype = (AOperationType) node.getType();
			Iterator<PType> typeIter = otype.getParameters().iterator();
			boolean alwaysMatches = false;
			PatternAlwaysMatchesVisitor amVisitor = new PatternAlwaysMatchesVisitor();

			for (PPattern p : node.getParameterPatterns())
			{
				for (PDefinition def : PPatternAssistantTC.getDefinitions(p, typeIter.next(), NameScope.LOCAL))
					pids.add(def.getName());

				alwaysMatches = alwaysMatches && p.apply(amVisitor);
			}

			if (pids.hasDuplicates() || !alwaysMatches)
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
					&& !TypeComparator.isSubType(node.getActualResult(), ((AOperationType) node.getType()).getResult()))
			{
				SubTypeObligation sto = SubTypeObligation.newInstance(node, node.getActualResult(), question);
				if (sto != null)
				{
					obligations.add(sto);
				}

			}

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node, IPOContextStack question)
			throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();
			LexNameList pids = new LexNameList();

			AOperationType otype = (AOperationType) node.getType();
			Iterator<PType> typeIter = otype.getParameters().iterator();
			boolean alwaysMatches = false;
			PatternAlwaysMatchesVisitor amVisitor = new PatternAlwaysMatchesVisitor();

			for (APatternListTypePair tp : node.getParameterPatterns())
			{
				for (PPattern p : tp.getPatterns())
				{
					for (PDefinition def : PPatternAssistantTC.getDefinitions(p, typeIter.next(), NameScope.LOCAL))
						pids.add(def.getName());

					alwaysMatches = alwaysMatches && p.apply(amVisitor);
				}
			}

			if (pids.hasDuplicates() || !alwaysMatches)
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
			}

			if (node.getBody() != null)
			{
				obligations.addAll(node.getBody().apply(rootVisitor, question));

				if (node.getIsConstructor()
						&& node.getClassDefinition() != null
						&& node.getClassDefinition().getInvariant() != null)
				{
					obligations.add(new StateInvariantObligation(node, question));
				}

				if (!node.getIsConstructor()
						&& !TypeComparator.isSubType(node.getActualResult(), ((AOperationType) node.getType()).getResult()))
				{
					SubTypeObligation sto = SubTypeObligation.newInstance(node, node.getActualResult(), question);
					if (sto != null)
					{
						obligations.add(sto);
					}
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
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAAssignmentDefinition(
			AAssignmentDefinition node, IPOContextStack question)
			throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();

			PExp expression = node.getExpression();
			PType type = node.getType();
			PType expType = node.getExpType();

			obligations.addAll(expression.apply(rootVisitor, question));

			if (!TypeComparator.isSubType(question.checkType(expression, expType), type))
			{
				SubTypeObligation sto = SubTypeObligation.newInstance(expression, type, expType, question);
				if (sto != null)
				{
					obligations.add(sto);
				}
			}

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList defaultPDefinition(PDefinition node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	public IProofObligationList caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node, IPOContextStack question)
			throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();

			PExp expression = node.getExpression();
			PType type = node.getType();
			PType expType = node.getExpType();

			obligations.addAll(expression.apply(rootVisitor, question));

			if (!TypeComparator.isSubType(question.checkType(expression, expType), type))
			{
				SubTypeObligation sto = SubTypeObligation.newInstance(expression, type, expType, question);
				if (sto != null)
				{
					obligations.add(sto);
				}
			}

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAPerSyncDefinition(APerSyncDefinition node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			question.push(new PONameContext(new LexNameList(node.getOpname())));
			IProofObligationList list = node.getGuard().apply(rootVisitor, question);
			question.pop();
			return list;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAStateDefinition(AStateDefinition node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList list = new ProofObligationList();

			if (node.getInvdef() != null)
			{
				list.addAll(node.getInvdef().apply(mainVisitor, question));
			}

			return list;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseATypeDefinition(ATypeDefinition node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList list = new ProofObligationList();

			AExplicitFunctionDefinition invDef = node.getInvdef();

			if (invDef != null)
			{
				list.addAll(invDef.apply(mainVisitor, question));
			}

			return list;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAValueDefinition(AValueDefinition node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();

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
						SubTypeObligation sto = SubTypeObligation.newInstance(exp, compatible, type, question);
						if (sto != null)
						{
							obligations.add(sto);
						}
					}
				}
			}

			if (!TypeComparator.isSubType(question.checkType(exp, node.getExpType()), type))
			{
				SubTypeObligation sto = SubTypeObligation.newInstance(exp, type, node.getExpType(), question);
				if (sto != null)
				{
					obligations.add(sto);
				}
			}

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList defaultPTraceDefinition(PTraceDefinition node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList defaultPTraceCoreDefinition(
			PTraceCoreDefinition node, IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseAClassClassDefinition(
			AClassClassDefinition node, IPOContextStack question)
			throws AnalysisException
	{
		try
		{
			IProofObligationList proofObligationList = new ProofObligationList();

			for (PDefinition def : node.getDefinitions())
			{
				question.push(new PONameContext(assistantFactory.createPDefinitionAssistant().getVariableNames(def)));
				proofObligationList.addAll(def.apply(mainVisitor, question));
				question.pop();
			}
			return proofObligationList;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList createNewReturnValue(INode node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList createNewReturnValue(Object node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

}
