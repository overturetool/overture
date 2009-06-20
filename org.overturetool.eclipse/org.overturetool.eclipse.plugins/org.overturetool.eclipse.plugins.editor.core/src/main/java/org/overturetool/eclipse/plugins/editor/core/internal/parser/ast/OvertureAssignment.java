/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.core.internal.parser.ast;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.utils.CorePrinter;


/**
 * Assignment expression used to hold a = b expressions.
 */
public class OvertureAssignment extends OvertureBinaryExpression
{
	
	public static final OvertureAssignment[] EMPTY_ARRAY = new OvertureAssignment[0];

	/**
	 * Construct from left, right and type expression. Used to construct NotStrictAssignment class.
	 * 
	 * @param left
	 * @param type
	 * @param right
	 */
	protected OvertureAssignment( ASTNode left, int type, ASTNode right ) {

		super( left, type, right );
	}

	/**
	 * Construct default strict assignment.
	 * 
	 * @param left
	 * @param right
	 */
	public OvertureAssignment( ASTNode left, ASTNode right ) {

		super( left, E_ASSIGN, right );
	}

	/**
	 * Convert to string in pattern: "left = right"
	 */
	public String toString( ) {
		return String.valueOf(getLeft()) + '=' + String.valueOf(getRight());
	}

	/**
	 * Testing purposes only. Used to print expression.
	 */
	public void printNode( CorePrinter output ) {

		if( getLeft() != null ) {
			getLeft().printNode( output );
		}
		output.formatPrintLn( " = " ); //$NON-NLS-1$
		if( getRight() != null ) {
			getRight().printNode( output );
		}
	}
}
