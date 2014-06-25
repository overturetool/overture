package org.overture.modelcheckers.probsolver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.modelcheckers.probsolver.visitors.BToVdmConverter;
import org.overture.modelcheckers.probsolver.visitors.BToVdmConverter.ProBToVdmAnalysisException;
import org.overture.modelcheckers.probsolver.visitors.VdmToBConverter;
import org.overture.parser.util.ParserUtil;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.AConjunctPredicate;
import de.be4.classicalb.core.parser.node.AEqualPredicate;
import de.be4.classicalb.core.parser.node.AMemberPredicate;
import de.be4.classicalb.core.parser.node.APredicateParseUnit;
import de.be4.classicalb.core.parser.node.EOF;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PParseUnit;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.command.CbcSolveCommand;
import de.prob.animator.domainobjects.AbstractEvalElement;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.ComputationNotCompletedResult;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.prolog.output.PrologTermStringOutput;

public class ProbSolverUtil extends AbstractProbSolverUtil
{
	private class VdmSolution
	{
		INode result;

		public VdmSolution(INode result)
		{
			this.result = result;
		}

		public PExp getExpression()
		{
			return isExpression() ? (PExp) this.result : null;
		}

		public PStm getStatement()
		{
			return isStatement() ? (PStm) this.result : null;
		}

		public boolean isExpression()
		{
			return result instanceof PExp;
		}

		public boolean isStatement()
		{
			return result instanceof PStm;
		}
	}

	public static PStm solve(String name, AImplicitOperationDefinition opDef,
			Map<String, String> stateContext, Map<String, String> argContext,
			Map<String, PType> argumentTypes, PType tokenType,
			Set<String> quotes, SolverConsole console) throws SolverException
	{
		VdmSolution solution = new ProbSolverUtil(console, quotes).solve(name, opDef, opDef.getResult(), stateContext, argContext, argumentTypes, tokenType);
		if (solution.isStatement())
		{
			return solution.getStatement();
		}
		throw new SolverException("Unable to produce a statement result");
	}

	public static PExp solve(String name, PExp body, APatternTypePair result,
			Map<String, String> stateContext, Map<String, String> argContext,
			Map<String, PType> argumentTypes, PType tokenType,
			Set<String> quotes, SolverConsole console) throws SolverException
	{
		VdmSolution solution = new ProbSolverUtil(console, quotes).solve(name, body, result, stateContext, argContext, argumentTypes, tokenType);
		if (solution.isExpression())
		{
			return solution.getExpression();
		}
		throw new SolverException("Unable to produce a expression result");
	}

	private final Map<String, Set<String>> sets = new HashMap<String, Set<String>>();

	private ProbSolverUtil(SolverConsole console, Set<String> quotes)
	{
		super(console);
		this.sets.put(VdmToBConverter.QUOTES_SET, getQuoteNames(quotes));
	}

	private Set<String> getQuoteNames(Set<String> quotes)
	{
		Set<String> names = new HashSet<String>();
		for (String string : quotes)
		{
			names.add(VdmToBConverter.getQuoteLiteralName(string));
		}
		return names;
	}

	private AbstractEvalElement createFormula(INode def,
			Map<String, INode> argContext, Map<String, PType> argumentTypes,
			VdmToBConverter translator, Node statePredicate, Node post)
			throws AnalysisException, SolverException
	{
		if (post == null)
		{
			post = def.apply(translator);
		} else
		{
			post = new AConjunctPredicate((PPredicate) post, (PPredicate) def.apply(translator));
		}

		if (statePredicate != null)
		{
			post = new AConjunctPredicate((PPredicate) statePredicate, (PPredicate) post);
		}

		// add argument constraints
		for (Entry<String, INode> arg : argContext.entrySet())
		{
			AEqualPredicate eqp = new AEqualPredicate(VdmToBConverter.createIdentifier(arg.getKey()), (PExpression) arg.getValue().apply(translator));
			post = new AConjunctPredicate((PPredicate) post, eqp);
		}
		// add argument types (only if no values are given)
		for (Entry<String, PType> arg : argumentTypes.entrySet())
		{
			if (!argContext.containsKey(arg.getKey()))
			{
				AMemberPredicate eqp = new AMemberPredicate(VdmToBConverter.createIdentifier(arg.getKey()), (PExpression) arg.getValue().apply(translator));
				post = new AConjunctPredicate((PPredicate) post, eqp);
			}
		}

		PPredicate p = (PPredicate) post;

		Start s = new Start(new APredicateParseUnit(p), new EOF());

		// s.apply(new ASTPrinter(System.out));

		AbstractEvalElement formula = null;
		try
		{
			formula = new ClassicalB(s);
			console.out.println(displayFormat(formula));
		} catch (Exception e)
		{
			throw new SolverException("Syntax error in: " + post, e);
		}
		return formula;
	}

	/**
	 * Extracts the B solution and converts it to VDM expressions and then constructs a VDM block with the assignments
	 * and return value
	 * 
	 * @param context
	 * @param r
	 * @return
	 * @throws SolverException
	 * @throws UnsupportedTranslationException
	 */
	private VdmSolution extractSolution(VdmContext context, EvalResult r)
			throws SolverException, UnsupportedTranslationException
	{
		ABlockSimpleBlockStm block = AstFactory.newABlockSimpleBlockStm(new LexLocation(), new Vector<AAssignmentDefinition>());
		PStm resultStm = null;

		Map<String, PExp> returnExpressions = new HashMap<String, PExp>();
		List<AAssignmentStm> assignments = new Vector<AAssignmentStm>();

		for (Entry<String, String> entry : r.getSolutions().entrySet())
		{
			String solutionName = entry.getKey();

			{
				if (context.hasId(solutionName))
				{
					ClassicalB b = new ClassicalB(entry.getValue());
					PParseUnit p = b.getAst().getPParseUnit();
					console.out.println("\t\t\t" + solutionName + " = "
							+ b.getCode());

					if (context.isState(solutionName))
					{
						try
						{
							if (context instanceof VdmSlContext)
							{
								VdmSlContext slContext = (VdmSlContext) context;
								PExp stateExp = BToVdmConverter.convert(slContext.getStateType(), VdmToBConverter.QUOTE_LIT_PREFIX, p);
								block.getStatements().add(BToVdmConverter.getStateAssignment(slContext.getStateType(), (AMkTypeExp) stateExp));
							} else
							{
								PExp stateExp = BToVdmConverter.convert(context.types.get(solutionName), VdmToBConverter.QUOTE_LIT_PREFIX, p);
								assignments.add(BToVdmConverter.getAssignment(context.getStateId(solutionName), stateExp));
							}

						} catch (Exception e)
						{
							throw new SolverException("Error converting state expression", e);
						}
					} else if (context.isResult(solutionName))
					{
						try
						{
							PExp retExp = BToVdmConverter.convert(context.types.get(solutionName), VdmToBConverter.QUOTE_LIT_PREFIX, p);
							returnExpressions.put(solutionName, retExp);

						} catch (ProBToVdmAnalysisException e)
						{
							throw new UnsupportedTranslationException(e.getMessage(), e);
						} catch (Exception e)
						{
							throw new SolverException("Error converting result expression. Expected output type: "
									+ context.types.get(solutionName), e);
						}
					}
				}
			}
		}

		if (!assignments.isEmpty())
		{
			// there might be invs so use atomic assignment
			block.getStatements().add(BToVdmConverter.getAtomicBlock(assignments));
		}

		if (!returnExpressions.isEmpty())
		{
			if (returnExpressions.size() == 1)
			{
				PExp exp = returnExpressions.values().iterator().next();
				if (context instanceof VdmExpContext)
				{
					return new VdmSolution(exp);
				}
				resultStm = BToVdmConverter.getReturnStatement(null, exp);
			} else
			{
				List<PExp> exps = new Vector<PExp>();
				// sorting the return expression according to the return type spec
				for (String id : context.resultIds)
				{
					exps.add(returnExpressions.get(id));
				}
				PExp tuple = BToVdmConverter.createTuple(exps);
				if (context instanceof VdmExpContext)
				{
					return new VdmSolution(tuple);
				}
				resultStm = BToVdmConverter.getReturnStatement(null, tuple);
			}

			// this needs to be inserted last since this will be the return of the operation
			block.getStatements().add(resultStm);
		}

		return new VdmSolution(block);
	}

	public VdmSolution solve(String name, INode opDef, APatternTypePair result,
			Map<String, String> stateContext, Map<String, String> argContext,
			Map<String, PType> argumentTypes, PType tokenType)
			throws SolverException
	{

		try
		{
			Map<String, INode> arguments = new HashMap<String, INode>();
			for (Entry<String, String> a : argContext.entrySet())
			{
				arguments.put(a.getKey(), ParserUtil.parseExpression(a.getValue()).result);
			}

			Map<String, INode> stateConstraints = new HashMap<String, INode>();
			List<PDefinition> states = new Vector<PDefinition>();

			if (!(opDef instanceof PExp))
			{
				states = findState(opDef);
				for (Entry<String, String> a : stateContext.entrySet())
				{
					stateConstraints.put(a.getKey(), ParserUtil.parseExpression(a.getValue()).result);
				}
			}

			console.out.println("---------------------------------------------------------------------------------");
			console.out.println("Solver running for operation: " + name);

			VdmContext context = translate(states, opDef, result, arguments, argumentTypes, tokenType, stateConstraints);

			VdmSolution val = solve(context);
			return val;
		} catch (AnalysisException e)
		{
			throw new SolverException("Internal error in translation of the specification and post condition", e);
		}
	}

	private List<PDefinition> findState(INode opDef)
	{
		List<PDefinition> states = new Vector<PDefinition>();

		AModuleModules module = opDef.getAncestor(AModuleModules.class);
		if (module != null)
		{
			for (PDefinition def : module.getDefs())
			{
				if (def instanceof AStateDefinition)
				{
					states.add(def);
				}
			}
		} else
		{
			SClassDefinition classDef = opDef.getAncestor(SClassDefinition.class);

			if (classDef != null)
			{
				for (PDefinition def : classDef.getDefinitions())
				{
					if (def instanceof AInstanceVariableDefinition)
					{
						states.add(def);
					} else if (def instanceof AClassInvariantDefinition)
					{
						states.add(def);
					}
				}
			}
		}
		return states;
	}

	private VdmSolution solve(VdmContext context) throws SolverException
	{
		try
		{
			initialize(sets);
			CbcSolveCommand command = new CbcSolveCommand(context.solverInput);

			// // http://nightly.cobra.cs.uni-duesseldorf.de/cli/
			IEvalResult solverResult = null;

			try
			{
				animator.execute(command);
				solverResult = command.getValue();
			} catch (Exception e)
			{
				String message = "";
				if (e.getMessage().startsWith("Uncovered boolean expression"))
				{
					message = "not a predicate";
				} else
				{
					message = "error";
				}
				PrologTermStringOutput pout = new PrologTermStringOutput();
				ASTProlog prolog = new ASTProlog(pout, null);
				if (context.solverInput instanceof ClassicalB)
				{
					((ClassicalB) context.solverInput).getAst().apply(prolog);
				}

				throw new SolverException(message + " - \n\n" + e.getMessage()
						+ "\n\n" + displayFormat(context.solverInput) + "\n\n"
						+ pout.toString() + "\n\n", e);
			}

			if (solverResult != null)
			{
				if (solverResult instanceof ComputationNotCompletedResult)
				{
					ComputationNotCompletedResult r = (ComputationNotCompletedResult) solverResult;
					throw new SolverException(r.getReason());
				} else if (solverResult instanceof EvalResult)
				{
					EvalResult r = (EvalResult) solverResult;
					console.out.println("Solver solution:");

					return extractSolution(context, r);

				}
			}
			return null;
		} catch (Exception e)
		{
			if (e instanceof SolverException)
			{
				throw (SolverException) e;
			}
			throw new SolverException("Internal error in solver invocation", e);
		}
	}

	private VdmContext translate(List<PDefinition> stateDefinitions,
			INode opDef, APatternTypePair result,
			Map<String, INode> argContext, Map<String, PType> argumentTypes,
			PType tokenType, Map<String, INode> stateConstraints)
			throws AnalysisException, SolverException
	{
		VdmToBConverter translator = new VdmToBConverter(console, tokenType);

		AStateDefinition stateDef = null;

		Node statePredicate = null;

		List<ILexNameToken> stateIds = new Vector<ILexNameToken>();
		Map<String, PType> stateTypes = new HashMap<String, PType>();

		for (PDefinition state : stateDefinitions)
		{
			Node newStatePredicate = state.apply(translator);
			if (statePredicate == null)
			{
				statePredicate = newStatePredicate;
			} else
			{
				statePredicate = new AConjunctPredicate((PPredicate) statePredicate, (PPredicate) newStatePredicate);
			}

			if (state instanceof AStateDefinition)
			{
				stateDef = (AStateDefinition) state;
			} else if (state instanceof AInstanceVariableDefinition)
			{
				ILexNameToken name = state.getName();
				stateIds.add(name);
				stateTypes.put(name.getName(), state.getType());
			}
		}

		Node post = null;

		// add state constraints
		for (Entry<String, INode> arg : stateConstraints.entrySet())
		{
			String name = null;
			if (stateDef != null)
			{
				name = VdmToBConverter.getStateId(stateDef, true) + "'"
						+ arg.getKey();
			} else
			{
				name = arg.getKey() + VdmToBConverter.OLD_POST_FIX;
			}
			AEqualPredicate eqp = new AEqualPredicate(VdmToBConverter.createIdentifier(name), (PExpression) arg.getValue().apply(translator));
			if (post != null)
			{
				post = new AConjunctPredicate((PPredicate) post, eqp);
			} else
			{
				post = eqp;
			}
		}

		AbstractEvalElement formula = createFormula(opDef, argContext, argumentTypes, translator, statePredicate, post);

		// AOperationType opType = (AOperationType) opDef.getType();

		List<String> resultIds = new Vector<String>();
		Map<String, PType> resultTypes = new HashMap<String, PType>();

		if (result != null)
		{
			//TODO: Here I used the assistantFactory created in the superclass AbstractProbSol.
			
			LexNameList allReturnVariables = assistantFactory.createPPatternAssistant().getAllVariableNames(result.getPattern());
			List<PType> allReturnTypes = new Vector<PType>();

			if (allReturnVariables.size() == 1)
			{
				allReturnTypes.add(result.getType());
			} else
			{
				allReturnTypes.addAll(((AProductType) result.getType()).getTypes());
			}

			Iterator<ILexNameToken> varItr = allReturnVariables.iterator();
			Iterator<PType> typItr = allReturnTypes.iterator();

			while (varItr.hasNext() && typItr.hasNext())
			{
				String id = varItr.next().getName();
				resultIds.add(id);
				resultTypes.put(id, typItr.next());
			}

		}

		if (!translator.unsupportedConstructs.isEmpty())
		{
			throw new UnsupportedTranslationException(translator.unsupportedConstructs);
		}

		if (opDef instanceof PExp)
		{
			return new VdmExpContext(formula, resultTypes, stateIds, resultIds);
		}

		if (stateDef != null)
		{
			return new VdmSlContext(formula, VdmToBConverter.getStateIdToken(stateDef, false), (ARecordInvariantType) stateDef.getRecordType(), resultIds, resultTypes);
		}

		return new VdmPpContext(formula, stateIds, stateTypes, resultIds, resultTypes);

	}

}
