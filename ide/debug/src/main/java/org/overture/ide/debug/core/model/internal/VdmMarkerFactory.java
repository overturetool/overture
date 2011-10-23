/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.model.internal;

import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class VdmMarkerFactory {
	public static final String LINE_BREAKPOINT_MARKER_ID = "vdm.lineBreakpoint.marker"; //$NON-NLS-1$
	public static final String METHOD_ENTRY_MARKER_ID = "org.overture.ide.debug.vdmMethodEntryBreakpointMarker"; //$NON-NLS-1$
	public static final String WATCHPOINT_MARKER_ID = "org.overture.ide.debug.vdmWatchPointMarker"; //$NON-NLS-1$
	

	public static IMarker makeMarker(IResource resource, @SuppressWarnings("rawtypes") Map attributes,
			String id) throws CoreException {
		IMarker marker = resource.createMarker(id);
		marker.setAttributes(attributes);
		return marker;
	}
}
