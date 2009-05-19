package org.overturetool.eclipse.plugins.editor.internal.ui.wizards;

import java.util.ArrayList;

import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ToolType;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.jface.wizard.Wizard;
import org.overturetool.eclipse.plugins.editor.core.OvertureConstants;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;
import org.overturetool.eclipse.plugins.editor.core.internal.OvertureCorePreferenceInitializer;
import org.overturetool.vdmtools.VDMToolsProject;

public class OvertureGenerateCppWizard extends Wizard {
	OvertureGenerateCodeCppWizardPage cppWizard;
	private String[] exts = new String[] { "vpp", "tex", "vdm" };
	
	 public void addPages() {
		 cppWizard = new OvertureGenerateCodeCppWizardPage("C++ page");
		 addPage(cppWizard);
	 }
	 
	 /**
	 * This method returns a list of files under the given directory or its
	 * subdirectories. The directories themselves are not returned.
	 * 
	 * @param dir
	 *            a directory
	 * @return list of IResource objects representing the files under the given
	 *         directory and its subdirectories
	 */
	private ArrayList<IFile> getAllMemberFiles(IContainer dir, String[] exts) {
		ArrayList<IFile> list = new ArrayList<IFile>();
		IResource[] arr = null;
		try {
			arr = dir.members();
		} catch (CoreException e) {
		}

		for (int i = 0; arr != null && i < arr.length; i++) {
			if (arr[i].getType() == IResource.FOLDER) {
				list.addAll(getAllMemberFiles((IFolder) arr[i], exts));
			}
			else {
				for (int j = 0; j < exts.length; j++) {
					if (exts[j].equalsIgnoreCase(arr[i].getFileExtension())) {
						list.add((IFile) arr[i]);
						break;
					}
				}
			}
		}
		return list;
	}
	
	@Override
	public boolean performFinish() {
		try
		{
			VDMToolsProject vdmProject = VDMToolsProject.getInstance();
			IProject proj = null;
			for (IProject project : cppWizard.iprojects) {
				if (cppWizard.combo.getText().equals(project.getName())){
					proj = project;
				}
			}
			
			// Select interpreter:
			IInterpreterInstall interpreterInstall = null;
			try {
				
				if (ScriptRuntime.getInterpreterInstallType(OvertureConstants.VDMTOOLS_INTERPRETER_ID) != null)
				{
					if (ScriptRuntime.getInterpreterInstallType(OvertureConstants.VDMTOOLS_INTERPRETER_ID).getInterpreterInstalls() != null)
					{						
						interpreterInstall = ScriptRuntime.getInterpreterInstallType(OvertureConstants.VDMTOOLS_INTERPRETER_ID).getInterpreterInstalls()[0];
						String VDMToolsPath = interpreterInstall.getInstallLocation().toOSString();
						vdmProject.init(VDMToolsPath, ToolType.PP_TOOLBOX);
					}
				}
				else
				{
					System.out.println("VDMTools not installed");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// add files from project to VDMTools
			ArrayList<IFile> fileNameList = getAllMemberFiles(proj, exts);
			ArrayList<String> filenamesString = new ArrayList<String>();
			for (IFile file : fileNameList) {
				filenamesString.add(file.getLocationURI().getPath());
			}
			vdmProject.addFilesToProject(filenamesString);
			
			vdmProject.saveAs(cppWizard.textProjectPath.getText());
			
			vdmProject.codeGenerateCPP(false);
			return true;
		}
		catch (Exception e) {
			return false;
		}

	}

}
