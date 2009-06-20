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
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.expressions.ExpressionConstants;

/**
 * Base binary class for binary expressions.
 */
public class OvertureBinaryExpression extends ASTNode implements ExpressionConstants {
	private ASTNode left;
	private ASTNode right;
	
	protected int kind;

	public OvertureBinaryExpression(ASTNode left, int kind, ASTNode right) {
		if (left != null) {
			this.setStart(left.sourceStart());
		}

		if (right != null) {
			this.setEnd(right.sourceEnd());
		}

		this.kind = kind;
		this.left = left;
		this.right = right;
	}

	public ASTNode getLeft() {
		return left;
	}

	public ASTNode getRight() {
		return right;
	}

	public void traverse(ASTVisitor pVisitor) throws Exception {
		if (pVisitor.visit(this)) {
			if (left != null) {
				left.traverse(pVisitor);
			}

			if (right != null) {
				right.traverse(pVisitor);
			}

			pVisitor.endvisit(this);
		}
	}	

	public int getKind() {
		return kind;
	}

}
