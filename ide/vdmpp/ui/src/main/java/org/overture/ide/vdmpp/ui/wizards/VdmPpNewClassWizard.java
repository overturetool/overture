package org.overture.ide.vdmpp.ui.wizards;

import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.dltk.internal.ui.scriptview.ScriptExplorerPart;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.wizards.NewSourceModulePage;
import org.eclipse.dltk.ui.wizards.NewSourceModuleWizard;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IViewPart;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;

@SuppressWarnings("restriction")
public class VdmPpNewClassWizard extends NewSourceModuleWizard
{

	public VdmPpNewClassWizard() {
		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
		setWindowTitle("New VDM++ class");

	}

	@Override
	protected NewSourceModulePage createNewSourceModulePage()
	{
		return new NewSourceModulePage() {

			@Override
			protected String getRequiredNature()
			{

				return VdmPpProjectNature.VDM_PP_NATURE;
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

				return "VDM++ class";
			}

			@Override
			protected String getPageDescription()
			{
				return "Create a new VDM++ class";
			}

			@Override
			protected String getFileContent()
			{
				String className = getFileText();
				return "class " + className + "\n" + "\ttypes\n\n"
						+ "\tvalues\n\n" + "\tinstance variables\n\n"
						+ "\toperations\n\n" + "\tfunctions\n\n" + "\tsync\n\n"
						+ "\t--thread\n\n" + "\ttraces\n\n" + "end "
						+ className;
			}
		};
	}

}
