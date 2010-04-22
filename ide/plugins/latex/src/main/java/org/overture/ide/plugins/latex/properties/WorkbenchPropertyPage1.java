package org.overture.ide.plugins.latex.properties;

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
import org.overture.ide.plugins.latex.utility.LatexProject;

@SuppressWarnings("restriction")
public class WorkbenchPropertyPage1 extends PropertyPage implements
		IWorkbenchPropertyPage
{
	private Button buttonSelectFile = null;
	private Button useAutoReportGeneration = null;
	private Text fileNameText;
	private LatexProject project;

	public WorkbenchPropertyPage1() {
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

		ISelection selection = WorkbenchPlugin.getDefault()
				.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.getSelection();
		this.project = new LatexProject(getSelectedProject(selection));

		Group mainDocumentGroup = new Group(myComposite, SWT.NONE);
		mainDocumentGroup.setText("Main document selection");
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
			documentName = project.getMainDocument();
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
		buttonSelectFile.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e)
			{
				org.eclipse.swt.widgets.FileDialog dialog = new FileDialog(myComposite.getShell());
				dialog.setFileName(project.getProject()
						.getLocation()
						.toFile()
						.getAbsolutePath());
				fileNameText.setText(dialog.open());
				useAutoReportGeneration.setSelection(fileNameText.getText()
						.trim()
						.length() == 0);

			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				// TODO Auto-generated method stub

			}
		});

		useAutoReportGeneration = new Button(mainDocumentGroup, SWT.CHECK);
		useAutoReportGeneration.setText("Use automatic report generation");
		try
		{
			useAutoReportGeneration.setSelection(!project.hasDocument());
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
				project.setMainDocument("");
			else
				project.setMainDocument(fileNameText.getText());
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
					selectedProject = ((IVdmProject) project);

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
