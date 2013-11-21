package org.overture.modelcheckers.probsolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.modelcheckers.probsolver.visitors.BToVdmConverter;
import org.overture.modelcheckers.probsolver.visitors.VdmToBConverter;
import org.overture.parser.util.ParserUtil;

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

		AModuleModules module = opDef.getAncestor(AModuleModules.class);
		AStateDefinition state = null;

		for (PDefinition def : module.getDefs())
		{
			if (def instanceof AStateDefinition)
			{
				state = (AStateDefinition) def;
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

			VdmContext context = translate(state, opDef, arguments, stateConstraints);

			PStm val = solve(context);
			return val;
		} catch (AnalysisException e)
		{
			throw new SolverException("Internal error in translation of the specification and post condition", e);
		}
	}

	private static class VdmContext
	{
		public final String resultId;
		public final PType resultType;
		public final String stateId;
		public final ARecordInvariantType stateType;
		public final AbstractEvalElement solverInput;

		public VdmContext(String stateId, ARecordInvariantType stateType,
				String resultId, PType resultType,
				AbstractEvalElement solverInput)
		{
			this.resultId = resultId;
			this.resultType = resultType;
			this.stateId = stateId;
			this.stateType = stateType;
			this.solverInput = solverInput;
		}
	}

	private VdmContext translate(AStateDefinition state,
			AImplicitOperationDefinition opDef, Map<String, INode> argContext,
			Map<String, INode> stateConstraints) throws AnalysisException,
			SolverException
	{
		VdmToBConverter translator = new VdmToBConverter(console);

		Node statePredicate = state.apply(translator);

		Node post = opDef.apply(translator);

		post = new AConjunctPredicate((PPredicate) statePredicate, (PPredicate) post);

		// add argument constraints
		for (Entry<String, INode> arg : argContext.entrySet())
		{
			AEqualPredicate eqp = new AEqualPredicate(VdmToBConverter.createIdentifier(arg.getKey()), (PExpression) arg.getValue().apply(translator));
			post = new AConjunctPredicate((PPredicate) post, eqp);
		}

		// add state constraints
		for (Entry<String, INode> arg : stateConstraints.entrySet())
		{
			AEqualPredicate eqp = new AEqualPredicate(VdmToBConverter.createIdentifier(VdmToBConverter.getStateId(state, true)
					+ "'" + arg.getKey()), (PExpression) arg.getValue().apply(translator));
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
		String resultId = null;

		// FIXME: this is only ok for single return value and not r,b : Nat
		if (opDef.getResult() != null)
		{
			resultId = opDef.getResult().getPattern().toString();
		}

		return new VdmContext(VdmToBConverter.getStateId(state, false), (ARecordInvariantType) state.getRecordType(), resultId, opType.getResult(), formula);
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
				// message += solverResult.toString();
				if (solverResult instanceof ComputationNotCompletedResult)
				{
					ComputationNotCompletedResult r = (ComputationNotCompletedResult) solverResult;
					throw new SolverException(r.getReason());
				} else if (solverResult instanceof EvalResult)
				{
					EvalResult r = (EvalResult) solverResult;
					// String message = "";
					// message += r.getValue();
					// message += "\n\tSolutions:";

					ABlockSimpleBlockStm block = AstFactory.newABlockSimpleBlockStm(new LexLocation(), new Vector<AAssignmentDefinition>());
					PStm resultStm = null;

					console.out.println("Solver solution:");

					for (Entry<String, String> entry : r.getSolutions().entrySet())
					{
						String solutionName = entry.getKey();
						if (solutionName.equals(context.stateId)
								|| solutionName.equals(context.resultId))
						{
							ClassicalB b = new ClassicalB(entry.getValue());
							PParseUnit p = b.getAst().getPParseUnit();
							console.out.println("\t\t\t" + solutionName + " = "
									+ b.getCode());

							if (solutionName.equals(context.stateId))
							{
								try
								{
									PExp stateExp = BToVdmConverter.convert(context.stateType, p);
									block.getStatements().add(BToVdmConverter.getStateAssignment(context.stateType, (AMkTypeExp) stateExp));

								} catch (Exception e)
								{
									throw new SolverException("Error converting state expression", e);
								}
							} else if (solutionName.equals(context.resultId))
							{
								try
								{
									PExp retExp = BToVdmConverter.convert(context.resultType, p);
									resultStm = BToVdmConverter.getReturnStatement(context.resultType, retExp);

								} catch (Exception e)
								{
									throw new SolverException("Error converting result expression", e);
								}
							}
						}
					}

					if (resultStm != null)
					{
						// this needs to be inserted last since this will be the return of the operation
						block.getStatements().add(resultStm);
					}

					return block;

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
}
