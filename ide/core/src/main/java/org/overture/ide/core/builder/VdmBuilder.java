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
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.resources.VdmProject;

public class VdmBuilder extends VdmCoreBuilder
{ // implements IScriptBuilder {
	// This must be the ID from your extension point

//	private static Map<IProject, SafeBuilder> buildingProjects = new Hashtable<IProject, SafeBuilder>();
//
//	protected static synchronized boolean isBuilding(IVdmProject project)
//	{
//		return buildingProjects.containsKey(project.getProject());
//	}
//
//	protected synchronized SafeBuilder getBuildingBuilder(IVdmProject project)
//	{
//		if (buildingProjects.containsKey(project.getProject()))
//		{
//			return buildingProjects.get(project.getProject());
//		}
//		return null;
//	}
//
//	protected static synchronized void setBuilding(IVdmProject project,
//			SafeBuilder monitor)
//	{
//		buildingProjects.put(project.getProject(), monitor);
//	}
//
//	protected static synchronized void removeBuilding(IVdmProject project)
//	{
//		if (buildingProjects.containsKey(project.getProject()))
//		{
//			buildingProjects.remove(project.getProject());
//		}
//	}

	

	@Override
	public void build(final IProgressMonitor monitor) throws CoreException
	{
		if (VdmCore.DEBUG)
		{
			System.out.println("buildModelElements");
		}

//		if (isBuilding(getVdmProject()))
//		{
//			SafeBuilder buildingMonitor = getBuildingBuilder(getVdmProject());
//			if (buildingMonitor != null)
//			{
//				buildingMonitor.interrupt();
//			}
//
//		}
		try
		{
			final List<IStatus> statusList = new Vector<IStatus>();

			final SafeBuilder builder = new SafeBuilder(getVdmProject(), statusList, monitor);
//			setBuilding(getVdmProject(), builder);
			clearProblemMarkers();
			// clearInternalModel();

			builder.start();
			
			try{
			while (builder.isAlive())
			{
				Thread.sleep(500);
				if (monitor.isCanceled())
				{
					builder.interrupt();
					Thread.sleep(2000);
					builder.stop();
				}
			}}catch(Exception e)
			{
				
			}

//			ISafeRunnable runnable = new ISafeRunnable()
//			{
//
//				public void handleException(Throwable exception)
//				{
//					exception.printStackTrace();
//
//				}
//
//				public void run() throws Exception
//				{
//					while (builder.isAlive())
//					{
//						Thread.sleep(500);
//						if (monitor.isCanceled())
//						{
//							builder.interrupt();
//							Thread.sleep(2000);
//							builder.stop();
//						}
//					}
//				}
//
//			};
//			SafeRunner.run(runnable);

		} finally
		{
//			removeBuilding(getVdmProject());
		}
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

	// private void clearInternalModel()
	// {
	// IVdmModel model = VdmProject.createProject(getProject()).getModel();
	// if (model != null)
	// {
	// model.clean();
	// }
	// }

	public void endBuild(IProgressMonitor monitor)
	{
//		if (VdmCore.DEBUG)
//		{
//			System.out.println("endBuild");
//		}
//		removeBuilding(getVdmProject());
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
