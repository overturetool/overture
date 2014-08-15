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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.model.IVdmWatchpoint;

public class VdmWatchpoint extends VdmLineBreakpoint implements IVdmWatchpoint
{

	public static final String FIELD_NAME = VdmDebugPlugin.PLUGIN_ID
			+ ".fieldName"; //$NON-NLS-1$

	public static final String ACCESS = VdmDebugPlugin.PLUGIN_ID + ".access"; //$NON-NLS-1$

	public static final String MODIFICATION = VdmDebugPlugin.PLUGIN_ID
			+ ".modification"; //$NON-NLS-1$

	public VdmWatchpoint(final String debugModelId, final IResource resource,
			final IPath path, final int lineNumber, final int start,
			final int end, final String fieldName) throws CoreException
	{
		IWorkspaceRunnable wr = new IWorkspaceRunnable()
		{
			public void run(IProgressMonitor monitor) throws CoreException
			{
				// create the marker
				setMarker(resource.createMarker(getMarkerId()));

				final Map<String, Object> attributes = new HashMap<String, Object>();
				// add attributes
				addVdmBreakpointAttributes(attributes, debugModelId, true);
				addLineBreakpointAttributes(attributes, path, lineNumber, start, end);
				attributes.put(FIELD_NAME, fieldName);

				// set attributes
				ensureMarker().setAttributes(attributes);

				// add to breakpoint manager if requested
				register(true);
			}
		};
		run(getMarkerRule(resource), wr);
	}

	public VdmWatchpoint()
	{
	}

	public String getFieldName() throws CoreException
	{
		return this.getMarker().getAttribute(FIELD_NAME, ""); //$NON-NLS-1$
	}

	public void setFieldName(String name) throws CoreException
	{
		this.getMarker().setAttribute(FIELD_NAME, name);
	}

	protected String getMarkerId()
	{
		return IDebugConstants.WATCHPOINT_MARKER_ID;
	}

	public boolean isAccess() throws CoreException
	{
		return new Boolean(this.getMarker().getAttribute(ACCESS, "true")) //$NON-NLS-1$
		.booleanValue();
	}

	public boolean isModification() throws CoreException
	{
		return new Boolean(this.getMarker().getAttribute(MODIFICATION, "true")) //$NON-NLS-1$
		.booleanValue();
	}

	public void setAccess(boolean access) throws CoreException
	{
		this.getMarker().setAttribute(ACCESS, Boolean.toString(access));
	}

	public void setModification(boolean modification) throws CoreException
	{
		this.getMarker().setAttribute(MODIFICATION, Boolean.toString(modification));
	}

	public boolean supportsAccess()
	{
		return true;
	}

	public boolean supportsModification()
	{
		return true;
	}

	private static final String[] UPDATABLE_ATTRS = new String[] { FIELD_NAME,
			ACCESS, MODIFICATION };

	public String[] getUpdatableAttributes()
	{
		List<String> all = new ArrayList<String>();
		all.addAll(Arrays.asList(super.getUpdatableAttributes()));
		all.addAll(Arrays.asList(UPDATABLE_ATTRS));
		return (String[]) all.toArray(new String[all.size()]);
	}
}
