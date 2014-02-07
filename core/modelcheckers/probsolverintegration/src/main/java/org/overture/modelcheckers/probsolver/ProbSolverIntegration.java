package org.overture.modelcheckers.probsolver;

import java.io.PrintWriter;
import java.util.Map;

import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.messages.Redirector;
import org.overture.interpreter.solver.IConstraintSolver;

public class ProbSolverIntegration implements IConstraintSolver
{
	@Override
	public PStm solve(String name, AImplicitOperationDefinition opDef,
			Map<String, String> stateExps, Map<String, String> argExps,
			PrintWriter out, PrintWriter err) throws Exception
	{
		return ProbSolverUtil.solve(name, opDef, stateExps, argExps, new SolverConsole(out, err));
	}

	@Override
	public PExp solve(String name, PExp body, APatternTypePair result,
			Map<String, String> stateExps, Map<String, String> argExps,
			Redirector out, Redirector err) throws Exception
	{
		return ProbSolverUtil.solve(name, body, result, stateExps, argExps, new SolverConsole(out, err));
	}
}
