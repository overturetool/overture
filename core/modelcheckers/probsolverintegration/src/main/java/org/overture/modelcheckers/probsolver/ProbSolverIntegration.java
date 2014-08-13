package org.overture.modelcheckers.probsolver;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.PType;
import org.overture.interpreter.messages.Redirector;
import org.overture.interpreter.solver.IConstraintSolver;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ProbSolverIntegration implements IConstraintSolver
{

	final ITypeCheckerAssistantFactory af;

	public ProbSolverIntegration(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PStm solve(Collection<? extends INode> ast, String name,
			AImplicitOperationDefinition opDef, Map<String, String> stateExps,
			Map<String, String> argExps, PrintWriter out, PrintWriter err)
			throws Exception
	{
		return ProbSolverUtil.solve(name, opDef, stateExps, argExps, new HashMap<String, PType>(), calculateTokenType(ast), calculateQuoteNames(ast), new SolverConsole(out, err), af);
	}

	@Override
	public PExp solve(Collection<? extends INode> ast, String name, PExp body,
			APatternTypePair result, Map<String, String> stateExps,
			Map<String, String> argExps, Redirector out, Redirector err)
			throws Exception
	{
		return ProbSolverUtil.solve(name, body, result, stateExps, argExps, new HashMap<String, PType>(), calculateTokenType(ast), calculateQuoteNames(ast), new SolverConsole(out, err), af);
	}

	/**
	 * Scans all mk_token expressions for their argument type
	 * 
	 * @param defs
	 * @return
	 * @throws AnalysisException
	 */
	public PType calculateTokenType(Collection<? extends INode> defs)
			throws AnalysisException
	{
		final TokenTypeCalculator tokenTypeFinder = new TokenTypeCalculator(af);
		for (INode d : defs)
		{
			d.apply(tokenTypeFinder);
		}
		return tokenTypeFinder.getTokenType();
	}

	/**
	 * Scans all quotes for their name and collect them
	 * 
	 * @param defs
	 * @return
	 * @throws AnalysisException
	 */
	public Set<String> calculateQuoteNames(Collection<? extends INode> defs)
			throws AnalysisException
	{
		final QuoteLiteralFinder finder = new QuoteLiteralFinder();
		for (INode d : defs)
		{
			d.apply(finder);
		}
		return finder.getQuoteLiterals();
	}

}
