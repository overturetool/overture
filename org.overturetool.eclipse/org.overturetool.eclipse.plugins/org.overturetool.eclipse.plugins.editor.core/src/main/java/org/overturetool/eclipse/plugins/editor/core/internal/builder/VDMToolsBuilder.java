package org.overturetool.eclipse.plugins.editor.core.internal.builder;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IScriptProject;
import org.overturetool.eclipse.plugins.editor.core.OverturePlugin;
import org.overturetool.vdmtools.VDMToolsError;
import org.overturetool.vdmtools.VDMToolsProject;
import org.overturetool.vdmtools.VDMToolsWarning;

/***
 * 
 * @author kedde
 * This class builds and parses the model using VDMTools
 *
 */
public class VDMToolsBuilder extends Builder {

	private VDMToolsProject vdmToolsProject;
	
	public VDMToolsBuilder(IScriptProject project, String  vdmtoolsPath) {
		super(project);
		vdmToolsProject = VDMToolsProject.getInstance();
//		vdmToolsProject.setPath(vdmtoolsPath);
	}

	@Override
	public IStatus typeCheck() {
		boolean hasError = false;
		try {
			vdmToolsProject.addFilesToProject(getSoruceFiles());
			clearMarkers();

			// *******************
			// Parse errors
			// *******************
			
			ArrayList<VDMToolsError> errorList = vdmToolsProject.parseProject();
			for (VDMToolsError error : errorList) {
				this.addMarker(
						error.getFilename(),
						error.getMessage(),
						error.getLineNr(),
						IMarker.SEVERITY_ERROR,
						error.getColNr(),
						error.getColNr());
			}
			if (errorList.size() > 0){
				hasError = true;
			}
			// *******************
			// Type checking 
			// *******************
			if (!hasError)
			{
				vdmToolsProject.typeCheckProject();
				// errors
				errorList = vdmToolsProject.getTypeCheckErrorList();
				for (VDMToolsError error : errorList) {
					this.addMarker(
							error.getFilename(),
							error.getMessage(),
							error.getLineNr(),
							IMarker.SEVERITY_ERROR,
							error.getColNr(),
							error.getColNr());
				}
				if (errorList.size() > 0){
					hasError = true;
				}
				// warnings
				ArrayList<VDMToolsWarning> warningList = vdmToolsProject.getTypeCheckWarningList();
				for (VDMToolsWarning toolsWarning : warningList) {
					this.addMarker(
							toolsWarning.getFilename(),
							toolsWarning.getMessage(),
							toolsWarning.getLineNr(),
							IMarker.SEVERITY_WARNING,
							toolsWarning.getColNr(),
							toolsWarning.getColNr());
				}
			}
		} catch (Exception e) {
			hasError = true;
		}
		
		if (hasError){
			IStatus typeChecked = new Status(
					IStatus.ERROR,
					OverturePlugin.PLUGIN_ID,
					0,
					"not typechecked",
					null);
			return typeChecked;			
		}
		else
		{
			
			IStatus typeChecked = new Status(
					IStatus.OK,
					OverturePlugin.PLUGIN_ID,
					0,
					"Type Checked",
					null);
			return typeChecked;	
		}
	}

}