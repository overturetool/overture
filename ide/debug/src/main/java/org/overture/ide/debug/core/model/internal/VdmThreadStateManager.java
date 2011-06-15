package org.overture.ide.debug.core.model.internal;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.IDbgpStatus;
import org.overture.ide.debug.core.dbgp.IDbgpStatusInterpreterThreadState;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.model.internal.operations.DbgpDebugger;
import org.overture.ide.debug.core.model.internal.operations.IDbgpDebuggerFeedback;

public class VdmThreadStateManager implements IDbgpDebuggerFeedback {
	public static interface IStateChangeHandler {
		void handleSuspend(int detail);

		void handleResume(int detail);

		void handleTermination(DbgpException e);

		void setInterpreterState(
				IDbgpStatusInterpreterThreadState interpreterThreadState);
	}

	private IStateChangeHandler handler;

	private final DbgpDebugger engine;

	// Abilities of debugging engine
	private volatile boolean canSuspend;

	// Number of suspends
	private volatile int modificationsCount;

	// States
	private volatile boolean stepping;

	private volatile boolean suspended;

	private volatile boolean terminated;

	private volatile boolean stepIntoState;

	private final Object stepIntoLock = new Object();

	private boolean errorState = false;

	protected void handleStatus(DbgpException exception, IDbgpStatus status,
			int suspendDetail) {
		if (exception != null) {
			setTerminated(exception);
		}
		if (status == null) {
			setTerminated(null);
			return;
		}
		
		//set internal VDMJ state
		if(status.getInterpreterThreadState()!=null)
		{
			handler.setInterpreterState(status.getInterpreterThreadState());
		}

		// status is break but with an error, f.e. precondition failure
		if(status.isBreak() && status.reasonError())
		{
			errorState  = true;
		}
		
		
		if (status.isBreak()) {
			setSuspended(true, suspendDetail);
		} else if (status.isStopping()) {
			// Temporary solution!
			try {
				terminate();
			} catch (DebugException e) {
				if (VdmDebugPlugin.DEBUG) {
					e.printStackTrace();
				}
			}
		} else if (status.isStopped()) {
			setTerminated(null);
		}
	}

	// State management
	protected void setSuspended(boolean value, int detail) {
		suspended = value;
		if (value) {
			++modificationsCount;
		}

		if (value) {
			handler.handleSuspend(detail);
		} else {
			handler.handleResume(detail);
		}
	}

	private void setTerminated(DbgpException e) {
		if (!terminated) {
			terminated = true;
			handler.handleTermination(e);
		}
	}

	private boolean canStep() {
		return (!terminated && suspended) && !errorState; 
	}

	private void beginStep(int detail) {
		stepping = true;
		setSuspended(false, detail);
	}

	private void endStep(DbgpException execption, IDbgpStatus status) {
		stepping = false;
		handleStatus(execption, status, DebugEvent.STEP_END);
	}

	public VdmThreadStateManager(VdmThread thread) {
		this.handler = thread;
		this.engine = new DbgpDebugger(thread, this);

		canSuspend = true; // engine.isSupportsAsync();
		this.modificationsCount = 0;

		this.suspended = true;
		this.terminated = false;
		this.stepping = this.suspended;
	}

	public DbgpDebugger getEngine() {
		return engine;
	}

	// Stepping
	public boolean isStepping() {
		return !terminated && stepping;
	}

	// StepInto
	public boolean canStepInto() {
		return canStep();
	}

	public void endStepInto(DbgpException e, IDbgpStatus status) {
		endStep(e, status);
	}

	public void stepInto() throws DebugException {
		beginStep(DebugEvent.STEP_INTO);
		synchronized (stepIntoLock) {
			this.stepIntoState = true;
		}
		engine.stepInto();
	}

	public boolean isStepInto() {
		synchronized (stepIntoLock) {
			return this.stepIntoState;
		}
	}

	public void setStepInto(boolean state) {
		synchronized (stepIntoLock) {
			this.stepIntoState = state;
		}
	}

	// StepOver
	public boolean canStepOver() {
		return canStep();
	}

	public void endStepOver(DbgpException e, IDbgpStatus status) {
		endStep(e, status);
	}

	public void stepOver() throws DebugException {
		beginStep(DebugEvent.STEP_OVER);
		engine.stepOver();
	}

	// StepReturn
	public boolean canStepReturn() {
		return canStep();
	}

	public void endStepReturn(DbgpException e, IDbgpStatus status) {
		endStep(e, status);
	}

	public void stepReturn() throws DebugException {
		beginStep(DebugEvent.STEP_RETURN);
		engine.stepReturn();
	}

	// Suspend
	public boolean isSuspended() {
		return suspended;
	}

	public boolean canSuspend() {
		return canSuspend && !terminated && !suspended;
	}

	public void endSuspend(DbgpException e, IDbgpStatus status) {
		handleStatus(e, status, DebugEvent.CLIENT_REQUEST);
	}

	public void suspend() throws DebugException {
		engine.suspend();
		setSuspended(true, DebugEvent.CLIENT_REQUEST);
	}

	public int getModificationsCount() {
		return modificationsCount;
	}

	// Resume
	public boolean canResume() {
		return (!terminated && suspended) && !errorState;
	}

	public void endResume(DbgpException e, IDbgpStatus status) {
		handleStatus(e, status, DebugEvent.BREAKPOINT);
	}

	public void resume() throws DebugException {
		setSuspended(false, DebugEvent.CLIENT_REQUEST);
		
		engine.resume();
	}

	// Terminate
	public boolean isTerminated() {
		return terminated;
	}

	public boolean canTerminate() {
		return !terminated;
	}

	public void endTerminate(DbgpException e, IDbgpStatus status) {
		handleStatus(e, status, DebugEvent.CLIENT_REQUEST);
	}

	public void terminate() throws DebugException {
		engine.terminate();
	}

	public void notifyModified() {
		modificationsCount++;
	}
}
