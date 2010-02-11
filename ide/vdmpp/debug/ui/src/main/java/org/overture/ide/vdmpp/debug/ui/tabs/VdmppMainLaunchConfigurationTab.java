/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.overture.ide.vdmpp.debug.ui.tabs;

import org.overture.ide.ast.AstManager;
import org.overture.ide.ast.NotAllowedException;
import org.overture.ide.ast.RootNode;
import org.overture.ide.debug.ui.tabs.VdmMainLaunchConfigurationTab;
import org.overture.ide.utility.VdmProject;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.messages.VDMErrorsException;
import org.overturetool.vdmj.runtime.ClassInterpreter;

/**
 * Main launch configuration tab for overture scripts
 */
public class VdmppMainLaunchConfigurationTab extends VdmMainLaunchConfigurationTab {

	public VdmppMainLaunchConfigurationTab(String mode) {
		super(mode);
	}
	
	@Override
	protected String getNatureID() {
		return VdmPpProjectNature.VDM_PP_NATURE;
	}

	@Override
	protected boolean validateTypes(String module, String operation)
	{
		try
		{
			Settings.dialect = Dialect.VDM_PP;
			Settings.release = new VdmProject(getProject().getProject()).getLanguageVersion();
			RootNode root =  AstManager.instance().getRootNode(getProject().getProject(), getNatureID());
			if(root==null ||!root.isChecked())
			{
				new VdmProject(getProject().getProject()).typeCheck(false, null);
				VdmProject.waitForBuidCompletion();
				root =  AstManager.instance().getRootNode(getProject().getProject(), getNatureID());
				
			}	
			ClassList classes = root.getClassList();
			ClassInterpreter ci = new ClassInterpreter(classes);
//			if(module.contains("`"))
//			ci.setDefaultName(module.substring(0,module.indexOf("("))); //needed for static fn/op check
			
			
			ci.typeCheck("new "+module+"."+operation);
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
