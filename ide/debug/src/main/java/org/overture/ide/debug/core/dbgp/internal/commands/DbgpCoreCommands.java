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
import java.util.Map;

import org.overture.ide.debug.core.dbgp.IDbgpCommunicator;
import org.overture.ide.debug.core.dbgp.IDbgpFeature;
import org.overture.ide.debug.core.dbgp.IDbgpProperty;
import org.overture.ide.debug.core.dbgp.IDbgpStackLevel;
import org.overture.ide.debug.core.dbgp.IDbgpStatus;
import org.overture.ide.debug.core.dbgp.breakpoints.DbgpBreakpointConfig;
import org.overture.ide.debug.core.dbgp.breakpoints.IDbgpBreakpoint;
import org.overture.ide.debug.core.dbgp.commands.IDbgpBreakpointCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpContextCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpContinuationCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpCoreCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpDataTypeCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpFeatureCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpPropertyCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpSourceCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpStackCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpStatusCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpStreamCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public class DbgpCoreCommands implements IDbgpCoreCommands
{

	private final IDbgpFeatureCommands featureCommands;

	private final IDbgpStatusCommands statusCommands;

	private final IDbgpBreakpointCommands breakpointCommands;

	private final IDbgpSourceCommands sourceCommands;

	private final IDbgpContextCommands contextCommands;

	private final IDbgpStackCommands stackCommands;

	private final IDbgpContinuationCommands continuationCommands;

	private final IDbgpStreamCommands streamCommands;

	private final IDbgpDataTypeCommands dataTypeCommands;

	private final IDbgpPropertyCommands propertyCommands;

	public DbgpCoreCommands(IDbgpCommunicator communicator)
	{
		this.featureCommands = new DbgpFeatureCommands(communicator);
		this.statusCommands = new DbgpStatusCommands(communicator);
		this.breakpointCommands = new DbgpBreakpointCommands(communicator);
		this.sourceCommands = new DbgpSourceCommands(communicator);
		this.contextCommands = new DbgpContextCommands(communicator);
		this.stackCommands = new DbgpStackCommands(communicator);
		this.continuationCommands = new DbgpContinuationCommands(communicator);
		this.streamCommands = new DbgpStreamCommands(communicator);
		this.propertyCommands = new DbgpPropertyCommands(communicator);
		this.dataTypeCommands = new DbgpDataTypeCommands(communicator);

	}

	public IDbgpFeature getFeature(String featureName) throws DbgpException
	{
		return featureCommands.getFeature(featureName);
	}

	public boolean setFeature(String featureName, String featureValue)
			throws DbgpException
	{
		return featureCommands.setFeature(featureName, featureValue);
	}

	public IDbgpBreakpoint getBreakpoint(String id) throws DbgpException
	{
		return breakpointCommands.getBreakpoint(id);
	}

	public IDbgpBreakpoint[] getBreakpoints() throws DbgpException
	{
		return breakpointCommands.getBreakpoints();
	}

	public void removeBreakpoint(String id) throws DbgpException
	{
		breakpointCommands.removeBreakpoint(id);
	}

	public String setCallBreakpoint(URI uri, String function,
			DbgpBreakpointConfig info) throws DbgpException
	{
		return breakpointCommands.setCallBreakpoint(uri, function, info);
	}

	public String setConditionalBreakpoint(URI uri, DbgpBreakpointConfig info)
			throws DbgpException
	{
		return breakpointCommands.setConditionalBreakpoint(uri, info);
	}

	public String setConditionalBreakpoint(URI uri, int lineNumber,
			DbgpBreakpointConfig info) throws DbgpException
	{
		return breakpointCommands.setConditionalBreakpoint(uri, lineNumber, info);
	}

	public String setExceptionBreakpoint(String exception,
			DbgpBreakpointConfig info) throws DbgpException
	{
		return breakpointCommands.setExceptionBreakpoint(exception, info);
	}

	public String setLineBreakpoint(URI uri, int lineNumber,
			DbgpBreakpointConfig info) throws DbgpException
	{
		return breakpointCommands.setLineBreakpoint(uri, lineNumber, info);
	}

	public String setReturnBreakpoint(URI uri, String function,
			DbgpBreakpointConfig info) throws DbgpException
	{
		return breakpointCommands.setReturnBreakpoint(uri, function, info);
	}

	public String setWatchBreakpoint(URI uri, int line,
			DbgpBreakpointConfig info) throws DbgpException
	{
		return breakpointCommands.setWatchBreakpoint(uri, line, info);
	}

	public void updateBreakpoint(String id, DbgpBreakpointConfig config)
			throws DbgpException
	{
		breakpointCommands.updateBreakpoint(id, config);
	}

	public IDbgpStatus detach() throws DbgpException
	{
		return continuationCommands.detach();
	}

	public IDbgpStatus run() throws DbgpException
	{
		return continuationCommands.run();
	}

	public IDbgpStatus stepInto() throws DbgpException
	{
		return continuationCommands.stepInto();
	}

	public IDbgpStatus stepOut() throws DbgpException
	{
		return continuationCommands.stepOut();
	}

	public IDbgpStatus stepOver() throws DbgpException
	{
		return continuationCommands.stepOver();
	}

	public IDbgpStatus stop() throws DbgpException
	{
		return continuationCommands.stop();
	}

	public Map<String, Integer> getTypeMap() throws DbgpException
	{
		return dataTypeCommands.getTypeMap();
	}

	public String getSource(URI uri) throws DbgpException
	{
		return sourceCommands.getSource(uri);
	}

	public String getSource(URI uri, int beginLine) throws DbgpException
	{
		return sourceCommands.getSource(uri, beginLine);
	}

	public String getSource(URI uri, int beginLine, int endLine)
			throws DbgpException
	{
		return sourceCommands.getSource(uri, beginLine, endLine);
	}

	public IDbgpStatus getStatus() throws DbgpException
	{
		return statusCommands.getStatus();
	}

	public IDbgpStackLevel getStackLevel(int stackDepth) throws DbgpException
	{
		return stackCommands.getStackLevel(stackDepth);
	}

	public IDbgpStackLevel[] getStackLevels() throws DbgpException
	{
		return stackCommands.getStackLevels();
	}

	public int getStackDepth() throws DbgpException
	{
		return stackCommands.getStackDepth();
	}

	public Map<Integer, String> getContextNames(int stackDepth)
			throws DbgpException
	{
		return contextCommands.getContextNames(stackDepth);
	}

	public IDbgpProperty[] getContextProperties(int stackDepth)
			throws DbgpException
	{
		return contextCommands.getContextProperties(stackDepth);
	}

	public IDbgpProperty[] getContextProperties(int stackDepth, int contextId)
			throws DbgpException
	{
		return contextCommands.getContextProperties(stackDepth, contextId);
	}

	public boolean configureStderr(int value) throws DbgpException
	{
		return streamCommands.configureStderr(value);
	}

	public boolean configureStdout(int value) throws DbgpException
	{
		return streamCommands.configureStdout(value);
	}

	public IDbgpProperty getProperty(String name) throws DbgpException
	{
		return propertyCommands.getProperty(name);
	}

	public IDbgpProperty getProperty(String name, int stackDepth)
			throws DbgpException
	{
		return propertyCommands.getProperty(name, stackDepth);
	}

	public IDbgpProperty getProperty(String name, int stackDepth, int contextId)
			throws DbgpException
	{
		return propertyCommands.getProperty(name, stackDepth, contextId);
	}

	public boolean setProperty(IDbgpProperty property) throws DbgpException
	{
		return propertyCommands.setProperty(property);
	}

	public boolean setProperty(String name, int stackDepth, String value)
			throws DbgpException
	{
		return propertyCommands.setProperty(name, stackDepth, value);
	}

	public IDbgpProperty getPropertyByKey(Integer page, String name,
			Integer stackDepth, String key) throws DbgpException
	{
		return propertyCommands.getPropertyByKey(page, name, stackDepth, key);
	}

	public IDbgpProperty getProperty(int page, String name, int stackDepth)
			throws DbgpException
	{
		return propertyCommands.getProperty(page, name, stackDepth);
	}

	public boolean setProperty(String longName, String key, String newValue)
			throws DbgpException
	{
		return propertyCommands.setProperty(longName, key, newValue);
	}

}
