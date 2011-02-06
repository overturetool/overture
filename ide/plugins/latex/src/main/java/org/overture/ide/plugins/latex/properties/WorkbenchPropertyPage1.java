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
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.latex.ILatexConstants;

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
		String documentName = null;
		try
		{
			String tmp = project.getPersistentProperty(getQualifierName(ILatexConstants.LATEX_MAIN_DOCUMENT));
			if (tmp != null)
			{
				documentName = tmp;
			}
		} catch (CoreException e1)
		{
			e1.printStackTrace();
		}
		if (documentName == null)
			fileNameText.setText(project.getProject().getName() + ".tex");
		else
			fileNameText.setText(documentName);
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
						+ project.getProject().getName()
						+ ".tex");

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
		try
		{
			String tmp = project.getPersistentProperty(getQualifierName(ILatexConstants.LATEX_GENERATE_MAIN_DOCUMENT));

			useAutoReportGeneration.setSelection(tmp == null
					|| tmp.equalsIgnoreCase("true"));

		} catch (CoreException e1)
		{
		}

		buttonInsertCoverageTables = new Button(mainDocumentGroup, SWT.CHECK);
		buttonInsertCoverageTables.setText("Insert coverage tables");
		try
		{
			String tmp = project.getPersistentProperty(getQualifierName(ILatexConstants.LATEX_INCLUDE_COVERAGETABLE));
			buttonInsertCoverageTables.setSelection(tmp == null
					|| tmp.equalsIgnoreCase("true"));

		} catch (CoreException e1)
		{
		}

		buttonMarkCoverage = new Button(mainDocumentGroup, SWT.CHECK);
		buttonMarkCoverage.setText("Mark coverage");
		try
		{
			String tmp = project.getPersistentProperty(getQualifierName(ILatexConstants.LATEX_MARK_COVERAGE));
			buttonMarkCoverage.setSelection(tmp == null
					|| tmp.equalsIgnoreCase("true"));

		} catch (CoreException e1)
		{
		}
		
		buttonModelOnly = new Button(mainDocumentGroup, SWT.CHECK);
		buttonModelOnly.setText("Model only");
		try
		{
			String tmp = project.getPersistentProperty(getQualifierName(ILatexConstants.LATEX_MODEL_ONLY));
			buttonModelOnly.setSelection(tmp == null
					|| tmp.equalsIgnoreCase("true"));

		} catch (CoreException e1)
		{
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
			
//			if (useAutoReportGeneration.getSelection())
//			{
//				project.setPersistentProperty(getQualifierName(ILatexConstants.LATEX_MAIN_DOCUMENT), null);
//				project.setPersistentProperty(getQualifierName(ILatexConstants.LATEX_GENERATE_MAIN_DOCUMENT), "true");
//				// project.setMainDocument("");
//			} else
//			{
				project.setPersistentProperty(getQualifierName(ILatexConstants.LATEX_MAIN_DOCUMENT), fileNameText.getText());
//				project.setPersistentProperty(getQualifierName(ILatexConstants.LATEX_GENERATE_MAIN_DOCUMENT), "false");
//				// project.setMainDocument(fileNameText.getText());
//			}

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
				Object project = selection.getPaths()[0].getFirstSegment();
				if (project instanceof IProject)
					selectedProject = (IProject) project;
				else if (project instanceof IVdmProject)
					selectedProject = (IProject) ((IVdmProject) project).getAdapter(IProject.class);

			}
		} else if (selectedItem instanceof IStructuredSelection)
		{
			IStructuredSelection selection = (IStructuredSelection) selectedItem;
			if (selection.getFirstElement() instanceof IProject)
				selectedProject = (IProject) selection.getFirstElement();
		}

		return selectedProject;
	}

}
