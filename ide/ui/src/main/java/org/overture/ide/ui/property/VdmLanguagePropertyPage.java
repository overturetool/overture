/*
 * #%~
 * org.overture.ide.ui
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.ui.property;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.overture.config.Release;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;

@SuppressWarnings("restriction")
public class VdmLanguagePropertyPage extends PropertyPage implements
		IWorkbenchPropertyPage
{
	private Combo comboBoxLanguageVersion = null;
	private Button checkBoxSuppressWarnings = null;
	private Button checkBoxUseStrictLetDef = null;

	private IVdmProject project = null;

	private Group typeGroup;

	public VdmLanguagePropertyPage()
	{
		super();
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());

		IProject p = getSelectedProject();

		if (p != null)
		{
			this.project = (IVdmProject) p.getAdapter(IVdmProject.class);
			Assert.isNotNull(this.project, "Project could not be adapted");

			createLanguagePanel(comp);
			createTypeCheckGroup(comp);
		}
		return comp;
	}

	private Group createGroup(String name, Composite comp)
	{
		Group group = new Group(comp, SWT.NONE);
		group.setText(name);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		group.setLayout(layout);
		return group;
	}

	void createLanguagePanel(Composite composite)
	{
		Group languageOptionsGroup = createGroup("Language options", composite);

		Label mylabel = new Label(languageOptionsGroup, SWT.NONE);
		mylabel.setLayoutData(new GridData());
		mylabel.setText("Language version");
		comboBoxLanguageVersion = new Combo(languageOptionsGroup, SWT.READ_ONLY);

		String[] languageTypes = new String[Release.values().length];
		int i = 0;
		for (Release r : Release.values())
		{
			languageTypes[i] = r.toString();
			i++;
		}

		comboBoxLanguageVersion.setItems(languageTypes);
		comboBoxLanguageVersion.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		try
		{
			if (project.getLanguageVersion() != null)
				for (int j = 0; j < languageTypes.length; j++)
				{
					if (project.getLanguageVersion().toString().equals(languageTypes[j]))
						comboBoxLanguageVersion.select(j);
				}
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void createTypeCheckGroup(Composite comp)
	{
		typeGroup = createGroup("Type checking", comp);

		checkBoxSuppressWarnings = new Button(typeGroup, SWT.CHECK);
		checkBoxSuppressWarnings.setText("Suppress warnings");
		checkBoxSuppressWarnings.setSelection(project.hasSuppressWarnings());
		
		checkBoxUseStrictLetDef = new Button(typeGroup, SWT.CHECK);
		checkBoxUseStrictLetDef.setText("Strict let def checks");
		checkBoxUseStrictLetDef.setSelection(project.hasUseStrictLetDef());

	}

	@SuppressWarnings( { "deprecation" })
	public static IProject getSelectedProject()
	{
		ISelection selectedItem = WorkbenchPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
		IProject selectedProject = null;

		if (selectedItem instanceof ITreeSelection)
		{
			ITreeSelection selection = (ITreeSelection) selectedItem;
			if (selection.getPaths().length > 0)
			{
				Object project = selection.getPaths()[0].getLastSegment();
				if (project instanceof IProject)
					selectedProject = (IProject) project;

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
			project.setBuilder(Release.lookup(comboBoxLanguageVersion.getText()));
			project.setSuppressWarnings(checkBoxSuppressWarnings.getSelection());
			project.setUseStrictLetDef(checkBoxUseStrictLetDef.getSelection());

			VdmTypeCheckerUi.typeCheck(getShell(), project);
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.performOk();
	}
}
