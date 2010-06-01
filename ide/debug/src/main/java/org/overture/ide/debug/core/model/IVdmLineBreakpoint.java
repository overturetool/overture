package org.overture.ide.debug.core.model;

import java.net.URI;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.model.ILineBreakpoint;

public interface IVdmLineBreakpoint extends IVdmBreakpoint, ILineBreakpoint {

	/**
	 * 
	 * Returns the {@link IResource} if this breakpoint is attached to the
	 * 
	 * resource in the workspace or <code>null</code> if breakpoint is attached
	 * 
	 * to the file outside of the workspace.
	 * 
	 * 
	 * 
	 * @return
	 */

	IResource getResource();

	IPath getResourcePath();

	URI getResourceURI();

}
