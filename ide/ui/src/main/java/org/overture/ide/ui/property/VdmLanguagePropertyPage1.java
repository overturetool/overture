package org.overture.ide.ui.property;

import java.util.List;
import java.util.Vector;

import javax.swing.ComboBoxEditor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ObjectPluginAction;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.overture.ide.utility.VdmProject;
import org.overturetool.vdmj.*;

public class VdmLanguagePropertyPage1 extends PropertyPage implements
		IWorkbenchPropertyPage
{
	private Combo languageVersionCombobox = null;
	private VdmProject project = null;

	public VdmLanguagePropertyPage1() {
		super();
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite myComposite = new Composite(parent, SWT.NONE);
		GridLayout mylayout = new GridLayout();
		mylayout.marginHeight = 1;
		mylayout.marginWidth = 1;
		myComposite.setLayout(mylayout);
		Label mylabel = new Label(myComposite, SWT.NONE);
		mylabel.setLayoutData(new GridData());
		mylabel.setText("Language version");
		languageVersionCombobox = new Combo(myComposite, SWT.READ_ONLY);

		String[] languageTypes = new String[Release.values().length];
		int i = 0;
		for (Release r : Release.values())
		{
			languageTypes[i] = r.toString();
			i++;
		}

		languageVersionCombobox.setItems(languageTypes);
		languageVersionCombobox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		ISelection selection = WorkbenchPlugin.getDefault()
				.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.getSelection();
		this.project = new VdmProject(getSelectedProject(selection));
		
		try
		{
			if(project.getLanguageVersion()!=null)
				for (int j = 0; j < languageTypes.length; j++)
				{
					if(project.getLanguageVersion().toString().equals(languageTypes[j]))
						languageVersionCombobox.select(j);
				}
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return myComposite;
	}

	public static IProject getSelectedProject(ISelection selectedItem)
	{
		IProject selectedProject = null;

		if (selectedItem instanceof ITreeSelection)
		{
			ITreeSelection selection = (ITreeSelection) selectedItem;
			if (selection.getPaths().length > 0)
			{
				Object project = selection.getPaths()[0].getFirstSegment();
				if (project instanceof IProject)
					selectedProject = (IProject) project;
				else if (project instanceof IScriptProject)
					selectedProject = ((IScriptProject) project).getProject();

			}
		} else if (selectedItem instanceof IStructuredSelection)
		{
			IStructuredSelection selection = (IStructuredSelection) selectedItem;
			if (selection.getFirstElement() instanceof IProject)
				selectedProject = (IProject) selection.getFirstElement();
		}

		return selectedProject;
	}

	
	@Override
	public boolean performOk()
	{
	try
	{
		project.setBuilder(Release.valueOf(languageVersionCombobox.getText()));
		} catch (CoreException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return super.performOk();
	}
}
