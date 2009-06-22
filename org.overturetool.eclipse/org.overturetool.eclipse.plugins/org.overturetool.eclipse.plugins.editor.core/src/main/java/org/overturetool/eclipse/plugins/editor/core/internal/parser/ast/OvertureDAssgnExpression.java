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
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.references.VariableReference;

public class OvertureDAssgnExpression extends OvertureAssignment {

	private SimpleReference left;

	public OvertureDAssgnExpression(int start, int end, String name, ASTNode value) {
		super(null, value);
		this.left = new VariableReference(start, start + name.length(), name);
		this.setStart(start);
		this.setEnd(end);
	}

	public String getName() {
		return left.getName();
	}

	public void setName(String name) {
		left.setName(name);
	}

	public int getKind() {
		return 0;
	}

//	public void traverse(ASTVisitor visitor) throws Exception {
//		if (visitor.visit(this)) {	
//			if ()
//			visitor.endvisit(this);
//		}
//	}

	public ASTNode getLeft() {
		return left;
	}

	
	
}
