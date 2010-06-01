/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.internal;

public class DbgpTransactionManager {
	private static DbgpTransactionManager instance = new DbgpTransactionManager();

	public static DbgpTransactionManager getInstance() {
		return instance;
	}

	private final Object lock = new Object();

	private int id;

	private DbgpTransactionManager() {
		this.id = 0;
	}

	public int generateId() {
		synchronized (lock) {
			return id++;
		}
	}
}
