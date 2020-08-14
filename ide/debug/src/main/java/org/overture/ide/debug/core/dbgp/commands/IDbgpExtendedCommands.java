/*
 * #%~
 * org.overture.ide.debug
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.debug.core.dbgp.commands;

import org.overture.ide.debug.core.dbgp.IDbgpProperty;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public interface IDbgpExtendedCommands
{
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

	IDbgpProperty execute(String code) throws DbgpException;
}
