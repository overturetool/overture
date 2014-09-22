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

import java.net.URI;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.overture.ide.core.resources.IVdmProject;

public class VdmBreakpointPathMapper implements
		IVdmBreakpointPathMapperExtension
{
	private HashMap<URI, URI> cache;
	private String mapTo;
	private IVdmProject vdmProject;
	private boolean stripSrcFolders;

	VdmBreakpointPathMapper(IVdmProject project, String mapTo,
			boolean stripSrcFolders)
	{
		this.mapTo = mapTo;
		this.vdmProject = project;
		this.stripSrcFolders = stripSrcFolders;

		this.cache = new HashMap<URI, URI>();
	}

	public void clearCache()
	{
		cache.clear();
	}

	public URI map(URI uri)
	{
		// no mapTo, return original uri
		if (mapTo == null || "".equals(mapTo)) { //$NON-NLS-1$
			return uri;
		}

		// check the cache
		if (cache.containsKey(uri))
		{
			return (URI) cache.get(uri);
		}

		// now for the fun ;)
		IProject project = (IProject) vdmProject.getAdapter(IProject.class);

		if (project == null)
		{
			return uri;
		}

		final IPath projectPath = project.getLocation();
		if (projectPath == null)
		{
			return uri;
		}

		final IPath path = new Path(uri.getPath());
		// only map paths that start w/ the project path
		if (projectPath.isPrefixOf(path))
		{
			IPath temp = path.removeFirstSegments(projectPath.segmentCount()).setDevice(null);
			if (stripSrcFolders)
			{
				temp = stripSourceFolders(temp);
			}
			final IPath outgoing = new Path(mapTo).append(temp);
			final URI result = VdmLineBreakpoint.makeUri(outgoing);
			cache.put(uri, result);
			return result;
		}
		cache.put(uri, uri);
		return uri;

	}

	private IPath stripSourceFolders(IPath path)
	{
		// try {
		// IProjectFragment[] fragments = vdmProject.getProjectFragments();
		//
		// for (int i = 0; i < fragments.length; i++) {
		// IProjectFragment frag = fragments[i];
		// // skip external/archive
		// if (frag.isExternal() || frag.isArchive()) {
		// continue;
		// }
		//
		// final String name = frag.getElementName();
		// if (path.segmentCount() > 0 && path.segment(0).equals(name)) {
		// return path.removeFirstSegments(1);
		// }
		// }
		// } catch (CoreException e) {
		// VdmDebugPlugin.log(e);
		// }

		return path;
	}
}
