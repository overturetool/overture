package org.overture.ide.ui.property;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
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

import org.overture.ide.core.IVdmProject;
import org.overture.ide.core.VdmProject;
import org.overturetool.vdmj.Release;

@SuppressWarnings("restriction")
public class VdmLanguagePropertyPage extends PropertyPage implements
		IWorkbenchPropertyPage
{
	// private Combo languageVersionCombobox = null;
	private Combo comboBoxLanguageVersion = null;
	private Button checkBoxSuppressWarnings = null;
	private Button checkBoxUsePostChecks = null;
	private Button checkBoxUsePreChecks = null;
	private Button checkBoxInvChecks = null;
	private Button checkBoxDynamicTypeChecks = null;
	private IVdmProject project = null;

	private Group typeGroup;
	private Group interperterGroup;

	public VdmLanguagePropertyPage() {
		super();
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite myComposite = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout();

		layout.type = SWT.VERTICAL;
		myComposite.setLayout(layout);

		IProject p = getSelectedProject();
		
		if (p!=null && VdmProject.isVdmProject(p))
		{
			this.project = VdmProject.createProject(p);

			createLanguagePanel(myComposite);

			createTypeCheckGroup(myComposite);
			createInterperterGroupCheckGroup(myComposite);
		}
		return myComposite;
	}

	void createLanguagePanel(Composite composite)
	{
		Group languageOptionsGroup = new Group(composite, SWT.NONE);
		languageOptionsGroup.setText("Language options");
		GridLayout mylayout = new GridLayout();
		mylayout.marginHeight = 1;
		mylayout.marginWidth = 1;
		mylayout.horizontalSpacing = 20;
		mylayout.numColumns = 2;
		languageOptionsGroup.setLayout(mylayout);

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
					if (project.getLanguageVersion()
							.toString()
							.equals(languageTypes[j]))
						comboBoxLanguageVersion.select(j);
				}
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void createTypeCheckGroup(Composite controlGroup)
	{
		typeGroup = new Group(controlGroup, SWT.NONE);
		typeGroup.setText("Type checking");
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		typeGroup.setLayout(layout);

		checkBoxSuppressWarnings = new Button(typeGroup, SWT.CHECK);
		checkBoxSuppressWarnings.setText("Suppress warnings");
		checkBoxSuppressWarnings.setSelection(project.hasSuppressWarnings());

	}

	void createInterperterGroupCheckGroup(Composite controlGroup)
	{
		interperterGroup = new Group(controlGroup, SWT.NONE);
		interperterGroup.setText("Interpreting");
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		interperterGroup.setLayout(layout);

		checkBoxDynamicTypeChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxDynamicTypeChecks.setText("Dynamic type checks");
		checkBoxDynamicTypeChecks.setSelection(project.hasDynamictypechecks());

		checkBoxInvChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxInvChecks.setText("Invariants checks");
		checkBoxInvChecks.setSelection(project.hasInvchecks());

		checkBoxUsePreChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxUsePreChecks.setText("Pre condition checks");
		checkBoxUsePreChecks.setSelection(project.hasPrechecks());

		checkBoxUsePostChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxUsePostChecks.setText("Post condition checks");
		checkBoxUsePostChecks.setSelection(project.hasPostchecks());

	}

	@SuppressWarnings({ "deprecation" })
	public static IProject getSelectedProject()
	{
		ISelection selectedItem = WorkbenchPlugin.getDefault()
				.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.getSelection();
		IProject selectedProject = null;

		if (selectedItem instanceof ITreeSelection)
		{
			ITreeSelection selection = (ITreeSelection) selectedItem;
			if (selection.getPaths().length > 0)
			{
				Object project = selection.getPaths()[0].getFirstSegment();
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

			project.setDynamictypechecks(checkBoxDynamicTypeChecks.getSelection());
			project.setInvchecks(checkBoxInvChecks.getSelection());
			project.setPostchecks(checkBoxUsePostChecks.getSelection());
			project.setPrechecks(checkBoxUsePreChecks.getSelection());
			project.setSuppressWarnings(checkBoxSuppressWarnings.getSelection());

			project.typeCheck();
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.performOk();
	}
}
