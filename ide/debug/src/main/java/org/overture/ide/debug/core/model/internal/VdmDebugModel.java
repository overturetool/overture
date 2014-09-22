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

import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.overture.ide.debug.core.model.IVdmLineBreakpoint;
import org.overture.ide.debug.core.model.IVdmMethodEntryBreakpoint;
import org.overture.ide.debug.core.model.IVdmWatchpoint;

public class VdmDebugModel
{

	public static String getDebugModelId(IResource resource)
	{
		// final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
		// .findToolkitForResource(resource);
		// if (toolkit != null) {
		// String natureId = toolkit.getNatureId();
		// return VdmDebugManager.getInstance().getDebugModelByNature(
		// natureId);
		// }
		return null;
	}

	public static IVdmLineBreakpoint createLineBreakpoint(IResource resource,
			IPath path, int lineNumber, int charStart, int charEnd,
			boolean register, Map<String, Object> attributes)
			throws CoreException
	{

		return new VdmLineBreakpoint(getDebugModelId(resource), resource, path, lineNumber, charStart, charEnd, register);
	}

	public static IVdmLineBreakpoint createLineBreakpoint(String debugModelId,
			IResource resource, IPath path, int lineNumber, int charStart,
			int charEnd, boolean register, Map<String, Object> attributes)
			throws CoreException
	{

		return new VdmLineBreakpoint(debugModelId, resource, path, lineNumber, charStart, charEnd, register);
	}

	// public static IVdmSpawnpoint createSpawnpoint(String debugModelId,
	// IResource resource, IPath path, int lineNumber, int charStart,
	// int charEnd, boolean register, Map attributes) throws CoreException {
	// return new VdmSpawnpoint(debugModelId, resource, path, lineNumber,
	// charStart, charEnd, register);
	// }

	public static IVdmMethodEntryBreakpoint createMethodEntryBreakpoint(
			IResource resource, IPath path, int lineNumber, int charStart,
			int charEnd, boolean register, Map<String, Object> attributes,
			String methodName) throws CoreException
	{

		return new VdmMethodEntryBreakpoint(getDebugModelId(resource), resource, path, lineNumber, charStart, charEnd, register, methodName);
	}

	public static IVdmWatchpoint createWatchPoint(IResource resource,
			IPath path, int lineNumber, int start, int end, String fieldName)
			throws CoreException
	{
		return new VdmWatchpoint(getDebugModelId(resource), resource, path, lineNumber, start, end, fieldName);
	}

	// public static VdmExceptionBreakpoint createExceptionBreakpoint(
	// String debugModelId, IResource resource, String typename,
	// boolean caught, boolean uncaught, boolean register, Map attributes)
	// throws CoreException {
	// if (attributes == null)
	// attributes = new HashMap();
	//
	// return new VdmExceptionBreakpoint(debugModelId, resource, typename,
	// caught, uncaught, register, attributes);
	// }
}
