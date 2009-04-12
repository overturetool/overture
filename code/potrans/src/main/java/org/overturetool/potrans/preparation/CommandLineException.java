/**
 * 
 */
package org.overturetool.potrans.preparation;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Signals that an exception has occurred during the invocation of a command. 
 * @author miguel_ferreira
 *
 */
public class CommandLineException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4704674991014634560L;

	/**
	 * time, command, message
	 */
	private static final String messageFormat = "[%s] %s: %s";
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss,SSS");
	
	private final String command;
	
	/**
	 * Creates a new CommandLineException.
	 */
	public CommandLineException(String command, String message) {
		super(String.format(messageFormat, getDate(), command, message));
		this.command = command;
	}

	/**
	 * Creates a new CommandLineException.
	 * @param message the message associated with the exception
	 * @param cause   the cause for the exception
	 */
	public CommandLineException(String command, String message, Throwable cause) {
		super(String.format(messageFormat, getDate(), command, message), cause);
		this.command = command;
	}

	/**
	 * @return
	 */
	protected static String getDate() {
		return dateFormat.format(new Date(System.currentTimeMillis()));
	}
	
	public String getCommand() {
		return command;
	}
}
