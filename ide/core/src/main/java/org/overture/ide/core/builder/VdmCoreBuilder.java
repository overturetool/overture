package org.overture.ide.core.builder;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
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
			
			if (resource instanceof IFile && resource.exists() && ((IFile) resource).getContentDescription()!=null)
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

	@SuppressWarnings("unchecked")
	@Override
	protected final IProject[] build(int kind, Map args,
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
	 * 
	 * @param project
	 *            The project which should be synced
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
	 * This method removed all problem markers and its sub-types from the project. It is called before an instance of
	 * the AbstractBuilder is created
	 * 
	 * @param project
	 *            The project which should be build.
	 */
	public void clearProblemMarkers()
	{
		try
		{
			getProject().deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);

		} catch (CoreException e)
		{
			VdmCore.log("VdmCoreBuilder:clearProblemMarkers", e);
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
