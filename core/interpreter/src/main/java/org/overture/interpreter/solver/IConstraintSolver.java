package org.overture.interpreter.solver;

import java.io.PrintWriter;
import java.util.Map;

import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.statements.PStm;

public interface IConstraintSolver
{
	PStm solve(ILexNameToken name, AImplicitOperationDefinition impldef,
			Map<String, String> stateExps, Map<String, String> argExps,
			PrintWriter out, PrintWriter err) throws Exception;

}
