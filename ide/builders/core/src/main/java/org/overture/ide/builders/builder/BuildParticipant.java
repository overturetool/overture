package org.overture.ide.builders.builder;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.builder.IScriptBuilder;
import org.overture.ide.ast.AstManager;
import org.overture.ide.builders.core.VdmSlBuilderCorePluginConstants;

public class BuildParticipant implements IScriptBuilder {
	// This must be the ID from your extension point
	public static final String BUILDER_ID = "org.overture.ide.builder";
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
	if(buildingProjects.contains(project))
	 buildingProjects.remove(project);
}

	public BuildParticipant() {

	}

	@SuppressWarnings("unchecked")
	public IStatus buildModelElements(IScriptProject project, List elements,
			final IProgressMonitor monitor, int status) {
		System.out.println("buildModelElements");
		

		final IProject currentProject = project.getProject();
		
		if(isBuilding(currentProject))
			{
			monitor.subTask("Waiting for other build: "+ currentProject.getName());
			while(isBuilding(currentProject))
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
				monitor.done();
				return new Status(IStatus.INFO,BUILDER_ID,"Build cancelled since another builder already was running.");
			}
		
		setBuilding(currentProject);
		AbstractBuilder.clearProblemMarkers(currentProject);

		final List<IStatus> statusList = new Vector<IStatus>();

		final SafeBuilder builder = new SafeBuilder(currentProject, statusList, monitor);

		builder.start();

		ISafeRunnable runnable = new ISafeRunnable() {

			public void handleException(Throwable exception) {
				exception.printStackTrace();

			}

			public void run() throws Exception {
				while (builder.isAlive()) {
					Thread.sleep(500);
					if (monitor.isCanceled())
						builder.stop();
				}
			}

		};
		SafeRunner.run(runnable);

		for (IStatus s : statusList) {
			if (!s.isOK())
				return s;
		}
		if (statusList.size() > 0) {

			// just return the first status
			return statusList.get(0);
		} else
			return new Status(IStatus.WARNING, VdmSlBuilderCorePluginConstants.PLUGIN_ID, "No builder returned any result");
	}

	@SuppressWarnings("unchecked")
	public IStatus buildResources(IScriptProject project, List resources,
			IProgressMonitor monitor, int status) {
		System.out.println("buildResources");

		return null;
	}

	public void clean(IScriptProject project, IProgressMonitor monitor) {
		System.out.println("clean");
		monitor.beginTask(
				"Cleaning project: " + project.getProject().getName(),
				IProgressMonitor.UNKNOWN);
		AstManager.instance().clean(project.getProject());
		monitor.done();

	}

	public void endBuild(IScriptProject project, IProgressMonitor monitor) {
		System.out.println("endBuild");
removeBuilding(project.getProject());
	}

	@SuppressWarnings("unchecked")
	public DependencyResponse getDependencies(IScriptProject project,
			int buildType, Set localElements, Set externalElements,
			Set oldExternalFolders, Set externalFolders) {
		System.out.println("DependencyResponse");

		return null;
	}

	public void initialize(IScriptProject project) {

		System.out.println("initialize");
		

		AbstractBuilder.syncProjectResources(project.getProject());
		

	}

}
