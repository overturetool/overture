/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.overture.ide.vdmsl.debug.ui.tabs;

import org.overture.ide.ast.AstManager;
import org.overture.ide.ast.NotAllowedException;
import org.overture.ide.ast.RootNode;
import org.overture.ide.debug.ui.tabs.VdmMainLaunchConfigurationTab;
import org.overture.ide.utility.VdmProject;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.messages.VDMErrorsException;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.runtime.ModuleInterpreter;

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
	@Override
	protected boolean validateTypes(String module, String operation)
	{
		try
		{
			Settings.dialect = Dialect.VDM_SL;
			Settings.release = new VdmProject(getProject().getProject()).getLanguageVersion();
			RootNode root =  AstManager.instance().getRootNode(getProject().getProject(), getNatureID());
			if(root==null ||!root.isChecked())
			{
				new VdmProject(getProject().getProject()).typeCheck(false, null);
				VdmProject.waitForBuidCompletion();
				root =  AstManager.instance().getRootNode(getProject().getProject(), getNatureID());
				
			}				
			ModuleList modules =root.getModuleList();
			modules.combineDefaults();
			ModuleInterpreter ci = new ModuleInterpreter(modules);
			ci.setDefaultName(module);
			ci.typeCheck(module+"`"+operation);
			return true;
		} catch (NotAllowedException e)
		{
			setErrorMessage(e.toString());
			e.printStackTrace();
		}catch(VDMErrorsException e)
		{
			setErrorMessage(e.toString());
		}
		catch (Exception e)
		{
			if(e.getMessage()!=null && e.getMessage().contains("not loaded"))
				setErrorMessage(e.getMessage());
			else
			setErrorMessage("Intrnal type check error: "+e.toString());
		}
		
		return false;
	}
}