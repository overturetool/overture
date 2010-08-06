package org.overture.ide.core.builder;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.resources.IVdmProject;

public class VdmBuilder extends VdmCoreBuilder
{

	@Override
	public void build(final IProgressMonitor monitor) throws CoreException
	{
		if (VdmCore.DEBUG)
		{
			System.out.println("buildModelElements");
		}
		try
		{
			final SafeBuilder builder = new SafeBuilder(getVdmProject(), monitor);

			clearProblemMarkers();
			builder.setDaemon(true);
			builder.start();
			while (!builder.isInterrupted() && builder.isAlive())
			{
				Thread.sleep(2000);

				if (monitor.isCanceled())
				{
					builder.interrupt();
					Thread.sleep(2000);
					builder.stop();
				}
			}
		} catch (Exception e)
		{
			VdmCore.log(e);
		}

	}

	public void clean(IProgressMonitor monitor)
	{
		if (VdmCore.DEBUG)
		{
			System.out.println("clean");
		}
		monitor.beginTask("Cleaning project: " + getProject().getName(), IProgressMonitor.UNKNOWN);

		if (getProject().getAdapter(IVdmProject.class) != null)
		{
			clearProblemMarkers();

			// IMPORTANT we do not have an incremental builder so a full parse/ build is required, therefore remove any
			// AST nodes in store.
			// clearInternalModel();

			try
			{
				IResource res = getProject().findMember("generated");

				ResourcesPlugin.getWorkspace().delete(new IResource[] { res }, true, monitor);

			} catch (Exception e)
			{
				// we can't do any thing about it
			}
		}
		monitor.done();

	}

	public void endBuild(IProgressMonitor monitor)
	{
	}

	public void initialize()
	{
		if (VdmCore.DEBUG)
		{
			System.out.println("initialize");
		}

		syncProjectResources();

	}

}
