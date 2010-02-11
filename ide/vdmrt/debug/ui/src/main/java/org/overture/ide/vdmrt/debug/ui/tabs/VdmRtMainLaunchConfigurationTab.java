package org.overture.ide.vdmrt.debug.ui.tabs;

import org.overture.ide.ast.AstManager;
import org.overture.ide.ast.NotAllowedException;
import org.overture.ide.debug.ui.tabs.VdmMainLaunchConfigurationTab;
import org.overture.ide.utility.VdmProject;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
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
			ClassList classes = AstManager.instance().getRootNode(getProject().getProject(), getNatureID()).getClassList();
			ClassInterpreter ci = new ClassInterpreter(classes);
			ci.typeCheck("new "+module+"."+operation);
			return true;
		} catch (NotAllowedException e)
		{
			setErrorMessage(e.toString());
			e.printStackTrace();
		} catch (Exception e)
		{
			setErrorMessage(e.toString());
		}
		
		return false;
	}
}

