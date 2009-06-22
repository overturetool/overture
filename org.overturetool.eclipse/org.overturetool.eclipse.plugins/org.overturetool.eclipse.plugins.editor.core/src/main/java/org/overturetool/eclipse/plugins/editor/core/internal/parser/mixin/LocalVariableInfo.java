/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
/**
 * 
 */
package org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin;

import java.util.List;

import org.eclipse.dltk.ast.ASTNode;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureAssignment;

/**
 * Stores local variable information: it's kind (argument, simple,...) and
 * evaluates all assignments to it relatively to given offset
 * 
 * @author fourdman
 * 
 */
public class LocalVariableInfo {

	public final static int KIND_DEFAULT = 0;

	public final static int KIND_BLOCK_ARG = 1;

	public final static int KIND_METHOD_ARG = 2;

	public final static int KIND_LOOP_VAR = 3;

	private ASTNode declaringScope;

	private OvertureAssignment[] conditionalAssignments;

	private OvertureAssignment lastAssignment;

	private int kind;

	public LocalVariableInfo(final ASTNode declaringScope,
			final OvertureAssignment[] assignments, final OvertureAssignment last) {
		this.declaringScope = declaringScope;
		this.conditionalAssignments = assignments;
		this.lastAssignment = last;
		this.kind = 0;
	}

	public LocalVariableInfo(final ASTNode declaringScope,
			final OvertureAssignment[] assignments, final OvertureAssignment last,
			int kind) {
		this.declaringScope = declaringScope;
		this.conditionalAssignments = assignments;
		lastAssignment = last;
		this.kind = kind;
	}

	public LocalVariableInfo() {
	}

	/**
	 * Scoping node (if, for, while, block, method...), that encloses current
	 * node
	 * 
	 * @return
	 */
	public ASTNode getDeclaringScope() {
		return declaringScope;
	}

	/**
	 * Assignments, that could be or not executed
	 * 
	 * @return
	 */
	public OvertureAssignment[] getConditionalAssignments() {
		if (conditionalAssignments == null)
			return new OvertureAssignment[0];
		return conditionalAssignments;
	}

	/**
	 * Last assignment (could be null) to this variable, and which will be
	 * performed in any way
	 * 
	 * @return
	 */
	public OvertureAssignment getLastAssignment() {
		return lastAssignment;
	}

	/**
	 * Kind of variable: simple, method argument, block argument, for-loop
	 * variable
	 * 
	 * @return
	 */
	public int getKind() {
		return kind;
	}

	public void setDeclaringScope(ASTNode declaringScope) {
		this.declaringScope = declaringScope;
	}

	public void setConditionalAssignments(
			OvertureAssignment[] conditionalAssignments) {
		this.conditionalAssignments = conditionalAssignments;
	}

	public void setConditionalAssignments(List conditionalAssignments) {
		this.conditionalAssignments = (OvertureAssignment[]) conditionalAssignments
				.toArray(new OvertureAssignment[conditionalAssignments.size()]);
	}

	public void setLastAssignment(OvertureAssignment lastAssignment) {
		this.lastAssignment = lastAssignment;
	}

	public void setKind(int kind) {
		this.kind = kind;
	}

}
