package org.overture.ide.core.resources;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public abstract class VdmProjectNature implements IProjectNature {
	protected IProject project = null;

	public abstract void configure() throws CoreException;

	public abstract void deconfigure() throws CoreException;

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
