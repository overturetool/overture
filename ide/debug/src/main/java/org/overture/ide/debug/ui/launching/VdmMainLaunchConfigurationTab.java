package org.overture.ide.debug.ui.launching;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.IDebugHelpContextIds;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.overture.ide.core.IVdmProject;
import org.overture.ide.core.VdmProject;
import org.overture.ide.ui.outline.DisplayNameCreator;
import org.overture.ide.ui.outline.ExecutableFilter;
import org.overture.ide.ui.outline.VdmOutlineLabelProvider;
import org.overture.ide.ui.outline.VdmOutlineTreeContentProvider;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.syntax.ExpressionReader;
import org.overturetool.vdmj.syntax.ParserException;

/**
 * Main launch configuration tab for overture scripts
 */
public  class VdmMainLaunchConfigurationTab extends AbstractLaunchConfigurationTab
{

	

	// private Button enableLogging;
	private Button fOperationButton;
	private Text fModuleNameText;
	private Text fOperationText;
	private Text fRemoteControlClassText;
	private Button checkBoxGenerateLatexCoverage = null;
	private Button checkBoxRemoteDebug = null;
	private Button fdebugInConsole;
	private WidgetListener fListener = new WidgetListener();
	private String vmOption = "";

	public void setVmOptions(String option)
	{
		this.vmOption = option;
	}

	// private String moduleDefinitionPath;

	class WidgetListener implements ModifyListener, SelectionListener
	{
		public void modifyText(ModifyEvent e)
		{
//			validatePage();
			updateLaunchConfigurationDialog();
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
			/* do nothing */
		}

		public void widgetSelected(SelectionEvent e)
		{
			fOperationText.setEnabled(!fdebugInConsole.getSelection());
			updateLaunchConfigurationDialog();
		}
	}

	protected IProject getProject()
	{
		return null;
		
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration config)
	{
		return true;
		
//		if (fRemoteControlClassText.getText().length() > 0)
//			return true;// super.validate();
//		try
//		{
//			Console.charset = getProject().getDefaultCharset();
//		} catch (CoreException e)
//		{
//			e.printStackTrace();
//		}
//
//		boolean syntaxCorrect = validateClass() && validateOperation();
//		if (!syntaxCorrect)
//			return syntaxCorrect;
//		else
//			return super.isValid(config)
//					&& validateTypes(fModuleNameText.getText(),
//							fOperationText.getText());
//
//		
		
	}
	
	

	protected  boolean validateTypes(String module, String operation)
	{
		return true;//abstract
	}

	

	private boolean validateOperation()
	{
		if ((fOperationText == null || fOperationText.getText().length() == 0))
		{
			setErrorMessage("No operation specified");
			return false;
		}
		LexTokenReader ltr;
		ltr = new LexTokenReader(fOperationText.getText(),
				Dialect.VDM_RT,
				Console.charset);

		ExpressionReader reader = new ExpressionReader(ltr);
		// reader.setCurrentModule(module);
		try
		{
			reader.readExpression();
		} catch (ParserException e)
		{
			this.setErrorMessage("Operation: " + e.number + " "
					+ e.getMessage());
			return false;
		} catch (LexException e)
		{
			this.setErrorMessage("Operation: " + e.number + " "
					+ e.getMessage());
			return false;
		}

		return !(fOperationText == null || fOperationText.getText().length() == 0);

	}

	private boolean validateClass()
	{
		if ((fModuleNameText == null || fModuleNameText.getText().length() == 0))
		{
			setErrorMessage("No " + getModuleLabelName() + " specified");
			return false;
		}
		LexTokenReader ltr;
		ltr = new LexTokenReader(fModuleNameText.getText(),
				Dialect.VDM_PP,
				Console.charset);

		ExpressionReader reader = new ExpressionReader(ltr);
		// reader.setCurrentModule(module);
		try
		{
			reader.readExpression();
		} catch (ParserException e)
		{
			this.setErrorMessage(getModuleLabelName() + ": " + e.number + " "
					+ e.getMessage());
			return false;
		} catch (LexException e)
		{
			this.setErrorMessage(getModuleLabelName() + ": " + e.number + " "
					+ e.getMessage());
			return false;
		}
		return !(fModuleNameText == null || fModuleNameText.getText().length() == 0);
	}

	
	
	
	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		
		setControl(comp);
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_COMMON_TAB);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());
		
		createOperationEditor(comp);
		createRemoteControlEditor(comp);
	}

	/*
	 * Default is class
	 */
	protected String getModuleLabelName()
	{
		return "Class";
	}

	private void createOperationEditor(Composite parent)
	{
		Group group = new Group(parent, parent.getStyle());
		group.setText("Operation:");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		group.setLayout(layout);

		// editParent = group;

		Label label = new Label(group, SWT.MIN);
		label.setText(getModuleLabelName() + ":");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		fModuleNameText = new Text(group, SWT.SINGLE | SWT.BORDER);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		fModuleNameText.setLayoutData(gd);
		fModuleNameText.addModifyListener(fListener);

		fOperationButton = createPushButton(group,
				"project",
				null);
		fOperationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				handleOperationButtonSelected();
			}
		});

		label = new Label(group, SWT.NORMAL);
		label.setText("Operation:");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		label.setLayoutData(gd);

		fOperationText = new Text(group, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fOperationText.setLayoutData(gd);
		fOperationText.addModifyListener(fListener);

		setControl(parent);
	}

	private void createRemoteControlEditor(Composite parent)
	{
		Group group = new Group(parent, parent.getStyle());
		group.setText("Remote Control:");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		group.setLayout(layout);

		// editParent = group;

		Label label = new Label(group, SWT.MIN);
		label.setText("Fully qualified remote control class:");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		fRemoteControlClassText = new Text(group, SWT.SINGLE | SWT.BORDER);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		fRemoteControlClassText.setLayoutData(gd);
		fRemoteControlClassText.addModifyListener(fListener);
		fRemoteControlClassText.setEnabled(false);
		//
		// fOperationButton = createPushButton(group,
		// DLTKLaunchConfigurationsMessages.mainTab_projectButton,
		// null);
		// fOperationButton.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e)
		// {
		// handleOperationButtonSelected();
		// }
		// });
		//
		// label = new Label(group, SWT.NORMAL);
		// label.setText("Operation:");
		// gd = new GridData(GridData.FILL_HORIZONTAL);
		// label.setLayoutData(gd);
		//
		// fOperationText = new Text(group, SWT.SINGLE | SWT.BORDER);
		// gd = new GridData(GridData.FILL_HORIZONTAL);
		// fOperationText.setLayoutData(gd);
		// fOperationText.addModifyListener(fListener);

		setControl(parent);
	}

	
//
//	@Override
//	protected void createDebugOptions(Composite group)
//	{
//		super.createDebugOptions(group);
//		org.eclipse.swt.widgets.Control[] temp = group.getChildren();
//
//		temp[0].setVisible(false);// HACK TO REMOVE "BREAK OF FIRST LINE
//		temp[1].setVisible(false);// HACK TO REMOVE "BREAK OF FIRST LINE
//		// fdebugInConsole = SWTFactory.createCheckButton(group,
//		// OvertureDebugConstants.DEBUG_FROM_CONSOLE);
//		// fdebugInConsole.addSelectionListener(fListener);
//
//		checkBoxGenerateLatexCoverage = new Button(group, SWT.CHECK);
//		checkBoxGenerateLatexCoverage.setText("Generate Latex coverage");
//		checkBoxGenerateLatexCoverage.setSelection(false);
//		checkBoxGenerateLatexCoverage.addSelectionListener(getWidgetListener());
//
//		checkBoxRemoteDebug = new Button(group, SWT.CHECK);
//		checkBoxRemoteDebug.setText("Remote debug");
//		checkBoxRemoteDebug.setSelection(false);
//		checkBoxRemoteDebug.addSelectionListener(getWidgetListener());
//
//	}

	/**
	 * Show a dialog that lets the user select a project. This in turn provides context for the main type,
	 * allowing the user to key a main type name, or constraining the search for main types to the specified
	 * project.
	 */
	protected void handleOperationButtonSelected()
	{
		try
		{
			chooseOperation();
		} catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * chooses a project for the type of launch config that it is
	 * 
	 * @return
	 * @throws CoreException
	 */
	protected void chooseOperation() throws CoreException
	{
		final ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(),
				new VdmOutlineLabelProvider(),
				new VdmOutlineTreeContentProvider());
		dialog.setTitle(getModuleLabelName()
				+ " and operation/function selection");
		dialog.setMessage("searchButton_message");
		
		// dialog.addFilter(new ExecutableFilter());
		// dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		// dialog.setInput(new Object());
		final IProject project = getProject();
		IVdmProject vdmProject = VdmProject.createProject(project);
		final String natureId = vdmProject.getVdmNature();
		

		
		dialog.setInput(vdmProject.getModel());
		dialog.addFilter(new ExecutableFilter());
		//dialog.setComparator(new ResourceComparator(ResourceComparator...NAME));
		if (dialog.open() == IDialogConstants.OK_ID)
		{
			Definition method = (Definition) dialog.getFirstResult();
			if (method.classDefinition != null)
			{
				boolean foundConstructor = false;
				for (Definition def : method.classDefinition.definitions)
				{
					if (def instanceof ExplicitOperationDefinition
							&& ((ExplicitOperationDefinition) def).isConstructor)
					{
						foundConstructor = true;
						fModuleNameText.setText(DisplayNameCreator.getDisplayName(def));
					}
				}
				if (!foundConstructor)
					fModuleNameText.setText(DisplayNameCreator.getDisplayName(method.classDefinition)
							+ "()");
			} else if (method.location != null
					&& method.location.module != null)
				fModuleNameText.setText(method.location.module);
			else
				fModuleNameText.setText("DEFAULT");// undetermined module

			// fOperationText.setText(method.name.name + "()");
			fOperationText.setText(DisplayNameCreator.getDisplayName(method));

		}
	}

	

	

	
	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
//		// cons
//		config.setAttribute(DebugCoreConstants.DEBUGGING_MODULE,
//				fModuleNameText.getText());
//		config.setAttribute(DebugCoreConstants.DEBUGGING_OPERATION,
//				fOperationText.getText());
//		config.setAttribute(DebugCoreConstants.DEBUGGING_REMOTE_CONTROL,
//				fRemoteControlClassText.getText().trim());
//
//		if (vmOption != null && vmOption.trim().length() > 0)
//			config.setAttribute(DebugCoreConstants.DEBUGGING_VM_MEMORY_OPTION,
//					vmOption.trim());
//
//		// config.setAttribute(ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME,
//		// moduleDefinitionPath);
//		// config.setAttribute(ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME,
//		// "");
//		config.setAttribute(DLTKDebugPreferenceConstants.PREF_DBGP_BREAK_ON_FIRST_LINE,
//				false);
//		config.setAttribute(DLTKDebugPreferenceConstants.PREF_DBGP_ENABLE_LOGGING,
//				true);
//		config.setAttribute(DebugCoreConstants.DEBUGGING_CREATE_COVERAGE,
//				checkBoxGenerateLatexCoverage.getSelection());
//
//		config.setAttribute(DebugCoreConstants.DEBUGGING_REMOTE_DEBUG,
//				checkBoxRemoteDebug.getSelection());
//
//		config.setAttribute(DLTKDebugPreferenceConstants.PREF_DBGP_CONNECTION_TIMEOUT,
//				5000);
//		super.performApply(configuration);
	}

	

	public String getName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		// TODO Auto-generated method stub
		
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		// TODO Auto-generated method stub
		
	}

}