package org.overture.ide.core.builder;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.overture.ide.core.Activator;
import org.overture.ide.core.ICoreConstants;
import org.overture.ide.core.ast.AstManager;
import org.overture.ide.core.ast.RootNode;
import org.overture.ide.core.parser.SourceParserManager;
import org.overture.ide.core.utility.IVdmProject;

public class SafeBuilder extends Thread
{

	final IVdmProject currentProject;
	final List<IStatus> statusList;
	final IProgressMonitor monitor;

	public SafeBuilder(final IVdmProject currentProject,
			final List<IStatus> statusList, final IProgressMonitor monitor) {
		this.currentProject = currentProject;
		this.statusList = statusList;
		this.monitor = monitor;
	}

	@Override
	public void run()
	{

		try
		{
			IConfigurationElement[] config = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(ICoreConstants.EXTENSION_BUILDER_ID);
			for (IConfigurationElement e : config)
			{
				if (currentProject.hasNature(e.getAttribute("nature")))
				{
					final Object o = e.createExecutableExtension("class");
					if (o instanceof AbstractVdmBuilder)
					{
						ISafeRunnable runnable = new ISafeRunnable() {

							public void handleException(Throwable exception)
							{
								exception.printStackTrace();

							}

							@SuppressWarnings("unchecked")
							public void run() throws Exception
							{
								AbstractVdmBuilder builder = (AbstractVdmBuilder) o;

								// if
								// (currentProject.hasNature(builder.getNatureId()))
								// {

								AstManager.instance().clean(currentProject);// we do now support incremental
																			// build

								SourceParserManager.parseMissingFiles(currentProject,
										monitor);

								final RootNode rootNode = AstManager.instance()
										.getRootNode(currentProject);
								// if the project don't have parse errors
								if (rootNode != null
										&& rootNode.isParseCorrect())
								{
									if (Activator.DEBUG)
										System.out.println("Type correct .. building");
									monitor.subTask("Type checking");
									statusList.add(builder.buileModelElements(currentProject,
											rootNode));
									// mark ast root as type checked

									if (rootNode != null)
									{
										rootNode.setChecked(statusList.get(statusList.size() - 1)
												.getCode() < IStatus.ERROR);
									}
								}
							}
							// }
						};
						SafeRunner.run(runnable);
					}
				}
			}
		} catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}

	}
}
