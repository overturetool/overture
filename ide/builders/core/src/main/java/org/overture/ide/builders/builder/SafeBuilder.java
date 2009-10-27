package org.overture.ide.builders.builder;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.overture.ide.ast.AstManager;
import org.overture.ide.ast.RootNode;

public class SafeBuilder extends Thread {

	final IProject currentProject;
	final List<IStatus> statusList;
	final IProgressMonitor monitor;
	public SafeBuilder(final IProject currentProject,final  List<IStatus> statusList,
		final	IProgressMonitor monitor) {
		this.currentProject = currentProject;
		this.statusList = statusList;
		this.monitor = monitor;
	}

	@Override
	public void run(){

		try {
			IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
					BuildParticipant.BUILDER_ID);
			for (IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("class");
				if (o instanceof AbstractBuilder) {
					ISafeRunnable runnable = new ISafeRunnable() {

						public void handleException(Throwable exception) {
							exception.printStackTrace();

						}

						@SuppressWarnings("unchecked")
						public void run() throws Exception {
							AbstractBuilder builder = (AbstractBuilder) o;

							if (currentProject.hasNature(builder.getNatureId())) {

								AbstractBuilder.parseMissingFiles(currentProject,builder.getNatureId(),builder.getContentTypeId(),monitor);

								final List ast = (List) AstManager.instance().getAstList(
										currentProject, builder.getNatureId());

								monitor.subTask("Type checking");
								statusList.add(builder.buileModelElements(
										currentProject, ast));
								//  mark ast root as type checked
								RootNode root =AstManager.instance().getRootNode(currentProject, builder.getNatureId());
								if(root!=null)
									root.setChecked(statusList.get(statusList.size()-1).getCode()< IStatus.ERROR);
							}
						}

					};
					SafeRunner.run(runnable);
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}
	
}






