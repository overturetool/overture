package org.overture.interpreter.solver;

import org.overture.ast.lex.Dialect;

public class SolverFactory
{

	public static IConstraintSolver getSolver(Dialect dialect)
	{
		try
		{
			Class<?> delegateClass = dialect.getClass().getClassLoader().loadClass("org.overture.modelcheckers.probsolver.ProbSolverIntegration");
			if (IConstraintSolver.class.isAssignableFrom(delegateClass))
			{
				return (IConstraintSolver) delegateClass.newInstance();
			}
		} catch (Exception e)
		{
			//ignore
		}
		return null;
	}

}
