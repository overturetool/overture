package org.overture.ide.debug.core.dbgp;

public interface IDbgpStatusInterpreterThreadState
{
	public enum InterpreterThreadStatus
	{
		CREATED, RUNNABLE, RUNNING, LOCKING, WAITING, TIMESTEP, COMPLETE
	};

	int getId();

	String getName();

	InterpreterThreadStatus getState();
}
