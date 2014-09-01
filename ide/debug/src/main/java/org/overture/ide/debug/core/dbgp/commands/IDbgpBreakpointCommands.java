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

import java.net.URI;

import org.overture.ide.debug.core.dbgp.breakpoints.DbgpBreakpointConfig;
import org.overture.ide.debug.core.dbgp.breakpoints.IDbgpBreakpoint;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public interface IDbgpBreakpointCommands
{
	// Line breakpoint
	String setLineBreakpoint(URI uri, int lineNumber,
			DbgpBreakpointConfig config) throws DbgpException;

	// Call breakpoint
	String setCallBreakpoint(URI uri, String function,
			DbgpBreakpointConfig config) throws DbgpException;

	// Return breakpoint
	String setReturnBreakpoint(URI uri, String function,
			DbgpBreakpointConfig config) throws DbgpException;

	// Exception breakpoint
	String setExceptionBreakpoint(String exception, DbgpBreakpointConfig config)
			throws DbgpException;

	String setConditionalBreakpoint(URI uri, int lineNumber,
			DbgpBreakpointConfig config) throws DbgpException;

	String setConditionalBreakpoint(URI uri, DbgpBreakpointConfig config)
			throws DbgpException;

	// Watch breakpoint
	String setWatchBreakpoint(URI uri, int line, DbgpBreakpointConfig config)
			throws DbgpException;

	// Remove
	void removeBreakpoint(String id) throws DbgpException;

	// Update
	void updateBreakpoint(String id, DbgpBreakpointConfig config)
			throws DbgpException;

	// Get
	IDbgpBreakpoint getBreakpoint(String id) throws DbgpException;

	IDbgpBreakpoint[] getBreakpoints() throws DbgpException;
}
