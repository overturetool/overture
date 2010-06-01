/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.internal;

import org.overture.ide.debug.core.dbgp.IDbgpProperty;

public class DbgpProperty implements IDbgpProperty {

	private final String address;

	private final String name;

	private final String fullName;

	private final String type;

	private final String value;

	private final boolean constant;

	private final int childrenCount;

	private final IDbgpProperty[] availableChildren;

	private final boolean hasChildren;

	private final String key;

	private int page;

	private int pageSize;

	public DbgpProperty(String name, String fullName, String type,
			String value, int childrenCount, boolean hasChildren,
			boolean constant, String key, String address,
			IDbgpProperty[] availableChildren, int page, int pageSize) {
		this.name = name;
		this.fullName = fullName;
		this.type = type;
		this.value = value;
		this.address = address;
		this.childrenCount = childrenCount;
		this.availableChildren = availableChildren;
		this.hasChildren = hasChildren;
		this.constant = constant;
		this.key = key;
		this.page = page;
		this.pageSize = pageSize;
	}

	public String getEvalName() {
		return fullName;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public boolean hasChildren() {
		return hasChildren;
	}

	public int getChildrenCount() {
		return childrenCount;
	}

	public IDbgpProperty[] getAvailableChildren() {
		return (IDbgpProperty[]) availableChildren.clone();
	}

	public boolean isConstant() {
		return constant;
	}

	public String toString() {
		return "DbgpProperty (Name: " + name + "; Full name: " + fullName //$NON-NLS-1$ //$NON-NLS-2$
				+ "; Type: " + type + "; Value: " + value + " Address: " + address + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	public String getKey() {
		return key;
	}

	public int getPage() {
		return page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public String getAddress() {
		return address;
	}
}
