package org.overture.ide.vdmrt.debug.ui.tabs;

import org.overture.ide.ast.AstManager;
import org.overture.ide.ast.NotAllowedException;
import org.overture.ide.ast.RootNode;
import org.overture.ide.debug.ui.tabs.VdmMainLaunchConfigurationTab;
import org.overture.ide.utility.VdmProject;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.messages.VDMErrorsException;
import org.overturetool.vdmj.runtime.ClassInterpreter;

/**
 * Main launch configuration tab for overture scripts
 */
public class VdmRtMainLaunchConfigurationTab extends VdmMainLaunchConfigurationTab {

	public VdmRtMainLaunchConfigurationTab(String mode) {
		super(mode);
	}
	@Override
	protected String getNatureID() {
		return VdmRtProjectNature.VDM_RT_NATURE;
	}
	@Override
	protected boolean validateTypes(String module, String operation)
	{
		try
		{
			Settings.dialect = Dialect.VDM_RT;
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
//				ci.setDefaultName(module.substring(0,module.indexOf("("))); //needed for static fn/op check
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

