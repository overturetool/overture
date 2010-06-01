/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.model.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.overture.ide.debug.core.VdmDebugManager;
import org.overture.ide.debug.core.model.IVdmLineBreakpoint;
import org.overture.ide.debug.core.model.IVdmMethodEntryBreakpoint;
import org.overture.ide.debug.core.model.IVdmWatchpoint;

public class VdmDebugModel {

	public static String getDebugModelId(IResource resource) {
//		final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
//				.findToolkitForResource(resource);
//		if (toolkit != null) {
//			String natureId = toolkit.getNatureId();
//			return VdmDebugManager.getInstance().getDebugModelByNature(
//					natureId);
//		}
		return null;
	}

	public static IVdmLineBreakpoint createLineBreakpoint(
			IResource resource, IPath path, int lineNumber, int charStart,
			int charEnd, boolean register, Map attributes) throws CoreException {

		return new VdmLineBreakpoint(getDebugModelId(resource), resource,
				path, lineNumber, charStart, charEnd, register);
	}

	public static IVdmLineBreakpoint createLineBreakpoint(
			String debugModelId, IResource resource, IPath path,
			int lineNumber, int charStart, int charEnd, boolean register,
			Map attributes) throws CoreException {

		return new VdmLineBreakpoint(debugModelId, resource, path,
				lineNumber, charStart, charEnd, register);
	}

//	public static IVdmSpawnpoint createSpawnpoint(String debugModelId,
//			IResource resource, IPath path, int lineNumber, int charStart,
//			int charEnd, boolean register, Map attributes) throws CoreException {
//		return new VdmSpawnpoint(debugModelId, resource, path, lineNumber,
//				charStart, charEnd, register);
//	}

	public static IVdmMethodEntryBreakpoint createMethodEntryBreakpoint(
			IResource resource, IPath path, int lineNumber, int charStart,
			int charEnd, boolean register, Map attributes, String methodName)
			throws CoreException {

		return new VdmMethodEntryBreakpoint(getDebugModelId(resource),
				resource, path, lineNumber, charStart, charEnd, register,
				methodName);
	}

	public static IVdmWatchpoint createWatchPoint(IResource resource,
			IPath path, int lineNumber, int start, int end, String fieldName)
			throws CoreException {
		return new VdmWatchpoint(getDebugModelId(resource), resource, path,
				lineNumber, start, end, fieldName);
	}

//	public static VdmExceptionBreakpoint createExceptionBreakpoint(
//			String debugModelId, IResource resource, String typename,
//			boolean caught, boolean uncaught, boolean register, Map attributes)
//			throws CoreException {
//		if (attributes == null)
//			attributes = new HashMap();
//
//		return new VdmExceptionBreakpoint(debugModelId, resource, typename,
//				caught, uncaught, register, attributes);
//	}
}
