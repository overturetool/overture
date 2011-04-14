package org.overture.ide.core.builder;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.overture.ide.core.ICoreConstants;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.ast.VdmModelWorkingCopy;
import org.overture.ide.core.parser.SourceParserManager;
import org.overture.ide.core.resources.IVdmProject;

public class SafeBuilder extends Thread
{

	final IVdmProject currentProject;

	final IProgressMonitor monitor;

	public SafeBuilder(final IVdmProject currentProject,
			final IProgressMonitor monitor)
	{
		this.currentProject = currentProject;
		this.monitor = monitor;
		this.setName("VDM Safe Builder");
		this.setDaemon(true);
	}

	@Override
	public void run()
	{
		try
		{
			IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(ICoreConstants.EXTENSION_BUILDER_ID);
			for (IConfigurationElement e : config)
			{
				if (currentProject.getVdmNature().equals(e.getAttribute("nature")))
				{
					final Object o = e.createExecutableExtension("class");
					if (o instanceof AbstractVdmBuilder)
					{
						ISafeRunnable runnable = new ISafeRunnable()
						{

							public void handleException(Throwable e)
							{
								VdmCore.log("SafeBuilder", e);
							}

							public void run() throws Exception
							{
								AbstractVdmBuilder builder = (AbstractVdmBuilder) o;

								final IVdmModel model = currentProject.getModel();
//								SourceParserManager.parseMissingFiles(currentProject, model, monitor);

								// if the project don't have parse errors
								if (model != null && model.isParseCorrect())
								{
									
									VdmModelWorkingCopy workingModel = model.getWorkingCopy();
									SourceParserManager.parseMissingFiles(currentProject, workingModel, monitor);
									try
									{

										if (VdmCore.DEBUG)
										{
											System.out.println("Parse correct .. building("
													+ currentProject.getName()
													+ ")");
										}
										monitor.subTask("Type checking: "
												+ currentProject);
										IStatus status = builder.buildModel(currentProject, workingModel);
										// mark ast root as type checked
										monitor.done();
										if (workingModel != null)
										{
											workingModel.setTypeCheckedStatus(status.isOK());
											workingModel.commit();
										}
									} catch (Exception e)
									{
										workingModel.discard();
										throw e;
									}
									return;
								}
							}
						};
						SafeRunner.run(runnable);
						break;
					}
				}
			}
		} catch (Exception ex)
		{
			System.out.println(ex.getMessage());
			VdmCore.log(ex);
		}

	}
}
