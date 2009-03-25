/**
 * 
 */
package org.overturetool.potrans.preparation;

/**
 * Signals that an exception has occurred during the invocation of a command. 
 * @author Miguel Ferreira
 *
 */
public class CommandLineException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4704674991014634560L;

	/**
	 * Creates a new CommandLineException.
	 */
	public CommandLineException() {
		super();
	}

	/**
	 * Creates a new CommandLineException.
	 * @param message the message associated with the exception
	 */
	public CommandLineException(String message) {
		super(message);
	}

	/**
	 * Creates a new CommandLineException.
	 * @param cause the cause for the exception
	 */
	public CommandLineException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new CommandLineException.
	 * @param message the message associated with the exception
	 * @param cause   the cause for the exception
	 */
	public CommandLineException(String message, Throwable cause) {
		super(message, cause);
	}

}
