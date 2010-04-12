package org.overture.ide.core.builder;

import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.VdmProject;

import org.overture.ide.internal.core.ast.VdmModelManager;

public class VdmBuilder extends VdmCoreBuilder
{ // implements IScriptBuilder {
	// This must be the ID from your extension point

	private static Vector<IProject> buildingProjects = new Vector<IProject>();

	protected static synchronized boolean isBuilding(IProject project)
	{
		return buildingProjects.contains(project);
	}

	protected static synchronized void setBuilding(IProject project)
	{
		buildingProjects.add(project);
	}

	protected static synchronized void removeBuilding(IProject project)
	{
		if (buildingProjects.contains(project))
			buildingProjects.remove(project);
	}

	public VdmBuilder() {

	}

	@Override
	public void fullBuild(final IProgressMonitor monitor) throws CoreException
	{
		if (VdmCore.DEBUG)
			System.out.println("buildModelElements");

		if (!VdmProject.isVdmProject(getProject()))
			if (VdmCore.DEBUG)
				System.err.println("Project is now VDM: " + getProject());

		final IVdmProject currentProject = VdmProject.createProject(getProject());

		if (isBuilding(currentProject))
		{
			monitor.subTask("Waiting for other build: "
					+ currentProject.getName());
			while (isBuilding(currentProject))
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e)
				{
				}
			monitor.done();
			// return new Status(IStatus.INFO,
			// BUILDER_ID,
			// "Build cancelled since another builder already was running.");
		}

		setBuilding(currentProject);
		clean(monitor);

		final List<IStatus> statusList = new Vector<IStatus>();

		final SafeBuilder builder = new SafeBuilder(currentProject,
				statusList,
				monitor);

		builder.start();

		ISafeRunnable runnable = new ISafeRunnable() {

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
						builder.stop();
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
removeBuilding(currentProject);
	}

	public void clean(IProgressMonitor monitor)
	{
		if (VdmCore.DEBUG)
			System.out.println("clean");
		monitor.beginTask("Cleaning project: " + getProject().getName(),
				IProgressMonitor.UNKNOWN);
		// AstManager.instance().clean(project.getProject());
		if (VdmProject.isVdmProject(getProject()))
		{
			clearProblemMarkers();

			VdmModelManager.getInstance().clean(VdmProject.createProject(getProject()));// IMPORTANT we do not
			// have an
			// incremental
			// builder so a full
			// parse/ build is
			// required,
			// therefore remove
			// any AST nodes in
			// store.
			try
			{
				IResource res = getProject().findMember("generated");

				ResourcesPlugin.getWorkspace().delete(new IResource[] { res },
						true,
						monitor);

			} catch (Exception e)
			{
				// we can do any thing about it
			}
		}
		monitor.done();

	}

	public void endBuild(IProgressMonitor monitor)
	{
		if (VdmCore.DEBUG)
			System.out.println("endBuild");
		removeBuilding(getProject());
	}

	public void initialize()
	{
		if (VdmCore.DEBUG)
			System.out.println("initialize");

		syncProjectResources();

	}

}
