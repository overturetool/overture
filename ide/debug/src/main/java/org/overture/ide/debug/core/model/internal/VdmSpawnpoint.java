///*******************************************************************************
// * Copyright (c) 2008 xored software, Inc.
// *
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
// *******************************************************************************/
//package org.overture.ide.debug.core.model.internal;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.eclipse.core.resources.IMarker;
//import org.eclipse.core.resources.IResource;
//import org.eclipse.core.resources.IWorkspaceRunnable;
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.core.runtime.IPath;
//import org.eclipse.core.runtime.IProgressMonitor;
//import org.eclipse.debug.core.DebugException;
//import org.eclipse.debug.core.model.IBreakpoint;
//import org.eclipse.dltk.debug.core.model.IScriptSpawnpoint;
//
//public class VdmSpawnpoint extends VdmLineBreakpoint implements
//		IVdmSpawnpoint {
//
//	protected String getMarkerId() {
//		return ScriptMarkerFactory.SPAWNPOINT_MARKER_ID;
//	}
//
//	public VdmSpawnpoint() {
//		// empty
//	}
//
//	/**
//	 * @param debugModelId
//	 * @param resource
//	 * @param path
//	 * @param lineNumber
//	 * @param charStart
//	 * @param charEnd
//	 * @param register
//	 * @throws DebugException
//	 */
//	public VdmSpawnpoint(final String debugModelId,
//			final IResource resource, final IPath path, final int lineNumber,
//			final int charStart, final int charEnd, final boolean register)
//			throws DebugException {
//
//		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
//			public void run(IProgressMonitor monitor) throws CoreException {
//				// create the marker
//				setMarker(resource.createMarker(getMarkerId()));
//
//				// add attributes
//				final Map attributes = new HashMap();
//				addScriptBreakpointAttributes(attributes, debugModelId, true);
//				addLineBreakpointAttributes(attributes, path, lineNumber,
//						charStart, charEnd);
//
//				// set attributes
//				ensureMarker().setAttributes(attributes);
//
//				// add to breakpoint manager if requested
//				register(register);
//			}
//		};
//		run(getMarkerRule(resource), wr);
//	}
//
//	private static final String[] UPDATABLE_ATTRS = new String[] {
//			IMarker.LINE_NUMBER, IBreakpoint.ENABLED };
//
//	public String[] getUpdatableAttributes() {
//		return UPDATABLE_ATTRS;
//	}
//
//}
