package org.overture.ide.debug.ui.launching;

import java.util.List;
import java.util.Vector;

import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;

public abstract class AbstractVdmLaunchConfigurationTabGroup extends
		org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup
{
	public void createTabs(ILaunchConfigurationDialog dialog, String mode)
	{
		List<ILaunchConfigurationTab> tabs = new Vector<ILaunchConfigurationTab>();
		tabs.add(getMainTab());
		tabs.add(getRuntimeTab());
		tabs.add(new SourceLookupTab());
		tabs.add(new CommonTab());
		tabs.add(new VmArgumentsLaunchConfigurationTab());
		tabs.add(new VdmDevelopLaunchConfigurationTab());
		tabs.addAll(getAdditionalTabs());
		setTabs(tabs.toArray(new ILaunchConfigurationTab[tabs.size()]));

	}

	/**
	 * Provides the main launch tab as an implementation of VdmMainLaunchConfigurationTab
	 */
	protected abstract AbstractVdmMainLaunchConfigurationTab getMainTab();

	/**
	 * Provides the runtime launch tab
	 * @return must return an instance of VdmRuntimeChecksLaunchConfigurationTab or a subclass
	 */
	protected VdmRuntimeChecksLaunchConfigurationTab getRuntimeTab()
	{
		return new VdmRuntimeChecksLaunchConfigurationTab();
	}
	
	/**
	 * Provides additional tab pages to the tab group
	 */
	protected List<ILaunchConfigurationTab> getAdditionalTabs()
	{
		return new Vector<ILaunchConfigurationTab>();
	}

}
