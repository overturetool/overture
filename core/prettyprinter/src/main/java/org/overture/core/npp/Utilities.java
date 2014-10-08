/*
 * #%~
 * New Pretty Printer
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
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
	
	/**
	 * Create the String to be wrapped for the most common Unary expressions.
	 * @param right
	 * The string representation of the expression.
	 * @param op
	 * The binary operation's symbol or String in string format.
	 * 
	 * @return the created string. 
	 */
	public static String unaryappend(String right, String op)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(op);
		sb.append(space);
		sb.append(right);
		
		return (sb.toString());
	}
}
