/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.exceptions;

public class DbgpException extends Exception {

	private static final long serialVersionUID = 1L;

	public DbgpException() {
		super("DbgpException");
	}

	public DbgpException(String message, Throwable cause) {
		super(message, cause);
	}

	public DbgpException(String message) {
		super(message);
	}

	public DbgpException(Throwable cause) {
		super(cause);
	}
}
