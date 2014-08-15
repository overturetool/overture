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

import org.overture.ide.debug.core.dbgp.IDbgpStatus;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public interface IDbgpContinuationCommands
{
	// starts or resumes the script until a new breakpoint is reached, or the
	// end of the script is reached.
	IDbgpStatus run() throws DbgpException;

	// steps to the next statement, if there is a function call involved it will
	// break on the first statement in that function
	IDbgpStatus stepInto() throws DbgpException;

	// steps to the next statement, if there is a function call on the line from
	// which the step_over is issued then the debugger engine will stop at the
	// statement after the function call in the same scope as from where the
	// command was issued
	IDbgpStatus stepOver() throws DbgpException;

	// steps out of the current scope and breaks on the statement after
	// returning from the current function. (Also called 'finish' in GDB)
	IDbgpStatus stepOut() throws DbgpException;

	// ends execution of the script immediately, the debugger engine may not
	// respond, though if possible should be designed to do so. The script will
	// be terminated right away and be followed by a disconnection of the
	// network connection from the IDE (and debugger engine if required in multi
	// request apache processes).
	IDbgpStatus stop() throws DbgpException;

	// (optional): stops interaction with the debugger engine. Once this command
	// is executed, the IDE will no longer be able to communicate with the
	// debugger engine. This does not end execution of the script as does the
	// stop command above, but rather detaches from debugging. Support of this
	// continuation command is optional, and the IDE should verify support for
	// it via the feature_get command. If the IDE has created
	// stdin/stdout/stderr pipes for execution of the script (eg. an interactive
	// shell or other console to catch script output), it should keep those open
	// and usable by the process until the process has terminated normally.

	// TODO: add detach handling to commands and DbgpDetachedException!!!
	IDbgpStatus detach() throws DbgpException;
}
