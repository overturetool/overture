package org.overture.interpreter.solver;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.PType;
import org.overture.interpreter.messages.Redirector;

public interface IConstraintSolver
{
	/**
	 * Solve implicit operations
	 * 
	 * @param name
	 * @param impldef
	 * @param stateExps
	 * @param argExps
	 * @param out
	 * @param err
	 * @return
	 * @throws Exception
	 */
	PStm solve(String name, AImplicitOperationDefinition impldef,
			Map<String, String> stateExps, Map<String, String> argExps, PType tokenType,
			PrintWriter out, PrintWriter err) throws Exception;

	/**
	 * Solve expressions that represents the body of functions
	 * 
	 * @param name
	 * @param body
	 * @param stateExps
	 * @param argExps
	 * @param out
	 * @param err
	 * @return
	 * @throws Exception
	 */
	PExp solve(String name, PExp body, APatternTypePair result,
			Map<String, String> stateExps, Map<String, String> argExps, PType tokenType,
			Redirector out, Redirector err) throws Exception;
	
	
	/**
	 * Scans all mk_token expressions for their argument type
	 * @param defs
	 * @return
	 * @throws AnalysisException 
	 */
	public PType calculateTokenType(List<PDefinition> defs) throws AnalysisException;

}
