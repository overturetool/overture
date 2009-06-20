/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin;

import org.eclipse.dltk.ast.expressions.CallExpression;
import org.eclipse.dltk.ti.goals.ItemReference;
import org.eclipse.dltk.ti.goals.PossiblePosition;

public class OvertureMethodReference extends ItemReference {

	private CallExpression node;
	
	public OvertureMethodReference(String name, String parentModelKey,
			PossiblePosition pos, int accuracy) {
		super(name, parentModelKey, pos, accuracy);
	}

	public CallExpression getNode() {
		return node;
	}

	public void setNode(CallExpression node) {
		this.node = node;
	}
	
}
