package org.overture.ide.vdmsl.ui.wizards;

import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.dltk.internal.ui.scriptview.ScriptExplorerPart;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.wizards.NewSourceModulePage;
import org.eclipse.dltk.ui.wizards.NewSourceModuleWizard;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IViewPart;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;

@SuppressWarnings("restriction")
public class VdmSlNewModuleWizard extends NewSourceModuleWizard {

	public VdmSlNewModuleWizard() {
		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
		setWindowTitle("new VDM-SL module");
	}

	@Override
	protected NewSourceModulePage createNewSourceModulePage() {
		return new NewSourceModulePage() {
			
			@Override
			protected String getRequiredNature() {
				return VdmSlProjectNature.VDM_SL_NATURE;
			}
			
			@SuppressWarnings({ "unused", "deprecation" })
			@Override
			public String getScriptFolderText()
			{

				for (IViewPart view : getWorkbench().getActiveWorkbenchWindow()
						.getActivePage()
						.getViews())
				{
					if (view instanceof ScriptExplorerPart)
					{
						TreeViewer g = ((ScriptExplorerPart) view).getTreeViewer();
						ISelection se = g.getSelection();
						if (se instanceof TreeSelection)
						{
							Object dd = ((TreeSelection) se).getFirstElement();
							int e = 0;
							if (dd instanceof ScriptProject)
							{
								return ((ScriptProject) dd).getProject()
										.getName();
							}
						}
					}
				}
				return "";
			}

			@Override
			protected String getPageTitle()
			{
				containerChanged();
				return "VDM-SL Module";
			}
			
			@Override
			protected String getPageDescription() {
				return "Create a new VDM-SL class";
			}
			
			@Override
			protected String getFileContent() {
				String moduleName = getFileText();
				return "module " + moduleName + "\n"
				+ "exports all\n"
				+ "definitions \n\n"
				+ "\tstate StateName of\n \n\n"
				+ "\tend \n\n"
				+ "\ttypes \n\n"
				+ "\tvalues \n\n"
				+ "\tfunctions \n\n"
				+ "\toperations \n\n"
				+ "end " + moduleName;
			}
		};
	}
}
