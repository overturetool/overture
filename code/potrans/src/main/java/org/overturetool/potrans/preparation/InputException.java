/**
 * 
 */
package org.overturetool.potrans.preparation;

/**
 * @author miguel_ferreira
 *
 */
public class InputException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1917475133966179639L;

	/**
	 * 
	 */
	public InputException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InputException(String message, Throwable cause) {
		super(message, cause);
	}

}
