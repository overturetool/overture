//package org.overture.modelcheckers.probsolver;
//
//import static org.junit.Assert.assertNotEquals;
//import static org.junit.Assert.fail;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map.Entry;
//
//import org.junit.Assert;
//import org.junit.Assume;
//import org.junit.Before;
//import org.junit.Test;
//import org.overture.ast.analysis.AnalysisException;
//import org.overture.ast.definitions.AImplicitOperationDefinition;
//import org.overture.ast.definitions.AStateDefinition;
//import org.overture.ast.definitions.PDefinition;
//import org.overture.ast.definitions.SOperationDefinition;
//import org.overture.ast.lex.Dialect;
//import org.overture.ast.modules.AModuleModules;
//import org.overture.config.Release;
//import org.overture.config.Settings;
//import org.overture.modelcheckers.probsolver.AbstractProbSolverUtil.SolverException;
//import org.overture.modelcheckers.probsolver.visitors.VdmToBConverter;
//import org.overture.typechecker.util.TypeCheckerUtil;
//import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;
//
//import ch.qos.logback.classic.Level;
//import de.be4.classicalb.core.parser.exceptions.BException;
//import de.be4.classicalb.core.parser.node.AConjunctPredicate;
//import de.be4.classicalb.core.parser.node.APredicateParseUnit;
//import de.be4.classicalb.core.parser.node.EOF;
//import de.be4.classicalb.core.parser.node.Node;
//import de.be4.classicalb.core.parser.node.PPredicate;
//import de.be4.classicalb.core.parser.node.Start;
//import de.prob.animator.IAnimator;
//import de.prob.animator.command.AbstractCommand;
//import de.prob.animator.command.CbcSolveCommand;
//import de.prob.animator.command.LoadBProjectFromStringCommand;
//import de.prob.animator.command.SetPreferenceCommand;
//import de.prob.animator.command.StartAnimationCommand;
//import de.prob.animator.domainobjects.AbstractEvalElement;
//import de.prob.animator.domainobjects.ClassicalB;
//import de.prob.animator.domainobjects.ComputationNotCompletedResult;
//import de.prob.animator.domainobjects.EvalResult;
//import de.prob.animator.domainobjects.IEvalResult;
//import de.prob.webconsole.ServletContextListener;
//
//public class AstTestConverter
//{
//
//	private IAnimator animator;
//
//	@Before
//	public void setup() throws BException
//	{
//		Settings.dialect = Dialect.VDM_SL;
//		Settings.release = Release.VDM_10;
//		try
//		{
//			setLoggingLevel(Level.OFF);
//			animator = ServletContextListener.INJECTOR.getInstance(IAnimator.class);
//			AbstractCommand[] init = {
//					new LoadBProjectFromStringCommand("MACHINE tmp1 SETS TOKEN END"),
//					// new LoadBProjectFromStringCommand("MACHINE empty END"),
//					new SetPreferenceCommand("CLPFD", "TRUE"),
//					new SetPreferenceCommand("BOOL_AS_PREDICATE", "TRUE"),
//					new SetPreferenceCommand("MAXINT", "127"),
//					new SetPreferenceCommand("MININT", "-128"),
//					new SetPreferenceCommand("TIME_OUT", "500"),
//					new StartAnimationCommand() };
//
//			animator.execute(init);
//		} catch (Exception e)
//		{
//			animator = null;
//		}
//
//	}
//
//	public static void setLoggingLevel(Level level)
//	{
//		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
//		root.setLevel(level);
//	}
//
//	@Test
//	public void testInit() throws IOException
//	{
//		testMethod("Init");
//	}
//
//	@Test
//	public void testGivePermission() throws IOException
//	{
//		testMethod("GivePermission");
//	}
//
//	@Test
//	public void testRecordLanding() throws IOException
//	{
//		testMethod("RecordLanding");
//	}
//
//	@Test
//	public void testRecordTakeOff() throws IOException
//	{
//		testMethod("RecordTakeOff");
//	}
//
//	@Test
//	public void testNumberWaiting() throws IOException
//	{
//		testMethod("NumberWaiting");
//	}
//
//	private void testMethod(String name) throws IOException
//	{
//		System.out.println("----------------- Running Solver on operation: "
//				+ name + " ---------------------------");
//		try
//		{
//			File f = new File("src/test/resources/modules/AirportNat.vdmsl".replace('/', File.separatorChar));
//			List<AModuleModules> modules = parse(f);
//
//			// SOperationDefinition postExp = null;
//			// AStateDefinition state = null;
//			AImplicitOperationDefinition opDef = null;
//			final TokenTypeCalculator tokenTypeFinder = new TokenTypeCalculator();
//
//			for (PDefinition d : modules.get(0).getDefs())
//			{
//				d.apply(tokenTypeFinder);
//				if (d instanceof AImplicitOperationDefinition
//						&& d.getName().getName().equals(name))
//				{
//					// postExp = (AImplicitOperationDefinition) d;
//					opDef = (AImplicitOperationDefinition) d;
//					break;
//				} else if (d instanceof AStateDefinition)
//				{
//					// state = (AStateDefinition) d;
//				}
//
//			}
//
//			// solve(postExp, state);
//
//			HashMap<String, String> emptyMap = new HashMap<String, String>();
//			ProbSolverUtil.solve(opDef.getName().getName(), opDef, emptyMap, emptyMap, ProbConverterTestBase.getArgTypes(opDef), tokenTypeFinder.getTokenType(), new SolverConsole());
//
//		} catch (AnalysisException e)
//		{
//		} catch (SolverException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	public void solve(SOperationDefinition opDef, AStateDefinition state)
//			throws IOException
//	{
//		if (animator == null)
//		{
//			Assume.assumeNotNull(animator);
//			return;
//		}
//		try
//		{
//			VdmToBConverter translator = new VdmToBConverter();
//
//			Node statePredicate = state.apply(translator);
//
//			Node post = opDef.apply(translator);
//
//			post = new AConjunctPredicate((PPredicate) statePredicate, (PPredicate) post);
//
//			PPredicate p = (PPredicate) post;
//
//			Start s = new Start(new APredicateParseUnit(p), new EOF());
//
//			// s.apply(new ASTPrinter(System.out));
//
//			AbstractEvalElement formula = null;
//			try
//			{
//				formula = new ClassicalB(s);
//				System.out.println("Code:\n\t"
//						+ formula.getCode().replace("&", " & "));
//			} catch (Exception e)
//			{
//				fail("Syntax error in: " + post);
//				return;
//
//			}
//
//			boolean error = false;
//			CbcSolveCommand command = new CbcSolveCommand(formula);
//			// http://nightly.cobra.cs.uni-duesseldorf.de/cli/
//			IEvalResult solverResult = null;
//			String message = "";
//
//			try
//			{
//
//				animator.execute(command);
//				solverResult = command.getValue();
//			} catch (Exception e)
//			{
//				if (e.getMessage().startsWith("Uncovered boolean expression"))
//				{
//					message = "not a predicate";
//				} else
//				{
//					message = "error";
//				}
//				error = true;
//			}
//
//			if (solverResult != null)
//			{
//				if (solverResult instanceof ComputationNotCompletedResult)
//				{
//					ComputationNotCompletedResult r = (ComputationNotCompletedResult) solverResult;
//					message += r.getReason();
//				} else if (solverResult instanceof EvalResult)
//				{
//					EvalResult r = (EvalResult) solverResult;
//					message += r.getValue();
//					message += "\n\tSolutions:";
//					for (Entry<String, String> entry : r.getSolutions().entrySet())
//					{
//						message += "\n\t\t" + entry.getKey() + " = "
//								+ entry.getValue();
//					}
//				}
//			}
//
//			System.out.println("Java prob: " + message);
//
//			if (message.equals("cannot be solved") || message.contains("error"))
//			{
//				System.out.println("Trying CLI solver");
//				if (ProbCliSolver.solve(formula.getCode(), true))
//				{
//					System.out.println("\tCli solver could solve it");
//				}
//			}
//			assertNotEquals(message, "cannot be solved");
//			if (error || message.contains("error")
//					|| message.contains("cannot be solved"))
//			{
//				fail(message);
//			}
//
//		} catch (AnalysisException e)
//
//		{
//			e.printStackTrace();
//			Assert.fail("Internal error");
//		} catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
//
//	public String padRight(String text, int count)
//	{
//		while (text.length() < count)
//		{
//			text += " ";
//		}
//		return text;
//	}
//
//	private List<AModuleModules> parse(File file) throws AnalysisException
//	{
//		if (file == null || !file.exists())
//		{
//			throw new AnalysisException("No expression to generate from");
//		}
//
//		TypeCheckResult<List<AModuleModules>> typeCheckResult = null;
//		try
//		{
//			typeCheckResult = TypeCheckerUtil.typeCheckSl(file);
//		} catch (Exception e)
//		{
//			throw new AnalysisException("Unable to type check expression: "
//					+ file + ". Message: " + e.getMessage());
//		}
//
//		if (!typeCheckResult.errors.isEmpty()
//				|| !typeCheckResult.parserResult.errors.isEmpty())
//		{
//			throw new AnalysisException("Unable to type check expression: "
//					+ file);
//		}
//
//		return typeCheckResult.result;
//	}
// }
