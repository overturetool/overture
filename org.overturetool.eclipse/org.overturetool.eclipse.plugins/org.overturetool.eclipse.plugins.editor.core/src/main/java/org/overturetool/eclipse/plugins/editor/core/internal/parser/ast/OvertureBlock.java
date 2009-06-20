/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.core.internal.parser.ast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;

public class OvertureBlock extends ASTNode {

	private Map vars = new HashMap();
	private ASTNode asterixParameter = null;
	private ASTNode body;

	public OvertureBlock(int start, int end, ASTNode body) {
		super(start, end);
		this.body = body;
	}

	public ASTNode getAsterixParameter() {
		return asterixParameter;
	}

	public void setAsterixParameter(ASTNode asterixParameter) {
		this.asterixParameter = asterixParameter;
	}

	public OvertureBlock(int start, int end) {
		super(start, end);
	}

	public Set getVars() {
		return vars.keySet();
	}

	public void addVar(ASTNode var) {
		addVar(var, null);
	}

	public void addVar(ASTNode var, ASTNode defaultValue) {
		vars.put(var, defaultValue);
	}

	public ASTNode getBody() {
		return body;
	}

	public void setBody(ASTNode body) {
		this.body = body;
	}

	public int getKind() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			for (Iterator iterator = vars.keySet().iterator(); iterator
					.hasNext();) {
				ASTNode var = (ASTNode) iterator.next();
				if (var != null)
					var.traverse(visitor);
			}
			for (Iterator iterator = vars.values().iterator(); iterator
					.hasNext();) {
				ASTNode var = (ASTNode) iterator.next();
				if (var != null)
					var.traverse(visitor);
			}
			if (body != null)
				body.traverse(visitor);
			if (asterixParameter != null)
				asterixParameter.traverse(visitor);
			visitor.endvisit(this);
		}
	}

}
