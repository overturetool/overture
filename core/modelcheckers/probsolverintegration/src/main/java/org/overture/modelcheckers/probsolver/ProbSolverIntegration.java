package org.overture.modelcheckers.probsolver;

import java.io.PrintWriter;
import java.util.Map;

import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.solver.IConstraintSolver;


public class ProbSolverIntegration implements IConstraintSolver
{
	@Override
	public PStm solve(ILexNameToken name, AImplicitOperationDefinition opDef,
			Map<String, String> stateExps, Map<String, String> argExps,
			PrintWriter out, PrintWriter err) throws Exception
	{
		return ProbSolverUtil.solve(name, opDef, stateExps, argExps, new SolverConsole(out, err));
	}
}
