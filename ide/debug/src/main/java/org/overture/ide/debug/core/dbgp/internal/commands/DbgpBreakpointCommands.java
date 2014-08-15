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
package org.overture.ide.debug.core.dbgp.internal.commands;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.overture.ide.debug.core.dbgp.DbgpBaseCommands;
import org.overture.ide.debug.core.dbgp.DbgpRequest;
import org.overture.ide.debug.core.dbgp.IDbgpCommunicator;
import org.overture.ide.debug.core.dbgp.breakpoints.DbgpBreakpointConfig;
import org.overture.ide.debug.core.dbgp.breakpoints.IDbgpBreakpoint;
import org.overture.ide.debug.core.dbgp.commands.IDbgpBreakpointCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlEntityParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DbgpBreakpointCommands extends DbgpBaseCommands implements
		IDbgpBreakpointCommands
{

	private static final String BREAKPOINT_SET_COMMAND = "breakpoint_set"; //$NON-NLS-1$

	private static final String BREAKPOINT_GET_COMMAND = "breakpoint_get"; //$NON-NLS-1$

	private static final String BREAKPOINT_UPDATE_COMMAND = "breakpoint_update"; //$NON-NLS-1$

	private static final String BREAKPOINT_REMOVE_COMMAND = "breakpoint_remove"; //$NON-NLS-1$

	private static final String BREAKPOINT_LIST_COMMAND = "breakpoint_list"; //$NON-NLS-1$

	private static final String BREAKPOINT_TAG = "breakpoint"; //$NON-NLS-1$

	final String LINE_BREAKPOINT = "line"; //$NON-NLS-1$

	final String CALL_BREAKPOINT = "call"; //$NON-NLS-1$

	final String RETURN_BREAKPOINT = "return"; //$NON-NLS-1$

	final String EXCEPTION_BREAKPOINT = "exception"; //$NON-NLS-1$

	final String CONDITIONAL_BREAKPOINT = "conditional"; //$NON-NLS-1$

	final String WATCH_BREAKPOINT = "watch"; //$NON-NLS-1$

	protected IDbgpBreakpoint[] parseBreakpointsResponse(Element response)
	{
		List<IDbgpBreakpoint> list = new ArrayList<IDbgpBreakpoint>();

		NodeList breakpoints = response.getElementsByTagName(BREAKPOINT_TAG);
		for (int i = 0; i < breakpoints.getLength(); ++i)
		{
			list.add(DbgpXmlEntityParser.parseBreakpoint((Element) breakpoints.item(i)));
		}

		return (IDbgpBreakpoint[]) list.toArray(new IDbgpBreakpoint[list.size()]);
	}

	protected String parseSetBreakpointResponse(Element response)
			throws DbgpException
	{
		return response.getAttribute("id"); //$NON-NLS-1$
	}

	protected String setBreakpoint(String type, URI uri, Integer lineNumber,
			String function, String exception, DbgpBreakpointConfig info)
			throws DbgpException
	{

		DbgpRequest request = createRequest(BREAKPOINT_SET_COMMAND);
		request.addOption("-t", type); //$NON-NLS-1$

		if (uri != null)
		{
			request.addOption("-f", uri.toASCIIString()); //$NON-NLS-1$
		}

		if (lineNumber != null)
		{
			request.addOption("-n", lineNumber.toString()); //$NON-NLS-1$
		}

		if (function != null)
		{
			request.addOption("-m", function); //$NON-NLS-1$
		}

		if (exception != null)
		{
			request.addOption("-x", exception); //$NON-NLS-1$
		}

		if (info != null)
		{
			request.addOption("-s", info.getStateString()); //$NON-NLS-1$
			request.addOption("-r", info.getTemporaryString()); //$NON-NLS-1$

			if (info.getHitValue() != -1 && info.getHitCondition() != -1)
			{
				request.addOption("-h", info.getHitValue()); //$NON-NLS-1$
				request.addOption("-o", info.getHitConditionString()); //$NON-NLS-1$
			}

			String expression = info.getExpression();
			if (expression != null)
			{
				request.setData(expression);
			}
		}

		return parseSetBreakpointResponse(communicate(request));
	}

	public DbgpBreakpointCommands(IDbgpCommunicator communicator)
	{
		super(communicator);
	}

	public String setLineBreakpoint(URI uri, int lineNumber,
			DbgpBreakpointConfig info) throws DbgpException
	{
		return setBreakpoint(LINE_BREAKPOINT, uri, new Integer(lineNumber), null, null, info);
	}

	public String setCallBreakpoint(URI uri, String function,
			DbgpBreakpointConfig info) throws DbgpException
	{
		return setBreakpoint(CALL_BREAKPOINT, uri, null, function, null, info);
	}

	public String setReturnBreakpoint(URI uri, String function,
			DbgpBreakpointConfig info) throws DbgpException
	{
		return setBreakpoint(RETURN_BREAKPOINT, uri, null, function, null, info);
	}

	public String setExceptionBreakpoint(String exception,
			DbgpBreakpointConfig info) throws DbgpException
	{
		return setBreakpoint(EXCEPTION_BREAKPOINT, null, null, null, exception, info);
	}

	public String setConditionalBreakpoint(URI uri, DbgpBreakpointConfig info)
			throws DbgpException
	{
		return setBreakpoint(CONDITIONAL_BREAKPOINT, uri, null, null, null, info);
	}

	public String setConditionalBreakpoint(URI uri, int lineNumber,
			DbgpBreakpointConfig info) throws DbgpException
	{
		return setBreakpoint(CONDITIONAL_BREAKPOINT, uri, new Integer(lineNumber), null, null, info);
	}

	public String setWatchBreakpoint(URI uri, int line,
			DbgpBreakpointConfig info) throws DbgpException
	{
		return setBreakpoint(WATCH_BREAKPOINT, uri, new Integer(line), null, null, info);
	}

	public IDbgpBreakpoint getBreakpoint(String id) throws DbgpException
	{
		DbgpRequest request = createRequest(BREAKPOINT_GET_COMMAND);
		request.addOption("-d", id); //$NON-NLS-1$
		IDbgpBreakpoint[] breakpoints = parseBreakpointsResponse(communicate(request));
		if (breakpoints.length > 0)
		{
			return breakpoints[0];
		}
		return null;
	}

	public void removeBreakpoint(String id) throws DbgpException
	{
		if (id == null)
		{
			return;
		}

		DbgpRequest request = createRequest(BREAKPOINT_REMOVE_COMMAND);
		request.addOption("-d", id); //$NON-NLS-1$
		communicate(request);
	}

	public void updateBreakpoint(String id, DbgpBreakpointConfig config)
			throws DbgpException
	{
		DbgpRequest request = createRequest(BREAKPOINT_UPDATE_COMMAND);
		request.addOption("-d", id); //$NON-NLS-1$
		request.addOption("-s", config.getStateString()); //$NON-NLS-1$

		if (config.getLineNo() > 0)
		{
			request.addOption("-n", config.getLineNo()); //$NON-NLS-1$
		}

		if (config.getHitValue() != -1)
		{
			request.addOption("-h", config.getHitValue()); //$NON-NLS-1$
		}

		if (config.getHitCondition() != -1)
		{
			request.addOption("-o", config.getHitConditionString()); //$NON-NLS-1$
		}

		communicate(request);
	}

	public IDbgpBreakpoint[] getBreakpoints() throws DbgpException
	{
		return parseBreakpointsResponse(communicate(createRequest(BREAKPOINT_LIST_COMMAND)));
	}
}
