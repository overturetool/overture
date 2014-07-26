package org.overture.core.npp;

/**
 * The Class Utilities provides a small library of static methods that
 * perform string manipulations. These manipulations are simples ones used
 * throughout the visitors of the pretty printer.
 */
public abstract class Utilities {

	private static String space = " ";
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
	
	/**
	 * Create the String to be wrapped for the most common binary expressions.
	 * @param left
	 * The string representation of the left side of the binary expression.
	 * @param right
	 * The string representation of the right side of the binary expression.
	 * @param op
	 * The binary operation's symbol or String in string format.
	 * 
	 * @return the created string. 
	 */
	
	public static String append(String left, String right, String op)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(left);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(right);
		
		return (sb.toString());
	}

}
