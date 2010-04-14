package org.overture.ide.vdmpp.debug.ui.launchconfigurations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.VdmProject;
import org.overture.ide.debug.core.Activator;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.ui.launchconfigurations.AbstractVdmApplicationLaunchShortcut;
import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;

public class VdmApplicationLaunchShortcut extends AbstractVdmApplicationLaunchShortcut
{

	@Override
	public void launch(ISelection selection, String mode)
	{
		if (selection instanceof IStructuredSelection)
		{
			searchAndLaunch(((IStructuredSelection) selection).toArray(),
					mode,
					getTypeSelectionTitle(),
					getSelectionEmptyMessage());
		}
	}

	@Override
	public void launch(IEditorPart editor, String mode)
	{
		//IEditorInput input = editor.getEditorInput();
		// IJavaElement je = (IJavaElement) input.getAdapter(IJavaElement.class);
		// if (je != null) {
		// searchAndLaunch(new Object[] {je}, mode, getTypeSelectionTitle(), getEditorEmptyMessage());
		// }
	}

	@Override
	public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection)
	{
		// TODO Auto-generated method stub
		return new ILaunchConfiguration[0];
	}

	@Override
	public ILaunchConfiguration[] getLaunchConfigurations(IEditorPart editorpart)
	{
		// TODO Auto-generated method stub
		return new ILaunchConfiguration[0];
	}

	@Override
	public IResource getLaunchableResource(ISelection selection)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getLaunchableResource(IEditorPart editorpart)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a title for a type selection dialog used to prompt the user when there is more than one type
	 * that can be launched.
	 * 
	 * @return type selection dialog title
	 */
	@Override
	protected String getTypeSelectionTitle()
	{
		return "Type selection title";
	}

	/**
	 * Returns an error message to use when the editor does not contain a type that can be launched.
	 * 
	 * @return error message when editor cannot be launched
	 */
	@Override
	protected String getEditorEmptyMessage()
	{
		return "Empty editor input";
	}

	/**
	 * Returns an error message to use when the selection does not contain a type that can be launched.
	 * 
	 * @return error message when selection cannot be launched
	 */
	@Override
	protected String getSelectionEmptyMessage()
	{
		return "Empty selection";
	}

	/**
	 * Resolves a type that can be launched from the given scope and launches in the specified mode.
	 * 
	 * @param scope
	 *            the java elements to consider for a type that can be launched
	 * @param mode
	 *            launch mode
	 * @param selectTitle
	 *            prompting title for choosing a type to launch
	 * @param emptyMessage
	 *            error message when no types are resolved for launching
	 */
	private void searchAndLaunch(Object[] scope, String mode,
			String selectTitle, String emptyMessage)
	{
		IAstNode[] types = null;
		IProject project = null;
		try
		{
			types = findTypes(scope, PlatformUI.getWorkbench()
					.getProgressService());
			project = findProject(scope, PlatformUI.getWorkbench()
					.getProgressService());
		} catch (InterruptedException e)
		{
			return;
		} catch (CoreException e)
		{
			MessageDialog.openError(getShell(), "core error", e.getMessage());
			return;
		}
		IAstNode type = null;
		if (types.length == 0)
		{
			MessageDialog.openError(getShell(), "no type", emptyMessage);
		} else if (types.length > 1)
		{
			type = chooseType(types, selectTitle);
		} else
		{
			type = types[0];
		}
		if (type != null && project!=null)
		{
			launch(type, mode,project.getName());
		}
	}

	private void launch(IAstNode type, String mode,String projectName)
	{
		// TODO Auto-generated method stub
		
		
		ILaunchConfiguration config = findLaunchConfiguration(type, projectName,getConfigurationType());
		if (config == null) {
			config = createConfiguration(type,projectName);
		}
		if (config != null) {
			DebugUITools.launch(config, mode);
		}	

	}

	private IAstNode chooseType(IAstNode[] types, String selectTitle)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private IProject findProject(Object[] scope,
			IProgressService progressService) throws InterruptedException,
			CoreException
	{
		for (Object object : scope)
		{
			if (object instanceof IProject)
			{
				IProject project = (IProject) object;
				return project;

			}
		}
		return null;
	}

	private IAstNode[] findTypes(Object[] scope,
			IProgressService progressService) throws InterruptedException,
			CoreException
	{
		for (Object object : scope)
		{
			if (object instanceof IProject)
			{
				IProject project = (IProject) object;
				if (VdmProject.isVdmProject(project))
				{
					IVdmProject vdmProject = VdmProject.createProject(project);
					IVdmModel model = vdmProject.getModel();
					if (model.hasClassList())
					{
						try
						{
							for (ClassDefinition definition : model.getClassList())
							{
								for (Definition def : definition.getDefinitions())
								{
									if (def instanceof ExplicitOperationDefinition
											&& def.getName()
													.toLowerCase()
													.equals("main")
											&& ((ExplicitOperationDefinition) def).isStatic())
									{
										return new IAstNode[] { def };
									}
								}
							}
						} catch (NotAllowedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchShortcut#createConfiguration(org.eclipse.jdt
	 * .core.IType)
	 */
	@Override
	protected ILaunchConfiguration createConfiguration(IAstNode type, String projectName)
	{
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;
		try
		{
			ExplicitOperationDefinition operation = (ExplicitOperationDefinition) type;
			ILaunchConfigurationType configType = getConfigurationType();
			wc = configType.newInstance(null,
					getLaunchManager().generateUniqueLaunchConfigurationNameFrom(type.getName()));
			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PROJECT,
					projectName);
			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_CREATE_COVERAGE,
					false);
			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MODULE,
					operation.location.module);
			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DEFAULT,
					operation.location.module);
			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_OPERATION,
					operation.getName()+"()");
			// wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_EXPRESSION_SEPERATOR, "`");
			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_EXPRESSION,
					operation.location.module+"`" + operation.getName() + "()");
			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_STATIC_OPERATION,
					true);
			// wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
			// type.getJavaProject().getElementName());
			// wc.setMappedResources(new IResource[] {type.getUnderlyingResource()});
			config = wc.doSave();
		} catch (CoreException exception)
		{
			exception.printStackTrace();
			// MessageDialog.openError(JDIDebugUIPlugin.getActiveWorkbenchShell(),
			// LauncherMessages.JavaLaunchShortcut_3, exception.getStatus().getMessage());
		}
		return config;
	}

	private ILaunchManager getLaunchManager()
	{
		return DebugPlugin.getDefault().getLaunchManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchShortcut#getConfigurationType()
	 */
	@Override
	protected ILaunchConfigurationType getConfigurationType()
	{
		return getLaunchManager().getLaunchConfigurationType("org.overture.ide.vdmpp.debug.launchConfigurationType");// IDebugConstants.ATTR_VDM_PROGRAM);
	}

	/**
	 * Convenience method to return the active workbench window shell.
	 * 
	 * @return active workbench window shell
	 */
	@Override
	protected Shell getShell()
	{
		return Activator.getActiveWorkbenchShell();
	}
	
	/**
	 * Finds and returns an <b>existing</b> configuration to re-launch for the given type,
	 * or <code>null</code> if there is no existing configuration.
	 * 
	 * @return a configuration to use for launching the given type or <code>null</code> if none
	 */
	@Override
	protected ILaunchConfiguration findLaunchConfiguration(IAstNode type,String projectName, ILaunchConfigurationType configType) {
		List<ILaunchConfiguration> candidateConfigs = Collections.emptyList();
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(configType);
			candidateConfigs = new ArrayList<ILaunchConfiguration>(configs.length);
			for (int i = 0; i < configs.length; i++) {
				ILaunchConfiguration config = configs[i];
				ExplicitOperationDefinition operation = (ExplicitOperationDefinition) type;
				if (config.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MODULE, "").equals(operation.location.module)) { //$NON-NLS-1$
					if (config.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PROJECT, "").equals(projectName) && config.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_OPERATION, "").equals(type.getName()+"()")) { //$NON-NLS-1$
						candidateConfigs.add(config);
					}
				}
			}
		} catch (CoreException e) {
			//JDIDebugUIPlugin.log(e);
		}
		int candidateCount = candidateConfigs.size();
		if (candidateCount == 1) {
			return candidateConfigs.get(0);
		} else if (candidateCount > 1) {
			//return chooseConfiguration(candidateConfigs);
			return candidateConfigs.get(0);
		}
		return null;
	}

}
