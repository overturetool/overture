package org.overture.ide.debug.core.model;

import java.io.File;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;
import org.overture.ide.debug.core.IDebugConstants;

public class VdmLineBreakpoint extends LineBreakpoint {

	private File file;
	private int id;
	
	
	public VdmLineBreakpoint() {
		super();
	}

	
	public VdmLineBreakpoint(final IResource resource, final int lineNumber) throws CoreException {
		
		initPath(resource);
		
		
		
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource
						.createMarker("vdm.lineBreakpoint.marker");
				setMarker(marker);
				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(IMarker.MESSAGE, "Line Breakpoint: "
						+ resource.getName() + " [line: " + lineNumber + "]");
				marker.setAttribute(IBreakpoint.PERSISTED, Boolean.TRUE);
			}
		};
		run(getMarkerRule(resource), runnable);
	     
	  }
	
	private void initPath(IResource resource){
		IPath path = resource.getRawLocation();
		path = path.makeAbsolute();
		
		setFile(path.toFile());
	}
	
	public String getModelIdentifier() {
		return IDebugConstants.ID_VDM_DEBUG_MODEL;
	}

	private void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	
	@Override
	public void setMarker(IMarker marker) throws CoreException {
		super.setMarker(marker);
		initPath(marker.getResource());
	}
}
