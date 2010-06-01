/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.internal;

import org.overture.ide.debug.core.dbgp.IDbgpFeature;

public class DbgpFeature implements IDbgpFeature {
	private final boolean supported;

	private final String name;

	private final String value;

	public DbgpFeature(boolean supported, String name, String value) {
		this.supported = supported;
		this.name = name;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	public boolean isSupported() {
		return supported;
	}

	public String toString() {
		return "DbgpFeature (name: " + name + "; value: " + value //$NON-NLS-1$ //$NON-NLS-2$
				+ "; supported: " + supported + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
