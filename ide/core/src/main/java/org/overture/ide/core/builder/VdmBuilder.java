package org.overture.ide.core.builder;

import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.overture.ide.core.ast.AstManager;



public class VdmBuilder extends VdmCoreBuilder{ //implements IScriptBuilder {
	// This must be the ID from your extension point
	
	private static Vector<IProject> buildingProjects = new Vector<IProject>();

	protected static synchronized boolean isBuilding(IProject project) {
		return buildingProjects.contains(project);
	}

	protected static synchronized void setBuilding(IProject project) {
		buildingProjects.add(project);
	}

	protected static synchronized void removeBuilding(IProject project) {
		if (buildingProjects.contains(project))
			buildingProjects.remove(project);
	}

	public VdmBuilder() {

	}
@Override
	public void fullBuild(final IProgressMonitor monitor) throws CoreException {
		System.out.println("full build");
	

		final IProject currentProject = getProject();

		if (isBuilding(currentProject)) {
			monitor.subTask("Waiting for other build: "
					+ currentProject.getName());
			while (isBuilding(currentProject))
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			monitor.done();
//			return new Status(IStatus.INFO, VdmBuilderCorePluginConstants.BUILDER_ID,
//					"Build cancelled since another builder already was running.");
		}

		setBuilding(currentProject);
		BuildParcitipant.clearProblemMarkers(currentProject);

		AstManager.instance().clean(getProject());// IMPORTANT we do not
															// have an
															// incremental
															// builder so a full
															// parse/ build is
															// required,
															// therefore remove
															// any AST nodes in
															// store.

		final List<IStatus> statusList = new Vector<IStatus>();

		final SafeBuilder builder = new SafeBuilder(currentProject, statusList,
				monitor);

		builder.start();

		ISafeRunnable runnable = new ISafeRunnable() {

			public void handleException(Throwable exception) {
				exception.printStackTrace();

			}

			@SuppressWarnings("deprecation")
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
				throw new CoreException( s);
		}
		
	}

//	@SuppressWarnings("unchecked")
//	public IStatus buildResources(IScriptProject project, List resources,
//			IProgressMonitor monitor, int status) {
//		System.out.println("buildResources");
//
//		return null;
//	}

	public void clean( IProgressMonitor monitor) {
		System.out.println("clean");
		monitor.beginTask(
				"Cleaning project: " + getProject().getName(),
				IProgressMonitor.UNKNOWN);
		AstManager.instance().clean(getProject());
		monitor.done();

	}

	public void endBuild( IProgressMonitor monitor) {
		System.out.println("endBuild");
		removeBuilding(getProject());
	}

//	@SuppressWarnings("unchecked")
//	public DependencyResponse getDependencies(IScriptProject project,
//			int buildType, Set localElements, Set externalElements,
//			Set oldExternalFolders, Set externalFolders) {
//		System.out.println("DependencyResponse");
//
//		return null;
//	}

	public void initialize() {

		System.out.println("initialize");

		BuildParcitipant.syncProjectResources(getProject());

	}

}
