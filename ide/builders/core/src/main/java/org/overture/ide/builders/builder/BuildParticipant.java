package org.overture.ide.builders.builder;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.builder.IScriptBuilder;
import org.overture.ide.ast.AstManager;
import org.overture.ide.builders.core.VdmSlBuilderCorePluginConstants;

public class BuildParticipant implements IScriptBuilder
{
	// This must be the ID from your extension point
	private static final String BUILDER_ID = "org.overture.ide.builder";

	public BuildParticipant()
	{
		// TODO Auto-generated constructor stub
	}

	public IStatus buildModelElements(IScriptProject project, List elements,
			IProgressMonitor monitor, int status)
	{
		System.out.println("buildModelElements");

		

		// TODO Auto-generated method stub

		// try
		// {
		// monitor.beginTask("Type checking", IProgressMonitor.UNKNOWN);
		// VdmjBuilder vdmjBuilder = new VdmjBuilder(project.getProject(), tmo);
		// IStatus typeCheck = vdmjBuilder.typeCheck();
		// monitor.done();
		// return typeCheck;
		// } catch (Exception ex)
		// {
		// ex.printStackTrace();
		//
		// }
		// return null;
		final IProject currentProject = project.getProject();
		final List<IStatus> statusList = new Vector<IStatus>();
		try
		{
			IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
					BUILDER_ID);
			for (IConfigurationElement e : config)
			{
				final Object o = e.createExecutableExtension("class");
				if (o instanceof AbstractBuilder)
				{
					ISafeRunnable runnable = new ISafeRunnable()
					{

						public void handleException(Throwable exception)
						{
							// TODO Auto-generated method stub

						}

						public void run() throws Exception
						{
							AbstractBuilder builder = (AbstractBuilder) o;

							if (currentProject.hasNature(builder.getNatureId()))
							{
								final List ast = (List) AstManager.instance().getAstList(
										currentProject,
										builder.getNatureId());

								statusList.add(builder.buileModelElements(
										currentProject,
										ast));
							}
						}

					};
					SafeRunner.run(runnable);
				}
			}
		} catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}

		for (IStatus s : statusList)
		{
			if (!s.isOK())
				return s;
		}
		if (statusList.size() > 0)
		{
			//TODO mark ast root as type checked AstManager.instance().setAstAsTypeChecked(project, nature)
			return statusList.get(0);
		}
		else
			return new Status(IStatus.WARNING,
					VdmSlBuilderCorePluginConstants.PLUGIN_ID,
					"No builder returned any result");
	}

	public IStatus buildResources(IScriptProject project, List resources,
			IProgressMonitor monitor, int status)
	{
		System.out.println("buildResources");
		// TODO Auto-generated method stub
		return null;
	}

	public void clean(IScriptProject project, IProgressMonitor monitor)
	{
		System.out.println("clean");
		// TODO Auto-generated method stub

	}

	public void endBuild(IScriptProject project, IProgressMonitor monitor)
	{
		System.out.println("endBuild");
		// TODO Auto-generated method stub

	}

	public DependencyResponse getDependencies(IScriptProject project,
			int buildType, Set localElements, Set externalElements,
			Set oldExternalFolders, Set externalFolders)
	{
		System.out.println("DependencyResponse");
		// TODO Auto-generated method stub
		return null;
	}

	public void initialize(IScriptProject project)
	{
		// TODO Auto-generated method stub
		System.out.println("initialize");
		
		AbstractBuilder.syncProjectResources(project.getProject());
		
		AbstractBuilder.clearProblemMarkers(project.getProject());
			
			
			
			
	}

}
