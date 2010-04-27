package org.overture.ide.debug.utils.communication;

import org.overturetool.vdmj.scheduler.RunState;


public interface IDebugThreadProxyCallback
{
	/**
	 * Step start detail. Indicates a thread was resumed by a step
	 * into action.
	 * @since 2.0
	 */
	public static final int STEP_INTO= 0x0001;
	
	/**
	 * Step start detail. Indicates a thread was resumed by a step
	 * over action.
	 * @since 2.0
	 */
	public static final int STEP_OVER= 0x0002;
	
	/**
	 * Step start detail. Indicates a thread was resumed by a step
	 * return action.
	 * @since 2.0
	 */
	public static final int STEP_RETURN= 0x0004;		

	/**
	 * Step end detail. Indicates a thread was suspended due
	 * to the completion of a step action.
	 */
	public static final int STEP_END= 0x0008;
	
	/*
	 * Called on console output
	 * 
	 * @param output true on sending and false on receive
	 * 
	 * @param message the message send
	 */
	void firePrintMessage(boolean output, String message);
	/*
	 * Called on console output
	 * 
	 * @param output true on sending and false on receive
	 * 
	 * @param message the message send
	 */
	void firePrintErrorMessage(boolean output, String message);

	/*
	 * Prints stdout
	 * 
	 * @param text the text to print
	 */
	void firePrintOut(String text);

	/*
	 * Prints stderr
	 * 
	 * @param text the error to print
	 */
	void firePrintErr(String text);

	/*
	 * Raised on breakpoint hit event
	 */
	void fireBreakpointHit();

	/*
	 * Called when debugging is stopped
	 */
	void fireStopped();

	/*
	 * Called when debugging is started
	 */
	void fireStarted();

	void fireBreakpointSet(Integer tid, Integer breakpointId);

	void suspended();
	
	void deadlockDetected();
	
	/**
	 * Update info about internal state of debugger thread
	 * @param id The internal debugger id
	 * @param name The internal name
	 * @param state The current internal state
	 */
	void updateInternalState(String id, String name, RunState state);

	

}
