/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
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
