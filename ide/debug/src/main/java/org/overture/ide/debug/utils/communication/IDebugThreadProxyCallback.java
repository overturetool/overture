package org.overture.ide.debug.utils.communication;


public interface IDebugThreadProxyCallback
{
	/*
	 * Called on console output
	 * 
	 * @param output true on sending and false on receive
	 * 
	 * @param message the message send
	 */
	void firePrintMessage(boolean output, String message);

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

	

}
