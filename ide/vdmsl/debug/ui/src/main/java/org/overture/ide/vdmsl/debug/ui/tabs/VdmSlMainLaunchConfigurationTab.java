/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.overture.ide.vdmsl.debug.ui.tabs;

import org.overture.ide.debug.ui.tabs.VdmMainLaunchConfigurationTab;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;

/**
 * Main launch configuration tab for overture scripts
 * Overrides default implementation by setting Module as label and specifying the nature
 */
public class VdmSlMainLaunchConfigurationTab  extends VdmMainLaunchConfigurationTab {

	public VdmSlMainLaunchConfigurationTab(String mode) {
		super(mode);
	}
	@Override
	protected String getNatureID() {
		return VdmSlProjectNature.VDM_SL_NATURE;
	}
	
	@Override
	protected String getModuleLabelName()
	{
		return "Module";
	}
}