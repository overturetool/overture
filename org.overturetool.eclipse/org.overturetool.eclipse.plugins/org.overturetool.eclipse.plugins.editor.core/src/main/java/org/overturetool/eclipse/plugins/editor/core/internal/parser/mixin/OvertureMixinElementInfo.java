/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin;

import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IType;

public class OvertureMixinElementInfo {

	public static final int K_CLASS = 0;

	public static final int K_MODULE = 1;

	public static final int K_METHOD = 2;

	public static final int K_VARIABLE = 3;

	public static final int K_INCLUDE = 4;

	public static final int K_VIRTUAL = 5;

	public static final int K_SUPER = 6;

	public static final int K_ALIAS = 7;

	public static final int K_EXTEND = 8;

	private int kind = 0;
	private Object object = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + kind;
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final OvertureMixinElementInfo other = (OvertureMixinElementInfo) obj;
		if (kind != other.kind)
			return false;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		return true;
	}

	public OvertureMixinElementInfo(int kind, Object object) {
		super();
		this.kind = kind;
		this.object = object;
	}

	public int getKind() {
		return kind;
	}

	public void setKind(int kind) {
		this.kind = kind;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public static OvertureMixinElementInfo createClass(IType type) {
		return new OvertureMixinElementInfo(K_CLASS, type);
	}

	public static OvertureMixinElementInfo createModule(IType type) {
		return new OvertureMixinElementInfo(K_MODULE, type);
	}

	public static OvertureMixinElementInfo createMethod(IMethod m) {
		return new OvertureMixinElementInfo(K_METHOD, m);
	}

	public static OvertureMixinElementInfo createInclude(String key) {
		return new OvertureMixinElementInfo(K_INCLUDE, key);
	}

	public static OvertureMixinElementInfo createExtend(String key) {
		return new OvertureMixinElementInfo(K_EXTEND, key);
	}

	public static OvertureMixinElementInfo createSuperClass(String key) {
		return new OvertureMixinElementInfo(K_SUPER, key);
	}

	public static OvertureMixinElementInfo createVariable(IField type) {
		return new OvertureMixinElementInfo(K_VARIABLE, type);
	}

	public static OvertureMixinElementInfo createVirtualClass() {
		return new OvertureMixinElementInfo(K_VIRTUAL, null);
	}

}
