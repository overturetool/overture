//package org.overture.ide.debug.ui.launching;
//
//import org.eclipse.debug.core.ILaunchConfiguration;
//import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
//import org.eclipse.debug.internal.ui.launchConfigurations.EnvironmentVariable;
//import org.eclipse.debug.ui.EnvironmentTab;
//import org.eclipse.swt.widgets.TableItem;
//import org.overture.ide.debug.core.DebugCoreConstants;
//
//public class VdmEnvironmentTab extends EnvironmentTab  {
//
//	VdmMainLaunchConfigurationTab launchTab;
//	
//	public VdmEnvironmentTab(VdmMainLaunchConfigurationTab launchTab) {
//		this.launchTab = launchTab;
//	}
//
//	@Override
//	protected void setAttribute(String attribute,
//			ILaunchConfigurationWorkingCopy configuration, boolean value,
//			boolean defaultValue)
//	{
//		// TODO Auto-generated method stub
//		super.setAttribute(attribute, configuration, value, defaultValue);
//	}
//	@Override
//	public void performApply(ILaunchConfigurationWorkingCopy configuration)
//	{
//		TableItem[] items = environmentTable.getTable().getItems();
//		//	Map map = new HashMap(items.length);
//		for (int i = 0; i < items.length; i++)
//		{
//			EnvironmentVariable var = (EnvironmentVariable) items[i].getData();
//			//		map.put(var.getName(), var.getValue());
//
//			if(var.getName().equals("VM_MEMORY")&& var.getValue().length()>0)
//			{
//				launchTab.setVmOptions(var.getValue());
//				configuration.setAttribute(DebugCoreConstants.DEBUGGING_VM_MEMORY_OPTION, var.getValue());
//
//			}
//		} 
//		super.performApply(configuration);
//	}
//	@Override
//	protected void updateEnvironment(ILaunchConfiguration configuration)
//	{
//		// TODO Auto-generated method stub
//		super.updateEnvironment(configuration);
//	}
//}
