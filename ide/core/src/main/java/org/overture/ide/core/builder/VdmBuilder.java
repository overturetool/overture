package org.overture.ide.core.builder;

import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.VdmProject;

public class VdmBuilder extends VdmCoreBuilder
{ // implements IScriptBuilder {
	// This must be the ID from your extension point

	private static Vector<IVdmProject> buildingProjects = new Vector<IVdmProject>();

	protected static synchronized boolean isBuilding(IVdmProject project)
	{
		return buildingProjects.contains(project);
	}

	protected static synchronized void setBuilding(IVdmProject project)
	{
		buildingProjects.add(project);
	}

	protected static synchronized void removeBuilding(IVdmProject project)
	{
		if (buildingProjects.contains(project))
			buildingProjects.remove(project);
	}

	public VdmBuilder()
	{

	}

	@Override
	public void build(final IProgressMonitor monitor) throws CoreException
	{
		if (VdmCore.DEBUG)
		{
			System.out.println("buildModelElements");
		}
		
		if (isBuilding(getVdmProject()))
		{
			monitor.subTask("Waiting for other build: "
					+ getVdmProject().getName());
			while (isBuilding(getVdmProject()))
			{
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e)
				{
				}
			}
			monitor.done();
			// return new Status(IStatus.INFO,
			// BUILDER_ID,
			// "Build cancelled since another builder already was running.");
		}

		setBuilding(getVdmProject());
		//clean(monitor);
		clearProblemMarkers();
		clearInternalModel();

		final List<IStatus> statusList = new Vector<IStatus>();

		final SafeBuilder builder = new SafeBuilder(getVdmProject(), statusList, monitor);

		builder.start();

		ISafeRunnable runnable = new ISafeRunnable()
		{

			public void handleException(Throwable exception)
			{
				exception.printStackTrace();

			}

			@SuppressWarnings("deprecation")
			public void run() throws Exception
			{
				while (builder.isAlive())
				{
					Thread.sleep(500);
					if (monitor.isCanceled())
					{
						builder.stop();
					}
				}
			}

		};
		SafeRunner.run(runnable);

		// for (IStatus s : statusList)
		// {
		// if (!s.isOK())
		// return s;
		// }
		// if (statusList.size() > 0)
		// {
		//
		// // just return the first status
		// return statusList.get(0);
		// } else
		// return new Status(IStatus.WARNING,
		// VdmBuilderCorePluginConstants.PLUGIN_ID,
		// "No builder returned any result");
		removeBuilding(getVdmProject());
	}

	public void clean(IProgressMonitor monitor)
	{
		if (VdmCore.DEBUG)
		{
			System.out.println("clean");
		}
		monitor.beginTask("Cleaning project: " + getProject().getName(), IProgressMonitor.UNKNOWN);
		// AstManager.instance().clean(project.getProject());
		if (VdmProject.isVdmProject(getProject()))
		{
			clearProblemMarkers();

			// IMPORTANT we do not have an incremental builder so a full parse/ build is required, therefore remove any
			// AST nodes in store.
			clearInternalModel();

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

	private void clearInternalModel()
	{
		IVdmModel model = VdmProject.createProject(getProject()).getModel();
		if (model != null)
		{
			model.clean();
		}
	}

	public void endBuild(IProgressMonitor monitor)
	{
		if (VdmCore.DEBUG)
		{
			System.out.println("endBuild");
		}
		removeBuilding(getVdmProject());
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
