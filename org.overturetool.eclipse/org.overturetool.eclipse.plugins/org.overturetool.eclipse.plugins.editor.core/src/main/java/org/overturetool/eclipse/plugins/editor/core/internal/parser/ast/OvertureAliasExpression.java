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

public class OvertureAliasExpression extends ASTNode {

	private final String oldValue;
	private final String newValue;
	
	public String getOldValue() {
		return oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public OvertureAliasExpression(int start, int end, String oldValue, String newValue) {
		super(start, end);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {			
			visitor.endvisit(this);
		}
	}	

}
