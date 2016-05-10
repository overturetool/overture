/*
 * #%~
 * org.overture.ide.debug
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
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
		List<ILaunchConfigurationTab> tabs = new ArrayList<ILaunchConfigurationTab>();
		tabs.add(getMainTab());
		tabs.add(getRuntimeTab());
		tabs.add(new VmArgumentsLaunchConfigurationTab());
		tabs.add(new VdmDevelopLaunchConfigurationTab());
		tabs.addAll(getAdditionalTabs());
		tabs.add(new SourceLookupTab());
		tabs.add(new CommonTab());
		setTabs(tabs.toArray(new ILaunchConfigurationTab[tabs.size()]));

	}

	/**
	 * Provides the main launch tab as an implementation of VdmMainLaunchConfigurationTab
	 */
	protected abstract AbstractVdmMainLaunchConfigurationTab getMainTab();

	/**
	 * Provides the runtime launch tab
	 * 
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
		return new ArrayList<ILaunchConfigurationTab>();
	}

}
