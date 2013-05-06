package org.overture.interpreter.assistant.statement;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.AAtomicStm;
import org.overture.ast.statements.PStm;

public class AAtomicStmAssistantInterpreter
{

	private static final Set<Thread> atomicThreads = new HashSet<Thread>();
	
	public static PExp findExpression(AAtomicStm stm, int lineno)
	{
		PExp found = null;

		for (AAssignmentStm stmt: stm.getAssignments())
		{
			found = AAssignmentStmAssistantInterpreter.findExpression(stmt,lineno);
			if (found != null) break;
		}

		return found;
	}

	public static PStm findStatement(AAtomicStm stm, int lineno)
	{
		PStm found = PStmAssistantInterpreter.findStatementBaseCase(stm, lineno);
		if (found != null) return found;

		for (AAssignmentStm stmt: stm.getAssignments())
		{
			found = PStmAssistantInterpreter.findStatement(stmt,lineno);
			if (found != null) break;
		}

		return found;
	}
	
	/*
	 * State invariants are switched on/off in the exec method directly, but
	 * type invariants may be affected in arbitrary places, so these methods
	 * record the (per thread) fact that we are inside an atomic statement.
	 */
	
	public static synchronized void addAtomicThread()
	{
		atomicThreads.add(Thread.currentThread());
	}
	
	public static synchronized void removeAtomicThread()
	{
		atomicThreads.remove(Thread.currentThread());
	}
	 
	public static synchronized boolean insideAtomic()
	{
		return atomicThreads.contains(Thread.currentThread());
	}

}
