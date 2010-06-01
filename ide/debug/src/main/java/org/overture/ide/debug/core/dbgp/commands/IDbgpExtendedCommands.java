/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.commands;

import org.overture.ide.debug.core.dbgp.IDbgpProperty;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public interface IDbgpExtendedCommands {
	int DISABLE = 0;

	int REDIRECT = 1;

	String BREAK_COMMAND = "break"; //$NON-NLS-1$

	String STDIN_COMMAND = "stdin"; //$NON-NLS-1$

	String EVAL_COMMAND = "eval"; //$NON-NLS-1$

	String EXEC_COMMAND = "exec"; //$NON-NLS-1$

	String EXPR_COMMAND = "expr"; //$NON-NLS-1$

	boolean configureStdin(int value) throws DbgpException;

	boolean sendStdin(String data) throws DbgpException;

	boolean makeBreak() throws DbgpException;

	IDbgpProperty evaluate(String snippet) throws DbgpException;

	/**
	 * @deprecated <code>eval</code> command does not support <code>depth</code>
	 *             parameter, so this method will be removed
	 */
	IDbgpProperty evaluate(String snippet, int depth) throws DbgpException;

	IDbgpProperty expression(String expression) throws DbgpException;

	IDbgpProperty execute(String code) throws DbgpException;
}
