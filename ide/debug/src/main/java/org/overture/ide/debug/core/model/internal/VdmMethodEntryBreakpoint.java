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
package org.overture.ide.debug.core.model.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugException;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.model.IVdmMethodEntryBreakpoint;

public class VdmMethodEntryBreakpoint extends VdmLineBreakpoint implements
		IVdmMethodEntryBreakpoint
{

	public static final String METHOD_NAME = VdmDebugPlugin.PLUGIN_ID
			+ ".methodName"; //$NON-NLS-1$

	public static final String BREAK_ON_ENTRY = VdmDebugPlugin.PLUGIN_ID
			+ ".methodEntry"; //$NON-NLS-1$

	public static final String BREAK_ON_EXIT = VdmDebugPlugin.PLUGIN_ID
			+ ".methodExit"; //$NON-NLS-1$

	private static final String ENTRY_ID = VdmDebugPlugin.PLUGIN_ID
			+ ".entryBrId"; //$NON-NLS-1$

	private static final String EXIT_ID = VdmDebugPlugin.PLUGIN_ID
			+ ".exitBrId"; //$NON-NLS-1$

	protected String getMarkerId()
	{
		return IDebugConstants.METHOD_ENTRY_MARKER_ID;
	}

	public VdmMethodEntryBreakpoint()
	{

	}

	public VdmMethodEntryBreakpoint(String debugModelId, IResource resource,
			IPath path, int lineNumber, int charStart, int charEnd,
			boolean register, String methodName) throws DebugException
	{

		super(debugModelId, resource, path, lineNumber, charStart, charEnd, register);

		try
		{
			ensureMarker().setAttribute(METHOD_NAME, methodName);
		} catch (CoreException e)
		{
			throw new DebugException(e.getStatus());
		}
	}

	// Method name
	public String getMethodName() throws CoreException
	{
		return ensureMarker().getAttribute(METHOD_NAME, ""); //$NON-NLS-1$
	}

	// Break on entry
	public boolean breakOnEntry() throws CoreException
	{
		return ensureMarker().getAttribute(BREAK_ON_ENTRY, false);
	}

	public void setBreakOnEntry(boolean value) throws CoreException
	{
		ensureMarker().setAttribute(BREAK_ON_ENTRY, value);
	}

	// Break on exit
	public boolean breakOnExit() throws CoreException
	{
		return ensureMarker().getAttribute(BREAK_ON_EXIT, false);
	}

	public void setBreakOnExit(boolean value) throws CoreException
	{
		ensureMarker().setAttribute(BREAK_ON_EXIT, value);
	}

	// Entry breakpoint id
	public String getEntryBreakpointId() throws CoreException
	{
		return ensureMarker().getAttribute(ENTRY_ID, null);
	}

	public void setEntryBreakpointId(String id) throws CoreException
	{
		ensureMarker().setAttribute(ENTRY_ID, id);
	}

	// Exit breakpoint id
	public String getExitBreakpointId() throws CoreException
	{
		return ensureMarker().getAttribute(EXIT_ID, null);
	}

	public void setExitBreakpointId(String id) throws CoreException
	{
		ensureMarker().setAttribute(EXIT_ID, id);
	}

	private static final String[] UPDATABLE_ATTRS = new String[] { METHOD_NAME,
			BREAK_ON_ENTRY, BREAK_ON_EXIT };

	public String[] getUpdatableAttributes()
	{
		List<String> all = new ArrayList<String>();
		all.addAll(Arrays.asList(super.getUpdatableAttributes()));
		all.addAll(Arrays.asList(UPDATABLE_ATTRS));
		return (String[]) all.toArray(new String[all.size()]);
	}
}
