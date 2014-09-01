/*
 * #%~
 * org.overture.ide.core
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
package org.overture.ide.core.builder;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.resources.IVdmProject;

public abstract class VdmCoreBuilder extends IncrementalProjectBuilder
{
	private class DeltaFileVisitor implements IResourceDeltaVisitor
	{
		private boolean sourceFound = false;

		public boolean isSourceFound()
		{
			return sourceFound;
		}

		public boolean visit(IResourceDelta delta) throws CoreException
		{
			IResource resource = delta.getResource();
			
			if (resource instanceof IFile && resource.exists() && resource.isSynchronized(IResource.DEPTH_ZERO) && ((IFile) resource).getContentDescription()!=null)
			{

				if (getVdmProject().isModelFile((IFile) resource))
				{
					sourceFound = true;
					return false;// do not visit children
				}
			}
			return true;
		}

	}

	@Override
	protected final IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args,
			IProgressMonitor monitor) throws CoreException
	{
		if (!validateProject())
		{
			// Project who invoked the builder is not supported
			return new IProject[0];
		}
	
		getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);

		if (kind == IncrementalProjectBuilder.FULL_BUILD)
		{	initialize();
			clean(monitor);
			build(monitor);
		} else
		{
			IResourceDelta delta = getDelta(getProject());
			// if (delta == null)
			DeltaFileVisitor visitor = new DeltaFileVisitor();
			delta.accept(visitor, IResourceDelta.ADDED | IResourceDelta.CHANGED);
			// {
			if (visitor.isSourceFound())
			{
				initialize();
				build(monitor);
			}
			// } else
			// {
			// incrementalBuild(delta, monitor);
			// }
		}

		endBuild(monitor);
		return new IProject[]{getProject()};
		
	}

	public abstract void initialize();

	// private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor)
	// {
	// System.out.println("incremental build on " + delta);
	// try
	// {
	// delta.accept(new IResourceDeltaVisitor() {
	// public boolean visit(IResourceDelta delta)
	// {
	// System.out.println("changed: "
	// + delta.getResource().getRawLocation());
	// return true; // visit children too
	// }
	// });
	// } catch (CoreException e)
	// {
	// e.printStackTrace();
	// }
	// }

	public abstract void build(IProgressMonitor monitor) throws CoreException;

	public abstract void endBuild(IProgressMonitor monitor);

	/***
	 * This method sync the project resources. It is called before an instance of the AbstractBuilder is created
	 */
	public void syncProjectResources()
	{
		if (!getProject().isSynchronized(IResource.DEPTH_INFINITE))
			try
			{
				getProject().refreshLocal(IResource.DEPTH_INFINITE, null);

			} catch (CoreException e)
			{
				VdmCore.log("VdmCoreBuilder:syncProjectResources", e);
			}
	}

	

	/***
	 * Validated that the project who invoked the builder is a supported VDM project
	 */
	protected boolean validateProject()
	{
		return getProject().getAdapter(IVdmProject.class) != null;
	}

	/***
	 * This method should be used instead of the getProject
	 */
	public final IVdmProject getVdmProject()
	{
		return (IVdmProject) getProject().getAdapter(IVdmProject.class);
	}

}
