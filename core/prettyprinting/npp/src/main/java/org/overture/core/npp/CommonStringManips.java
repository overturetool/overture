package org.overture.core.npp;

/**
 * The Class CommonStringManips provides a small library of static methods that
 * perform string manipulations. These manipulations are simples ones used
 * throughout the visitors of the pretty printer.
 */
public abstract class CommonStringManips {

	/**
	 * Wrap a string in parenthesis.
	 * 
	 * @param s
	 *            the string to be wrapped
	 * 
	 * @return the wrapped string: <code>"(s)"</code>
	 */
	public static String wrap(String s) {
		StringBuilder sb = new StringBuilder();

		sb.append("(");
		sb.append(s);
		sb.append(")");

		return sb.toString();
	}

}
