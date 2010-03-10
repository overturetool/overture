package org.overture.ide.core.builder;

import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class VdmCoreBuilder extends IncrementalProjectBuilder
{

	public VdmCoreBuilder() {

	}

	@SuppressWarnings("unchecked")
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException
	{

		initialize();

		if (kind == IncrementalProjectBuilder.FULL_BUILD)
		{
			fullBuild(monitor);
		} else
		{
			IResourceDelta delta = getDelta(getProject());
			if (delta == null)
			{
				fullBuild(monitor);
			} else
			{
				incrementalBuild(delta, monitor);
			}
		}

		endBuild(monitor);

		return null;
	}

	public void initialize()
	{
	}

	private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor)
	{
		System.out.println("incremental build on " + delta);
		try
		{
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta)
				{
					System.out.println("changed: "
							+ delta.getResource().getRawLocation());
					return true; // visit children too
				}
			});
		} catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	public void fullBuild(IProgressMonitor monitor) throws CoreException
	{
		System.out.println("full build");
	}

	public void endBuild(IProgressMonitor monitor)
	{
	}

	
	/***
	 * This method sync the project resources. It is called before an instance
	 * of the AbstractBuilder is created
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

			} catch (CoreException e1)
			{

				e1.printStackTrace();
			}
	}
	
	
	/***
	 * This method removed all problem markers and its sub-types from the
	 * project. It is called before an instance of the AbstractBuilder is
	 * created
	 * 
	 * @param project
	 *            The project which should be build.
	 */
	public void clearProblemMarkers() {
		try {
			getProject().deleteMarkers(IMarker.PROBLEM, true,
					IResource.DEPTH_INFINITE);

		} catch (CoreException e) {

			e.printStackTrace();
		}

	}
}
