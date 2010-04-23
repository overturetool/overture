package org.overture.ide.debug.ui.launching;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
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
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.VdmProject;
import org.overture.ide.debug.core.Activator;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.ui.internal.viewsupport.DecorationgVdmLabelProvider;
import org.overture.ide.ui.internal.viewsupport.VdmUILabelProvider;
import org.overture.ide.ui.outline.DisplayNameCreator;
import org.overture.ide.ui.outline.ExecutableFilter;
import org.overture.ide.ui.outline.VdmOutlineTreeContentProvider;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;
import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.syntax.ExpressionReader;
import org.overturetool.vdmj.syntax.ParserException;

/**
 * Main launch configuration tab for overture scripts
 */
public abstract class AbstractVdmMainLaunchConfigurationTab extends
		AbstractLaunchConfigurationTab
{
	/**
	 * Custom content provider for the operation selection. Overloads the default one to merge DEFAULT modules into one
	 * module
	 * 
	 * @author kela
	 */
	private class MergedModuleVdmOutlineTreeContentProvider extends
			VdmOutlineTreeContentProvider
	{
		@Override
		public Object[] getElements(Object inputElement)
		{
			Object[] elems = super.getElements(inputElement);
			if (elems.length > 0 && elems[0] instanceof Module
					&& ((Module) elems[0]).name.name.equals("DEFAULT"))
			{
				DefinitionList definitions = new DefinitionList();

				for (Object m : elems)
				{
					definitions.addAll(((Module) m).defs);
				}

				Module module = new Module(new File("mergedFile"), definitions);
				return new Object[] { module };

			}
			return elems;
		}
	}

	class WidgetListener implements ModifyListener, SelectionListener
	{
		public void modifyText(ModifyEvent e)
		{
			// validatePage();
			updateLaunchConfigurationDialog();
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
			/* do nothing */
		}

		public void widgetSelected(SelectionEvent e)
		{
			// fOperationText.setEnabled(!fdebugInConsole.getSelection());

			updateLaunchConfigurationDialog();
		}
	}

	protected final static String STATIC_CALL_SEPERATOR = "`";
	protected final static String CALL_SEPERATOR = ".";

	private Text fProjectText;
	// private Button enableLogging;
	private Button fOperationButton;
	private Text fModuleNameText;
	private Text fOperationText;
	private Text fRemoteControlClassText;
	private Button checkBoxGenerateLatexCoverage = null;
	private Button checkBoxRemoteDebug = null;
	private Button checkBoxEnableLogging = null;
	// private String expressionPathseperator = "";
	private String defaultModule = "";
	private String expression = "";
	private boolean staticOperation = false;
	private WidgetListener fListener = new WidgetListener();

	// private String moduleDefinitionPath;

	protected IProject getProject()
	{
		if (fProjectText != null && fProjectText.getText().length() > 0)
		{
			return ResourcesPlugin.getWorkspace().getRoot().getProject(fProjectText.getText());
		} else
		{
			setErrorMessage("Project not set");
			return null;
		}

	}

	@Override
	public boolean isValid(ILaunchConfiguration config)
	{
		// return true;
		setErrorMessage(null);
		// if (fRemoteControlClassText.getText().length() > 0)
		// return true;// super.validate();
		if (super.isValid(config) && getProject() != null
				&& getProject().exists() && getProject().isOpen())
		{

			try
			{
				Console.charset = getProject().getDefaultCharset();
			} catch (CoreException e)
			{
				e.printStackTrace();
			}
			//
			boolean syntaxCorrect = validateClass() && validateOperation();
			if (!syntaxCorrect)
			{
				return syntaxCorrect;
			} else if (VdmProject.isVdmProject(getProject()))
			{
				expression = getExpression(fModuleNameText.getText(), fOperationText.getText(), staticOperation);
				return validateTypes(VdmProject.createProject(getProject()), expression);
			}
		}
		//
		//		
		return false;

	}

	// private void validatePage()
	// {
	// setErrorMessage(null);
	// validateClass();
	// validateOperation();
	// }

	protected abstract boolean validateTypes(IVdmProject project,
			String expression);

	private boolean validateOperation()
	{
		if ((fOperationText == null || fOperationText.getText().length() == 0))
		{
			setErrorMessage("No operation specified");
			return false;
		}
		LexTokenReader ltr;
		ltr = new LexTokenReader(fOperationText.getText(), Dialect.VDM_RT, Console.charset);

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
		ltr = new LexTokenReader(fModuleNameText.getText(), Dialect.VDM_PP, Console.charset);

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
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
		// IDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_COMMON_TAB);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());

		createProjectSelection(comp);
		createOperationEditor(comp);
		createRemoteControlEditor(comp);
		createOtherOptions(comp);
	}

	/*
	 * Default is class
	 */
	protected String getModuleLabelName()
	{
		return "Class";
	}

	private void createProjectSelection(Composite parent)
	{
		Group group = new Group(parent, parent.getStyle());
		group.setText("Project");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		group.setLayout(layout);

		// editParent = group;

		Label label = new Label(group, SWT.MIN);
		label.setText("Project:");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		fProjectText = new Text(group, SWT.SINGLE | SWT.BORDER);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		fProjectText.setLayoutData(gd);
		fProjectText.addModifyListener(fListener);

		Button selectProjectButton = createPushButton(group, "Browse...", null);

		selectProjectButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// ListSelectionDialog dlg = new ListSelectionDialog(getShell(),
				// ResourcesPlugin.getWorkspace().getRoot(), new BaseWorkbenchContentProvider(), new
				// WorkbenchLabelProvider(), "Select the Project:");
				// dlg.setTitle("Project Selection");
				// dlg.open();
				class ProjectContentProvider extends
						BaseWorkbenchContentProvider
				{
					@Override
					public boolean hasChildren(Object element)
					{
						if (element instanceof IProject)
						{
							return false;
						} else
						{
							return super.hasChildren(element);
						}
					}

					@SuppressWarnings("unchecked")
					@Override
					public Object[] getElements(Object element)
					{
						List elements = new Vector();
						Object[] arr = super.getElements(element);
						if (arr != null)
						{
							for (Object object : arr)
							{
								try
								{
									if (object instanceof IProject
											&& VdmProject.isVdmProject((IProject) object)
											&& isSupported((IProject) object))
									{
										elements.add(object);
									}
								} catch (CoreException e)
								{
									if (Activator.DEBUG)
									{
										e.printStackTrace();
									}
								}
							}
							return elements.toArray();
						}
						return null;
					}

				}
				;
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new ProjectContentProvider());
				dialog.setTitle("Project Selection");
				dialog.setMessage("Select a project:");
				dialog.setComparator(new ViewerComparator());

				dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());

				if (dialog.open() == Window.OK)
				{
					if (dialog.getFirstResult() != null
							&& dialog.getFirstResult() instanceof IProject
							&& VdmProject.isVdmProject((IProject) dialog.getFirstResult()))
					{
						fProjectText.setText(((IProject) dialog.getFirstResult()).getName());
					}

				}
			}
		});
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

		fOperationButton = createPushButton(group, "Search...", null);
		fOperationButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				try
				{
					chooseOperation();
				} catch (CoreException e1)
				{
					if (Activator.DEBUG)
					{
						e1.printStackTrace();
					}
				}
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
	}

	protected void createOtherOptions(Composite parent)
	{
		Group group = new Group(parent, parent.getStyle());
		group.setText("Other:");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		group.setLayout(layout);

		checkBoxGenerateLatexCoverage = new Button(group, SWT.CHECK);
		checkBoxGenerateLatexCoverage.setText("Generate coverage");
		checkBoxGenerateLatexCoverage.setSelection(false);
		checkBoxGenerateLatexCoverage.addSelectionListener(fListener);

		checkBoxRemoteDebug = new Button(group, SWT.CHECK);
		checkBoxRemoteDebug.setText("Remote debug");
		checkBoxRemoteDebug.setSelection(false);
		checkBoxRemoteDebug.addSelectionListener(fListener);

		checkBoxEnableLogging = new Button(group, SWT.CHECK);
		checkBoxEnableLogging.setText("Enable logging");
		checkBoxEnableLogging.setSelection(false);
		checkBoxEnableLogging.addSelectionListener(fListener);

	}

	/**
	 * chooses a project for the type of launch config that it is
	 * 
	 * @return
	 * @throws CoreException
	 */
	protected void chooseOperation() throws CoreException
	{
		final ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new DecorationgVdmLabelProvider(new VdmUILabelProvider()), new MergedModuleVdmOutlineTreeContentProvider());
		// ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new
		// WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());

		dialog.setTitle(getModuleLabelName()
				+ " and operation/function selection");
		dialog.setMessage("Select a function");

		dialog.addFilter(new ExecutableFilter());
		// dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		dialog.setAllowMultiple(false);
		dialog.setValidator(new ISelectionStatusValidator()
		{

			public IStatus validate(Object[] selection)
			{
				if (selection.length == 1
						&& (selection[0] instanceof ExplicitOperationDefinition || selection[0] instanceof ExplicitFunctionDefinition))
				{
					// new Status(IStatus.OK,IDebugConstants.PLUGIN_ID,IStatus.OK,"Selection: "+
					// ((IAstNode)selection[0]).getName(),null);
					return Status.OK_STATUS;
				}
				return new Status(IStatus.CANCEL, IDebugConstants.PLUGIN_ID, "Invalid selection");
			}
		});

		final IProject project = getProject();
		IVdmProject vdmProject = VdmProject.createProject(project);

		if (VdmTypeCheckerUi.typeCheck(getShell(), vdmProject))
		{
			dialog.setInput(vdmProject.getModel());
			dialog.addFilter(new ExecutableFilter());
			// dialog.setComparator(new ResourceComparator(ResourceComparator...NAME));
			if (dialog.open() == IDialogConstants.OK_ID)
			{
				Definition method = (Definition) dialog.getFirstResult();
				IAstNode module = null;

				if (method.classDefinition != null)
				{
					boolean foundConstructor = false;
					for (Definition def : method.classDefinition.definitions)
					{
						if (def instanceof ExplicitOperationDefinition
								&& ((ExplicitOperationDefinition) def).isConstructor)
						{
							foundConstructor = true;
							module = def;
							defaultModule = def.getName();
							fModuleNameText.setText(DisplayNameCreator.getDisplayName(def));
						}
					}
					if (!foundConstructor)
					{
						module = method.classDefinition;
						defaultModule = method.classDefinition.getName();
						fModuleNameText.setText(DisplayNameCreator.getDisplayName(method.classDefinition)
								+ "()");
					}
				} else if (method.location != null
						&& method.location.module != null)
				{
					defaultModule = method.location.module;
					fModuleNameText.setText(defaultModule);
				} else
				{
					defaultModule = "DEFAULT";
					fModuleNameText.setText(defaultModule);// undetermined module
				}

				// fOperationText.setText(method.name.name + "()");
				fOperationText.setText(DisplayNameCreator.getDisplayName(method));
				// expressionPathseperator = getCombinChar(module, method);
				// if (module != null && module instanceof ClassDefinition)
				// {
				// expression = "new " + fModuleNameText.getText()
				// + expressionPathseperator + fOperationText.getText();
				// } else
				// {
				// expression = fModuleNameText.getText()
				// + expressionPathseperator + fOperationText.getText();
				// }
				staticOperation = isStaticCall(module, method);
				expression = getExpression(fModuleNameText.getText(), fOperationText.getText(), staticOperation);

			}
		}
	}

	protected abstract String getExpression(String module, String operation,
			boolean isStatic);

	protected abstract boolean isSupported(IProject project)
			throws CoreException;

	public String getName()
	{
		return "Main";
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		// not supported

	}

	private boolean isStaticCall(IAstNode module, IAstNode operation)
	{
		boolean staticAccess = true;
		if (module != null && !(module instanceof Module))
		{
			if (operation instanceof ExplicitOperationDefinition
					&& !((ExplicitOperationDefinition) operation).isStatic())
			{
				staticAccess = false;
			} else if (operation instanceof ExplicitFunctionDefinition
					&& !((ExplicitFunctionDefinition) operation).isStatic())
			{
				staticAccess = false;
			}
		}

		return staticAccess;
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PROJECT, fProjectText.getText());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MODULE, fModuleNameText.getText());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_OPERATION, fOperationText.getText());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_STATIC_OPERATION, staticOperation);

		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_CONTROL, fRemoteControlClassText.getText());

		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_DEBUG, checkBoxRemoteDebug.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_CREATE_COVERAGE, checkBoxGenerateLatexCoverage.getSelection());

		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_LOGGING, checkBoxEnableLogging.getSelection());

		// configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_EXPRESSION_SEPERATOR,
		// expressionPathseperator);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DEFAULT, defaultModule);

		// System.out.println("Expression: " + expression);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_EXPRESSION, expression);

	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			fProjectText.setText(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PROJECT, ""));

			fModuleNameText.setText(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MODULE, ""));
			fOperationText.setText(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_OPERATION, ""));

			staticOperation = configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_STATIC_OPERATION, false);

			fRemoteControlClassText.setText(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_CONTROL, ""));

			checkBoxRemoteDebug.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_DEBUG, false));
			checkBoxGenerateLatexCoverage.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_CREATE_COVERAGE, false));

			checkBoxEnableLogging.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_LOGGING, false));

			defaultModule = configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DEFAULT, "");

			expression = configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_EXPRESSION, "");

			if (fProjectText.getText().length() == 0)
			{
				String newLaunchConfigName =autoFillBaseSettings();
				if(newLaunchConfigName!=null)
				{
				ILaunchConfigurationWorkingCopy wConfig =	configuration.getWorkingCopy();
				wConfig.rename(newLaunchConfigName);
				wConfig.doSave();//we do not need to handle to the new ILaunchConfiguration since no future access is needed
				}
			}
		} catch (CoreException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
		}

	}

	/**
	 * Gets the last selected project in the platform if selection is tree selection
	 */
	private String autoFillBaseSettings()
	{
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();// .getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if (selection instanceof TreeSelection)
		{
			TreeSelection tSelection = (TreeSelection) selection;
			if (tSelection.getFirstElement() != null
					&& tSelection.getFirstElement() instanceof IProject)
			{
				String name = ((IProject) tSelection.getFirstElement()).getName();
				if (name != null && name.trim().length() > 0)
				{
					fProjectText.setText(name);

					String launchConfigName = DebugPlugin.getDefault().getLaunchManager().generateUniqueLaunchConfigurationNameFrom(name);
					return launchConfigName;
				}
			}
		}
		return null;
	}

}