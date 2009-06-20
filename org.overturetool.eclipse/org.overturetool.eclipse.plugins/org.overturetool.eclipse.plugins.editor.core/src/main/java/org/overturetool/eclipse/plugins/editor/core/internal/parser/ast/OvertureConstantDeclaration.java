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
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.utils.CorePrinter;

public class OvertureConstantDeclaration extends ASTNode {

	private final ASTNode path;
	private final SimpleReference name;
	private final ASTNode value;

	public OvertureConstantDeclaration(int start, int end, ASTNode path,
			SimpleReference name, ASTNode value) {
		super(start, end);
		this.path = path;
		this.name = name;
		this.value = value;
	}

	public SimpleReference getName() {
		return name;
	}

	public ASTNode getPath() {
		return path;
	}

	public ASTNode getValue() {
		return value;
	}

	public int getKind() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void printNode(CorePrinter output) {
		output.formatPrint("ConstantDeclaration" + this.getSourceRange().toString() + ":(" + this.getName() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void traverse(ASTVisitor pVisitor) throws Exception {
		if (pVisitor.visit(this)) {
			if (path != null) {
				path.traverse(pVisitor);
			}
			if (name != null) {
				name.traverse(pVisitor);
			}
			if (value != null) {
				value.traverse(pVisitor);
			}
			pVisitor.endvisit(this);
		}
	}
	
}
