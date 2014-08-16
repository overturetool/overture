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
package org.overture.ide.debug.core.dbgp.exceptions;

import org.overture.ide.debug.utils.communication.DBGPErrorType;
import org.overture.interpreter.debug.DBGPException;

public class DbgpDebuggingEngineException extends DbgpException
{
	private static final long serialVersionUID = 1L;

	/* Command parsing errors */
	public static final int NO_ERROR = 0;

	public static final int PASSE_ERROR_IN_COMMAND = 1;

	public static final int DUPLICATE_ARGUMENTS_IN_COMMAND = 2;

	// missing a required option
	public static final int INVALID_OPTIONS = 3;

	public static final int UNIMPLEMENTED_COMMAND = 4;

	// Is used for async commands. For instance
	// if the engine is in state "run" than only "break" and "status"
	// are available
	public static final int COMMAND_NOT_AVAILABLE = 5;

	/* File related errors */

	// as a reply to a "source" command if the requested source file can't be
	// opened
	public static final int FILE_CAN_NOT_OPEN_FILE = 100;

	// stream redirect failed
	public static final int STREAM_REDIRECT_FAILED = 101;

	/* Breakpoint, or code flow errors */

	// for some reason the breakpoint could not be set due to problems
	// registering it
	public static final int BREAKPOINT_COULD_NOT_BE_SET = 200;

	// for example I don't support 'watch' yet and thus return this error
	public static final int BREAKPOINT_TYPE_NOT_SUPPORTED = 201;

	// the IDE tried to set a breakpoint on a line that does not exist in the
	// file (ie "line 0" or lines past the end of the file
	public static final int INVALID_PREAKPOINT = 202;

	// the IDE tried to set a breakpoint on a line which does not have any
	// executable code. The
	// debugger engine is NOT required to return this type if it
	// is impossible to determine if there is code on a given
	// location. (For example, in the PHP debugger backend this
	// will only be returned in some special cases where the current
	// scope falls into the scope of the breakpoint to be set
	public static final int NO_CODE_ON_BREAKPOINT_LINE = 203;

	// using an unsupported breakpoint state was attempted
	public static final int INVALID_BREAKPOINT_STATE = 204;

	// used in breakpoint_get etc. to show that there is no breakpoint with the
	// given ID
	public static final int NO_SUCH_BREAKPOINT = 205;

	// use from eval() (or perhaps property_get for a full name get
	public static final int ERROR_EVALUATING_CODE = 206;

	// the expression used for a non-eval() was invalid
	public static final int IVALID_EXPRESSION = 207;

	/* Data errors */

	// when the requested property to get did
	// not exist, this is NOT used for an existing but uninitialized
	// property, which just gets the type "uninitialised" (See:
	// PreferredTypeNames)
	public static final int CAN_NOT_GET_PROPERTY = 300;

	// the -d stack depth parameter did not exist (ie, there were less stack
	// elements than the number requested) or the parameter was < 0
	public static final int STACH_DEPTH_INVALID = 301;

	// an non existing context was requested
	public static final int CONTEXT_INVALID = 302;

	/* Protocol errors */
	public static final int ENCODING_NOT_SUPPROTED = 900;

	public static final int INTERNAL_EXCEPTION = 998;

	public static final int UNKNOWN_ERROR = 999;

	private final int code;

	public DbgpDebuggingEngineException(int code)
	{
		this.code = code;
	}

	public DbgpDebuggingEngineException(int code, String message)
	{
		super(message);
		// super(NLS.bind("dbgpDebuggingEngineException", new Integer(code),
		// message));
		this.code = code;
	}

	public DbgpDebuggingEngineException(int code, Throwable cause)
	{
		super(cause);
		this.code = code;
	}

	public DbgpDebuggingEngineException(int code, String message,
			Throwable cause)
	{
		// super(NLS.bind("dbgpDebuggingEngineException2", message, new Integer(
		// code)), cause);
		super(message, cause);
		this.code = code;
	}

	public int getCode()
	{
		return code;
	}

	@Override
	public String getMessage()
	{
		String protocolErrorMessage = new Integer(getCode()).toString();
		try
		{
			protocolErrorMessage = DBGPErrorType.lookup(getCode()).toString();
		} catch (DBGPException e)
		{
			//
		}
		return protocolErrorMessage + "\nDetailed Message: "
				+ super.getMessage();
	}
}
