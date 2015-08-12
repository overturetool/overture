package org.overture.pog.visitors;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
import org.overture.pog.contexts.AssignmentContext;
import org.overture.pog.contexts.OpBodyEndContext;
import org.overture.pog.contexts.POFunctionDefinitionContext;
import org.overture.pog.contexts.POFunctionResultContext;
import org.overture.pog.contexts.POImpliesContext;
import org.overture.pog.contexts.PONameContext;
import org.overture.pog.contexts.POOperationDefinitionContext;
import org.overture.pog.obligation.FunctionPostCondition;
import org.overture.pog.obligation.OperationPostConditionObligation;
import org.overture.pog.obligation.ParameterPatternObligation;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.obligation.SatisfiabilityObligation;
import org.overture.pog.obligation.StateInvariantObligation;
import org.overture.pog.obligation.TypeCompatibilityObligation;
import org.overture.pog.obligation.ValueBindingObligation;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.utility.POException;
import org.overture.pog.utility.PogAssistantFactory;
import org.overture.pog.utility.UniqueNameGenerator;

public class PogParamDefinitionVisitor<Q extends IPOContextStack, A extends IProofObligationList>
		extends QuestionAnswerAdaptor<IPOContextStack, IProofObligationList>
{

	final private QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> rootVisitor;
	final private QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> mainVisitor;

	final protected IPogAssistantFactory assistantFactory;

	/**
	 * Constructor for Extensions.
	 * 
	 * @param parentVisitor
	 * @param mainVisitor
	 * @param assistantFactory
	 */
	public PogParamDefinitionVisitor(
			QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> parentVisitor,
			QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> mainVisitor,
			IPogAssistantFactory assistantFactory)
	{
		this.rootVisitor = parentVisitor;
		this.mainVisitor = mainVisitor;
		this.assistantFactory = assistantFactory;
	}

	/**
	 * Overture constructor. <b>NOT</b> for use by extensions.
	 * 
	 * @param parentVisitor
	 */
	public PogParamDefinitionVisitor(
			QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> parentVisitor)
	{
		this.rootVisitor = parentVisitor;
		this.mainVisitor = this;
		this.assistantFactory = new PogAssistantFactory();
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
			// Insanely, we can call operations in functions so let's set it up...
			question.setGenerator(new UniqueNameGenerator(node));

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
				{
					for (PPattern p : patterns)
					{
						for (PDefinition def : p.getDefinitions())
						{
							pids.add(def.getName());
						}

						alwaysMatches = alwaysMatches && p.apply(amVisitor);
					}
				}
			}

			// check for duplicates
			if (pids.hasDuplicates() || !alwaysMatches)
			{
				obligations.add(new ParameterPatternObligation(node, question, assistantFactory));
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
				obligations.add(new FunctionPostCondition(node, question, assistantFactory));
				question.push(new POFunctionResultContext(node));
				obligations.addAll(postcondition.apply(rootVisitor, question));
				question.pop();
				question.pop();
			}

			// do proof obligations for the function body

			question.push(new POFunctionDefinitionContext(node, true));
			PExp body = node.getBody();
			obligations.addAll(body.apply(rootVisitor, question));

			// do proof obligation for the return type
			if (node.getIsUndefined()
					|| !assistantFactory.getTypeComparator().isSubType(node.getActualResult(), node.getExpectedResult()))
			{
				TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(node, node.getExpectedResult(), node.getActualResult(), question, assistantFactory);
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
				for (PDefinition pdef : node.getClassDefinition().getDefinitions())
				{
					if (pdef instanceof AInstanceVariableDefinition)
					{
						AInstanceVariableDefinition ivdef = (AInstanceVariableDefinition) pdef;
						if (ivdef.getInitialized())
						{
							question.push(new AssignmentContext((AInstanceVariableDefinition) pdef, assistantFactory.getVarSubVisitor(), question));
						}
					}
				}
				list.add(new StateInvariantObligation(node, question, assistantFactory));
				question.clearStateContexts();
				list.add(new SatisfiabilityObligation(node, question, assistantFactory));
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
					PType patternType = assistantFactory.createPPatternAssistant().getPossibleType(pattern); // With
																												// unknowns
					AUnionType ut = (AUnionType) node.getExpType();
					PTypeSet set = new PTypeSet(assistantFactory);

					for (PType u : ut.getTypes())
					{
						if (assistantFactory.getTypeComparator().compatible(u, patternType))
						{
							set.add(u);
						}
					}

					if (!set.isEmpty())
					{
						PType compatible = set.getType(node.getLocation());

						if (!assistantFactory.getTypeComparator().isSubType(question.checkType(node.getTest(), node.getExpType()), compatible))
						{
							list.add(new ValueBindingObligation(node, question, assistantFactory));
							TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(node.getTest(), compatible, node.getExpType(), question, assistantFactory);
							if (sto != null)
							{
								list.add(sto);
							}
						}
					}
				}
			} else if (node.getTypebind() != null)
			{
				if (!assistantFactory.getTypeComparator().isSubType(question.checkType(node.getTest(), node.getExpType()), node.getDefType()))
				{
					TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(node.getTest(), node.getDefType(), node.getExpType(), question, assistantFactory);
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

			alwaysMatches = true;
			PatternAlwaysMatchesVisitor amVisitor = new PatternAlwaysMatchesVisitor();
			for (APatternListTypePair pltp : node.getParamPatterns())
			{
				for (PPattern p : pltp.getPatterns())
				{
					for (PDefinition def : assistantFactory.createPPatternAssistant().getDefinitions(p, typeIter.next(), NameScope.LOCAL))
					{
						pids.add(def.getName());
					}
					alwaysMatches = alwaysMatches && p.apply(amVisitor);
				}

			}

			if (pids.hasDuplicates() || !alwaysMatches)
			{
				obligations.add(new ParameterPatternObligation(node, question, assistantFactory));
			}
			// I pass one more argument to the method
			// POFunctionDefinitionContext to pass the assistantFactory.
			question.push(new POFunctionDefinitionContext(node, false, assistantFactory));

			if (node.getPrecondition() != null)
			{
				obligations.addAll(node.getPrecondition().apply(rootVisitor, question));
			}

			if (node.getPostcondition() != null)
			{
				if (node.getBody() != null) // else satisfiability, below
				{
					obligations.add(new FunctionPostCondition(node, question, assistantFactory));
				}

				question.push(new POFunctionResultContext(node));
				obligations.addAll(node.getPostcondition().apply(rootVisitor, question));
				question.pop();
			}

			if (node.getBody() == null)
			{
				if (node.getPostcondition() != null)
				{
					obligations.add(new SatisfiabilityObligation(node, question, assistantFactory));
				}
			} else
			{
				obligations.addAll(node.getBody().apply(rootVisitor, question));

				if (node.getIsUndefined()
						|| !assistantFactory.getTypeComparator().isSubType(node.getActualResult(), ((AFunctionType) node.getType()).getResult()))
				{
					TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(node, ((AFunctionType) node.getType()).getResult(), node.getActualResult(), question, assistantFactory);
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

			question.setGenerator(new UniqueNameGenerator(node));

			IProofObligationList obligations = new ProofObligationList();
			LexNameList pids = new LexNameList();

			Boolean precond = true;
			if (node.getPrecondition() == null)
			{
				precond = false;
			}

			collectOpCtxt(node, question, precond);

			// add all defined names from the function parameter list
			AOperationType otype = (AOperationType) node.getType();
			Iterator<PType> typeIter = otype.getParameters().iterator();
			boolean alwaysMatches = true;
			PatternAlwaysMatchesVisitor amVisitor = new PatternAlwaysMatchesVisitor();

			for (PPattern p : node.getParameterPatterns())
			{
				for (PDefinition def : assistantFactory.createPPatternAssistant().getDefinitions(p, typeIter.next(), NameScope.LOCAL))
				{
					pids.add(def.getName());
				}

				alwaysMatches = alwaysMatches && p.apply(amVisitor);
			}

			if (pids.hasDuplicates() || !alwaysMatches)
			{
				obligations.add(new ParameterPatternObligation(node, question, assistantFactory));
			}

			if (node.getPrecondition() != null)
			{
				obligations.addAll(node.getPrecondition().apply(rootVisitor, question));
			}

			obligations.addAll(node.getBody().apply(rootVisitor, question));

			if (node.getIsConstructor() && node.getClassDefinition() != null
					&& node.getClassDefinition().getInvariant() != null)
			{
				obligations.add(new StateInvariantObligation(node, question, assistantFactory));
			}

			if (!node.getIsConstructor()
					&& !assistantFactory.getTypeComparator().isSubType(node.getActualResult(), ((AOperationType) node.getType()).getResult()))
			{
				TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(node, node.getActualResult(), question, assistantFactory);
				if (sto != null)
				{
					obligations.add(sto);
				}

			}

			if (node.getPostcondition() != null)
			{

				List<AInstanceVariableDefinition> state = collectState(node);
				question.push(new OpBodyEndContext(state, assistantFactory));
				obligations.addAll(node.getPostcondition().apply(rootVisitor, question));
				obligations.add(new OperationPostConditionObligation(node, question, assistantFactory));
			}
			question.clearStateContexts();
			question.pop();
			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	protected List<AInstanceVariableDefinition> collectState(
			AExplicitOperationDefinition node) throws AnalysisException
	{
		List<AInstanceVariableDefinition> r = new LinkedList<AInstanceVariableDefinition>();
		List<PDefinition> stateDefs;
		if (node.getClassDefinition() != null)
		{
			stateDefs = node.getClassDefinition().getDefinitions();
		} else
		{
			if (node.getState() != null)
			{
				stateDefs = node.getState().getStateDefs();
			} else
			{
				return r;
			}
		}
		for (PDefinition d : stateDefs)
		{
			if (d instanceof AInstanceVariableDefinition)
			{
				r.add((AInstanceVariableDefinition) d);
			}
		}

		return r;

	}

	/**
	 * Operation processing is identical in extension except for context generation. So, a quick trick here.
	 * 
	 * @param node
	 * @param question
	 * @param precond
	 * @throws AnalysisException
	 */
	protected void collectOpCtxt(AExplicitOperationDefinition node,
			IPOContextStack question, Boolean precond) throws AnalysisException
	{
		question.push(new POOperationDefinitionContext(node, precond, node.getState()));
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
			boolean alwaysMatches = true;
			PatternAlwaysMatchesVisitor amVisitor = new PatternAlwaysMatchesVisitor();

			for (APatternListTypePair tp : node.getParameterPatterns())
			{
				for (PPattern p : tp.getPatterns())
				{
					for (PDefinition def : assistantFactory.createPPatternAssistant().getDefinitions(p, typeIter.next(), NameScope.LOCAL))
					{
						pids.add(def.getName());
					}

					alwaysMatches = alwaysMatches && p.apply(amVisitor);
				}
			}

			if (pids.hasDuplicates() || !alwaysMatches)
			{
				obligations.add(new ParameterPatternObligation(node, question, assistantFactory));
			}

			if (node.getPrecondition() != null)
			{
				obligations.addAll(node.getPrecondition().apply(rootVisitor, question));
			}

			if (node.getPostcondition() != null)
			{
				if (node.getPrecondition() != null)
				{
					question.push(new POImpliesContext(node.getPrecondition()));
					obligations.addAll(node.getPostcondition().apply(rootVisitor, question));
					question.pop();
				} else
				{
					obligations.addAll(node.getPostcondition().apply(rootVisitor, question));
				}
			}

			if (node.getBody() != null)
			{
				obligations.addAll(node.getBody().apply(rootVisitor, question));

				if (node.getIsConstructor()
						&& node.getClassDefinition() != null
						&& node.getClassDefinition().getInvariant() != null)
				{
					obligations.add(new StateInvariantObligation(node, question, assistantFactory));
				}

				if (!node.getIsConstructor()
						&& !assistantFactory.getTypeComparator().isSubType(node.getActualResult(), ((AOperationType) node.getType()).getResult()))
				{
					TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(node, node.getActualResult(), question, assistantFactory);
					if (sto != null)
					{
						obligations.add(sto);
					}
				}
			} else
			{
				if (node.getPostcondition() != null)
				{
					// passed 1 more argument to give the assistantFactory to
					// the constructor.
					question.push(new POOperationDefinitionContext(node, false, node.getStateDefinition(), assistantFactory));
					obligations.add(new SatisfiabilityObligation(node, node.getStateDefinition(), question, assistantFactory));
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

			if (!assistantFactory.getTypeComparator().isSubType(question.checkType(expression, expType), type))
			{
				TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(expression, type, expType, question, assistantFactory);
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

			if (!assistantFactory.getTypeComparator().isSubType(question.checkType(expression, expType), type))
			{
				TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(expression, type, expType, question, assistantFactory);
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
				list.add(new SatisfiabilityObligation(node, question, assistantFactory));
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
				list.add(new SatisfiabilityObligation(node, question, assistantFactory));
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
					&& assistantFactory.createPTypeAssistant().isUnion(type))
			{
				PType patternType = assistantFactory.createPPatternAssistant().getPossibleType(pattern);
				AUnionType ut = assistantFactory.createPTypeAssistant().getUnion(type);
				PTypeSet set = new PTypeSet(assistantFactory);

				for (PType u : ut.getTypes())
				{
					if (assistantFactory.getTypeComparator().compatible(u, patternType))
					{
						set.add(u);
					}
				}

				if (!set.isEmpty())
				{
					PType compatible = set.getType(node.getLocation());
					if (!assistantFactory.getTypeComparator().isSubType(type, compatible))
					{
						obligations.add(new ValueBindingObligation(node, question, assistantFactory));
						TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(exp, compatible, type, question, assistantFactory);
						if (sto != null)
						{
							obligations.add(sto);
						}
					}
				}
			}

			if (!assistantFactory.getTypeComparator().isSubType(question.checkType(exp, node.getExpType()), type))
			{
				TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(exp, type, node.getExpType(), question, assistantFactory);
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
			question.setGenerator(new UniqueNameGenerator(node));

			for (PDefinition def : node.getDefinitions())
			{
				question.push(new PONameContext(assistantFactory.createPDefinitionAssistant().getVariableNames(def)));
				proofObligationList.addAll(def.apply(mainVisitor, question));
				question.pop();
				question.clearStateContexts();
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
