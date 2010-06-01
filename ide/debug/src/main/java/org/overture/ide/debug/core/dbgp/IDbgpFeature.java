/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp;

public interface IDbgpFeature {
	final String ZERO_VALUE = "0"; //$NON-NLS-1$

	final String ONE_VALUE = "1"; //$NON-NLS-1$

	boolean isSupported();

	String getName();

	String getValue();
}
