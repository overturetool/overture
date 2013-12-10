package org.overture.modelcheckers.probsolver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.modelcheckers.probsolver.visitors.BToVdmConverter;
import org.overture.modelcheckers.probsolver.visitors.VdmToBConverter;
import org.overture.parser.util.ParserUtil;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;

import ch.qos.logback.classic.Level;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.AConjunctPredicate;
import de.be4.classicalb.core.parser.node.AEqualPredicate;
import de.be4.classicalb.core.parser.node.APredicateParseUnit;
import de.be4.classicalb.core.parser.node.EOF;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PParseUnit;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.IAnimator;
import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.CbcSolveCommand;
import de.prob.animator.command.LoadBProjectFromStringCommand;
import de.prob.animator.command.SetPreferenceCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.animator.domainobjects.AbstractEvalElement;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.ComputationNotCompletedResult;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.webconsole.ServletContextListener;

public class ProbSolverUtil
{
	public static class SolverException extends Exception
	{

		/**
		 * Generated serial
		 */
		private static final long serialVersionUID = 1L;

		public SolverException(String message)
		{
			super(message);
		}

		public SolverException(String message, Throwable reason)
		{
			super(message, reason);
		}

	}

	private final SolverConsole console;

	private ProbSolverUtil(SolverConsole console)
	{
		this.console = console;
	}

	public static PStm solve(ILexNameToken name,
			AImplicitOperationDefinition opDef,
			Map<String, String> stateContext, Map<String, String> argContext,
			SolverConsole console) throws SolverException
	{
		return new ProbSolverUtil(console).solve(name, opDef, stateContext, argContext);
	}

	public PStm solve(ILexNameToken name, AImplicitOperationDefinition opDef,
			Map<String, String> stateContext, Map<String, String> argContext)
			throws SolverException
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

		try
		{
			Map<String, INode> arguments = new HashMap<String, INode>();
			for (Entry<String, String> a : argContext.entrySet())
			{
				Settings.dialect = Dialect.VDM_PP;
				Settings.release = Release.VDM_10;
				arguments.put(a.getKey(), ParserUtil.parseExpression(a.getValue()).result);
			}

			Map<String, INode> stateConstraints = new HashMap<String, INode>();
			for (Entry<String, String> a : stateContext.entrySet())
			{
				Settings.dialect = Dialect.VDM_PP;
				Settings.release = Release.VDM_10;
				stateConstraints.put(a.getKey(), ParserUtil.parseExpression(a.getValue()).result);
			}

			console.out.println("---------------------------------------------------------------------------------");
			console.out.println("Solver running for operation: " + name);

			VdmContext context = translate(states, opDef, arguments, stateConstraints);

			PStm val = solve(context);
			return val;
		} catch (AnalysisException e)
		{
			throw new SolverException("Internal error in translation of the specification and post condition", e);
		}
	}

	private static class VdmContext
	{
		public final AbstractEvalElement solverInput;
		public final Map<String, PType> types;
		/**
		 * Sorted list of return values matching the mk_(...) ret that will be generated
		 */
		public final List<String> resultIds;
		public final List<ILexNameToken> stateIds;

		public VdmContext(AbstractEvalElement solverInput,
				Map<String, PType> types, List<ILexNameToken> stateIds,
				List<String> resultIds)
		{
			this.solverInput = solverInput;
			this.types = types;
			this.stateIds = stateIds;
			this.resultIds = resultIds;
		}

		public boolean hasId(String id)
		{
			return resultIds.contains(id) || isState(id);
		}

		public boolean isState(String id)
		{
			return getStateId(id) != null;
		}

		public boolean isResult(String id)
		{
			return resultIds.contains(id);
		}

		public ILexNameToken getStateId(String id)
		{
			for (ILexNameToken stateId : stateIds)
			{
				if (stateId.getName().equals(id))
				{
					return stateId;
				}
			}
			return null;
		}
	}

	private static class VdmSlContext extends VdmContext
	{

		public VdmSlContext(AbstractEvalElement solverInput,
				ILexNameToken stateId, ARecordInvariantType stateType,
				List<String> resultIds, Map<String, PType> resultTypes)
		{
			super(solverInput, resultTypes, Arrays.asList(new ILexNameToken[] { stateId }), resultIds);
			this.types.put(stateId.getName(), stateType);
			this.types.putAll(resultTypes);
		}

		public ILexNameToken getStateId()
		{
			return this.stateIds.iterator().next();
		}

		public ARecordInvariantType getStateType()
		{
			return (ARecordInvariantType) this.types.get(getStateId().getName());
		}
	}

	private static class VdmPpContext extends VdmContext
	{

		public VdmPpContext(AbstractEvalElement solverInput,
				Map<String, PType> types, List<ILexNameToken> stateIds,
				List<String> resultIds)
		{
			super(solverInput, types, stateIds, resultIds);
		}

		public VdmPpContext(AbstractEvalElement formula,
				List<ILexNameToken> stateIds, Map<String, PType> stateTypes,
				List<String> resultIds, Map<String, PType> resultTypes)
		{
			super(formula, resultTypes, stateIds, resultIds);
			this.types.putAll(stateTypes);
			this.types.putAll(resultTypes);
		}

	}

	private VdmContext translate(List<PDefinition> stateDefinitions,
			AImplicitOperationDefinition opDef, Map<String, INode> argContext,
			Map<String, INode> stateConstraints) throws AnalysisException,
			SolverException
	{
		VdmToBConverter translator = new VdmToBConverter(console);

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

		Node post = opDef.apply(translator);

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
				name = arg.getKey() + "~";
			}
			AEqualPredicate eqp = new AEqualPredicate(VdmToBConverter.createIdentifier(name), (PExpression) arg.getValue().apply(translator));
			post = new AConjunctPredicate((PPredicate) post, eqp);
		}

		PPredicate p = (PPredicate) post;

		Start s = new Start(new APredicateParseUnit(p), new EOF());

		// s.apply(new ASTPrinter(System.out));

		AbstractEvalElement formula = null;
		try
		{
			formula = new ClassicalB(s);
			console.out.println("Solver input:\n\t\t\t"
					+ formula.getCode().replace("&", " & \n\t\t\t"));
		} catch (Exception e)
		{
			throw new SolverException("Syntax error in: " + post, e);
		}

		AOperationType opType = (AOperationType) opDef.getType();

		List<String> resultIds = new Vector<String>();
		Map<String, PType> resultTypes = new HashMap<String, PType>();

		if (opDef.getResult() != null)
		{
			LexNameList allReturnVariables = PPatternAssistantTC.getAllVariableNames(opDef.getResult().getPattern());
			List<PType> allReturnTypes = new Vector<PType>();
			
			if(allReturnVariables.size()==1)
			{
				allReturnTypes.add(opType.getResult());
			}else
			{
				allReturnTypes.addAll(((AProductType)opType.getResult()).getTypes());
			}
			
			Iterator<ILexNameToken> varItr = allReturnVariables.iterator();
			Iterator<PType> typItr = allReturnTypes.iterator();
			
			while(varItr.hasNext() && typItr.hasNext())
			{
				String id = varItr.next().getName();
				resultIds.add(id);
				resultTypes.put(id, typItr.next());				
			}

		}

		if (stateDef != null)
		{

			return new VdmSlContext(formula, VdmToBConverter.getStateIdToken(stateDef, false), (ARecordInvariantType) stateDef.getRecordType(), resultIds, resultTypes);
		}

		return new VdmPpContext(formula, stateIds, stateTypes, resultIds, resultTypes);

	}

	private IAnimator animator;

	public static void setLoggingLevel(Level level)
	{
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		root.setLevel(level);
	}

	private void initialize() throws BException
	{
		if (animator == null)
		{
			setLoggingLevel(Level.OFF);
			animator = ServletContextListener.INJECTOR.getInstance(IAnimator.class);
			AbstractCommand[] init = {
					/* We load a machine with the token type installed */
					new LoadBProjectFromStringCommand("MACHINE tmp1 SETS TOKEN END"),
					// new LoadBProjectFromStringCommand("MACHINE empty END"),
					new SetPreferenceCommand("CLPFD", "TRUE"),
					new SetPreferenceCommand("BOOL_AS_PREDICATE", "TRUE"),
					new SetPreferenceCommand("MAXINT", "127"),
					new SetPreferenceCommand("MININT", "-128"),
					new SetPreferenceCommand("TIME_OUT", "500"),
					new StartAnimationCommand() };
			animator.execute(init);
		}
	}

	private PStm solve(VdmContext context) throws SolverException
	{
		try
		{
			initialize();
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

				throw new SolverException(message + " - " + e.getMessage(), e);
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

	/**
	 * Extracts the B solution and converts it to VDM expressions and then constructs a VDM block with the assignments
	 * and return value
	 * 
	 * @param context
	 * @param r
	 * @return
	 * @throws SolverException
	 */
	public PStm extractSolution(VdmContext context, EvalResult r)
			throws SolverException
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
								PExp stateExp = BToVdmConverter.convert(slContext.getStateType(), p);
								block.getStatements().add(BToVdmConverter.getStateAssignment(slContext.getStateType(), (AMkTypeExp) stateExp));
							} else
							{
								PExp stateExp = BToVdmConverter.convert(context.types.get(solutionName), p);
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
							PExp retExp = BToVdmConverter.convert(context.types.get(solutionName), p);
							returnExpressions.put(solutionName, retExp);

						} catch (Exception e)
						{
							throw new SolverException("Error converting result expression", e);
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
				resultStm = BToVdmConverter.getReturnStatement(null, returnExpressions.values().iterator().next());
			} else
			{
				List<PExp> exps = new Vector<PExp>();
				// sorting the return expression according to the return type spec
				for (String id : context.resultIds)
				{
					exps.add(returnExpressions.get(id));
				}
				resultStm = BToVdmConverter.getReturnStatement(null, exps);
			}

			// this needs to be inserted last since this will be the return of the operation
			block.getStatements().add(resultStm);
		}

		return block;
	}
}
