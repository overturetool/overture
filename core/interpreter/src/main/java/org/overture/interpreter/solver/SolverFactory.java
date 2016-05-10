package org.overture.interpreter.solver;

import org.overture.ast.lex.Dialect;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

public class SolverFactory
{

	private SolverFactory() {
	}

	public static IConstraintSolver getSolver(Dialect dialect)
	{
		try
		{
			Class<?> delegateClass = dialect.getClass().getClassLoader().loadClass("org.overture.modelcheckers.probsolver.ProbSolverIntegration");
			if (IConstraintSolver.class.isAssignableFrom(delegateClass))
			{
				return (IConstraintSolver) delegateClass.getConstructor(ITypeCheckerAssistantFactory.class).newInstance(new TypeCheckerAssistantFactory());
			}
		} catch (Exception e)
		{
			// ignore
		}
		return null;
	}

}
