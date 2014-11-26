/*
 * #%~
 * org.overture.ide.plugins.latex
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
package org.overture.ide.plugins.latex.properties;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.overture.ide.core.resources.IOptionGroup;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.latex.ILatexConstants;
import org.overture.ide.plugins.latex.LatexPlugin;

@SuppressWarnings("restriction")
public class WorkbenchPropertyPage1 extends PropertyPage implements
		IWorkbenchPropertyPage
{
	private Button buttonSelectFile = null;
	private Button useAutoReportGeneration = null;
	private Button buttonInsertCoverageTables = null;
	private Button buttonMarkCoverage = null;
	private Button buttonModelOnly = null;
	private Text fileNameText;
	private IProject project;

	public static org.eclipse.core.runtime.QualifiedName getQualifierName(
			String propertyName)
	{
		return new org.eclipse.core.runtime.QualifiedName(ILatexConstants.QUALIFIER, propertyName);
	}

	public WorkbenchPropertyPage1()
	{
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Control createContents(Composite parent)
	{
		// org.eclipse.swt.widgets.FileDialog

		final Composite myComposite = new Composite(parent, SWT.NONE);
		try
		{
			FillLayout layout = new FillLayout();
			// layout.numColumns = 1;
			layout.type = SWT.VERTICAL;
			myComposite.setLayout(layout);

			ISelection selection = WorkbenchPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
			this.project = getSelectedProject(selection);// new LatexProject(getSelectedProject(selection));

			Group mainDocumentGroup = new Group(myComposite, SWT.NONE);
			mainDocumentGroup.setText("Latex options");
			GridLayout mylayout = new GridLayout();
			mylayout.marginHeight = 2;
			mylayout.marginWidth = 2;
			mylayout.horizontalSpacing = 20;
			mylayout.numColumns = 2;
			mainDocumentGroup.setLayout(mylayout);

			Label mylabel = new Label(mainDocumentGroup, SWT.NONE);
			mylabel.setLayoutData(new GridData());
			mylabel.setText("Main document");

			fileNameText = new Text(mainDocumentGroup, SWT.FILL);
			fileNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			IVdmProject p = (IVdmProject) project.getAdapter(IVdmProject.class);
			IOptionGroup opt = p.getOptions().getGroup(LatexPlugin.PLUGIN_ID, true);

			String documentName = null;
			String tmpDoc = opt.getAttribute(ILatexConstants.LATEX_MAIN_DOCUMENT, null);
			if (tmpDoc != null)
			{
				documentName = tmpDoc;
			}
			if (documentName == null)
			{
				fileNameText.setText(project.getProject().getName() + ".tex");
			} else
			{
				fileNameText.setText(documentName);
			}
			buttonSelectFile = new Button(mainDocumentGroup, SWT.NONE);
			buttonSelectFile.setLayoutData(new GridData());
			buttonSelectFile.setText("Browse...");
			buttonSelectFile.addSelectionListener(new SelectionListener()
			{

				public void widgetSelected(SelectionEvent e)
				{
					org.eclipse.swt.widgets.FileDialog dialog = new FileDialog(myComposite.getShell());
					dialog.setFileName(project.getProject().getLocation().toFile().getAbsolutePath()
							+ File.separatorChar
							+ project.getProject().getName() + ".tex");

					fileNameText.setText(dialog.open());
					useAutoReportGeneration.setSelection(fileNameText.getText().trim().length() == 0);

				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
					// TODO Auto-generated method stub

				}
			});

			useAutoReportGeneration = new Button(mainDocumentGroup, SWT.CHECK);
			useAutoReportGeneration.setText("Generate main document");
			useAutoReportGeneration.setSelection(opt.getAttribute(ILatexConstants.LATEX_GENERATE_MAIN_DOCUMENT, true));

			buttonInsertCoverageTables = new Button(mainDocumentGroup, SWT.CHECK);
			buttonInsertCoverageTables.setText("Insert coverage tables");
			buttonInsertCoverageTables.setSelection(opt.getAttribute(ILatexConstants.LATEX_INCLUDE_COVERAGETABLE, ILatexConstants.LATEX_INCLUDE_COVERAGETABLE_DEFAULT));

			buttonMarkCoverage = new Button(mainDocumentGroup, SWT.CHECK);
			buttonMarkCoverage.setText("Mark coverage");
			buttonMarkCoverage.setSelection(opt.getAttribute(ILatexConstants.LATEX_MARK_COVERAGE, ILatexConstants.LATEX_MARK_COVERAGE_DEFAULT));

			buttonModelOnly = new Button(mainDocumentGroup, SWT.CHECK);
			buttonModelOnly.setText("Model only");
			buttonModelOnly.setSelection(opt.getAttribute(ILatexConstants.LATEX_MODEL_ONLY, true));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return myComposite;
	}

	@Override
	public boolean performOk()
	{
		try
		{
			if (useAutoReportGeneration.getSelection())
			{
				project.setPersistentProperty(getQualifierName(ILatexConstants.LATEX_GENERATE_MAIN_DOCUMENT), "true");
			} else
			{
				project.setPersistentProperty(getQualifierName(ILatexConstants.LATEX_GENERATE_MAIN_DOCUMENT), "false");
			}

			project.setPersistentProperty(getQualifierName(ILatexConstants.LATEX_MAIN_DOCUMENT), fileNameText.getText());

			if (buttonInsertCoverageTables.getSelection())
			{
				project.setPersistentProperty(getQualifierName(ILatexConstants.LATEX_INCLUDE_COVERAGETABLE), "true");
			} else
			{
				project.setPersistentProperty(getQualifierName(ILatexConstants.LATEX_INCLUDE_COVERAGETABLE), "false");
			}

			if (buttonMarkCoverage.getSelection())
			{
				project.setPersistentProperty(getQualifierName(ILatexConstants.LATEX_MARK_COVERAGE), "true");
			} else
			{
				project.setPersistentProperty(getQualifierName(ILatexConstants.LATEX_MARK_COVERAGE), "false");
			}

			if (buttonModelOnly.getSelection())
			{
				project.setPersistentProperty(getQualifierName(ILatexConstants.LATEX_MODEL_ONLY), "true");
			} else
			{
				project.setPersistentProperty(getQualifierName(ILatexConstants.LATEX_MODEL_ONLY), "false");
			}
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IVdmProject p = (IVdmProject) project.getAdapter(IVdmProject.class);
		if (p != null)
		{
			IOptionGroup opt = p.getOptions().getGroup(LatexPlugin.PLUGIN_ID, true);

			if (useAutoReportGeneration.getSelection())
			{
				opt.setAttribute(ILatexConstants.LATEX_GENERATE_MAIN_DOCUMENT, true);
			} else
			{
				opt.setAttribute(ILatexConstants.LATEX_GENERATE_MAIN_DOCUMENT, false);
			}

			opt.setAttribute(ILatexConstants.LATEX_MAIN_DOCUMENT, fileNameText.getText());

			if (buttonInsertCoverageTables.getSelection())
			{
				opt.setAttribute(ILatexConstants.LATEX_INCLUDE_COVERAGETABLE, true);
			} else
			{
				opt.setAttribute(ILatexConstants.LATEX_INCLUDE_COVERAGETABLE, false);
			}

			if (buttonMarkCoverage.getSelection())
			{
				opt.setAttribute(ILatexConstants.LATEX_MARK_COVERAGE, true);
			} else
			{
				opt.setAttribute(ILatexConstants.LATEX_MARK_COVERAGE, false);
			}

			if (buttonModelOnly.getSelection())
			{
				opt.setAttribute(ILatexConstants.LATEX_MODEL_ONLY, true);
			} else
			{
				opt.setAttribute(ILatexConstants.LATEX_MODEL_ONLY, false);
			}

			opt.getOptions().save();
		}

		return super.performOk();
	}

	public static IProject getSelectedProject(ISelection selectedItem)
	{
		IProject selectedProject = null;

		if (selectedItem instanceof ITreeSelection)
		{
			ITreeSelection selection = (ITreeSelection) selectedItem;
			if (selection.getPaths().length > 0)
			{
				Object project = selection.getPaths()[0].getLastSegment();
				if (project instanceof IProject)
				{
					selectedProject = (IProject) project;
				} else if (project instanceof IVdmProject)
				{
					selectedProject = (IProject) ((IVdmProject) project).getAdapter(IProject.class);
				}

			}
		} else if (selectedItem instanceof IStructuredSelection)
		{
			IStructuredSelection selection = (IStructuredSelection) selectedItem;
			if (selection.getFirstElement() instanceof IProject)
			{
				selectedProject = (IProject) selection.getFirstElement();
			}
		}

		return selectedProject;
	}

}
