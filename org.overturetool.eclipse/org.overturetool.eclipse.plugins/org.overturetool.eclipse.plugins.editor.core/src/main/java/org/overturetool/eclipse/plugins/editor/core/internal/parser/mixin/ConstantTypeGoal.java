/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin;

import org.eclipse.dltk.ti.IContext;
import org.eclipse.dltk.ti.goals.AbstractTypeGoal;

public class ConstantTypeGoal extends AbstractTypeGoal {

	private final int offset;
	private final String name;

	public ConstantTypeGoal(IContext context, int offset, String name) {
		super(context);
		this.offset = offset;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getOffset() {
		return offset;
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + offset;
		return result;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConstantTypeGoal other = (ConstantTypeGoal) obj;
		if (offset != other.offset)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
