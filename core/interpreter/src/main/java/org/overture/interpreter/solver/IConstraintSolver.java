package org.overture.interpreter.solver;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.messages.Redirector;

public interface IConstraintSolver
{
	/**
	 * Solve implicit operations
	 * 
	 * @param ast
	 *            the complete specification. This is used to search for context elements for the translation.
	 * @param name
	 * @param impldef
	 * @param stateExps
	 * @param argExps
	 * @param out
	 * @param err
	 * @return
	 * @throws Exception
	 */
	PStm solve(Collection<? extends INode> ast, String name,
			AImplicitOperationDefinition impldef,
			Map<String, String> stateExps, Map<String, String> argExps,
			PrintWriter out, PrintWriter err) throws Exception;

	/**
	 * Solve expressions that represents the body of functions
	 * 
	 * @param ast
	 *            the complete specification. This is used to search for context elements for the translation.
	 * @param name
	 * @param body
	 * @param result
	 * @param stateExps
	 * @param argExps
	 * @param out
	 * @param err
	 * @return
	 * @throws Exception
	 */
	PExp solve(Collection<? extends INode> ast, String name, PExp body,
			APatternTypePair result, Map<String, String> stateExps,
			Map<String, String> argExps, Redirector out, Redirector err)
			throws Exception;

}
