package org.overture.ide.debug.ui.launching;

import java.io.File;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
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
import org.overture.ide.debug.core.VdmDebugPlugin;
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

	private Button radioLaunchModeConsole = null;
	private Button radioLaunchModeEntryPoint = null;
	private Button radioLaunchModeRemoteControl = null;
	// private Button checkBoxRemoteDebug = null;
	// private Button checkBoxEnableLogging = null;
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
		if (super.isValid(config))
		{
			if (getProject() == null || !getProject().exists()
					|| !getProject().getName().equals(fProjectText.getText()))
			{
				setErrorMessage("Project does not exist");
				return false;
			}

			if (!getProject().isOpen())
			{
				setErrorMessage("Project is not open");
				return false;
			}

			try
			{
				Console.charset = getProject().getDefaultCharset();
			} catch (CoreException e)
			{
				e.printStackTrace();
			}
			//

			if (radioLaunchModeConsole.getSelection())
			{
				// no future checks needed
			}

			if (radioLaunchModeRemoteControl.getSelection())
			{
				if (!isFullyQualifiedClassname(fRemoteControlClassText.getText()))
				{
					setErrorMessage("Remote Control class name is not a well-formed fully-qualified Java classname");
					return false;
				}
			}

			if (radioLaunchModeEntryPoint.getSelection())
			{

				boolean syntaxCorrect = validateClass() && validateOperation();
				IVdmProject project = (IVdmProject) getProject().getAdapter(IVdmProject.class);
				if (!syntaxCorrect)
				{
					// error message set in validate class and operation
					return syntaxCorrect;
				} else if (project != null)
				{
					expression = getExpression(fModuleNameText.getText().trim(), fOperationText.getText().trim(), staticOperation);
					return validateTypes(project, expression);
				}
			}

			return true;
		}
		//
		//		
		return false;

	}

	/**
	 * Determine whether the supplied string represents a well-formed fully-qualified Java classname. This utility
	 * method enforces no conventions (e.g., packages are all lowercase) nor checks whether the class is available on
	 * the classpath.
	 * 
	 * @param classname
	 * @return true if the string is a fully-qualified class name
	 */
	public static boolean isFullyQualifiedClassname(String classname)
	{
		if (classname == null)
			return false;
		String[] parts = classname.split("[\\.]");
		if (parts.length == 0)
			return false;
		for (String part : parts)
		{
			CharacterIterator iter = new StringCharacterIterator(part);
			// Check first character (there should at least be one character for each part) ...
			char c = iter.first();
			if (c == CharacterIterator.DONE)
				return false;
			if (!Character.isJavaIdentifierStart(c)
					&& !Character.isIdentifierIgnorable(c))
				return false;
			c = iter.next();
			// Check the remaining characters, if there are any ...
			while (c != CharacterIterator.DONE)
			{
				if (!Character.isJavaIdentifierPart(c)
						&& !Character.isIdentifierIgnorable(c))
					return false;
				c = iter.next();
			}
		}
		return true;
	}

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
		createLaunchModelGroup(comp);
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
				// ResourcesPlugin.getWorkspace().getRoot(), new
				// BaseWorkbenchContentProvider(), new
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
											&& (((IProject) object).getAdapter(IVdmProject.class) != null)
											&& isSupported((IProject) object))
									{
										elements.add(object);
									}
								} catch (CoreException e)
								{
									if (VdmDebugPlugin.DEBUG)
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
							&& ((IProject) dialog.getFirstResult()).getAdapter(IVdmProject.class) != null)
					{
						fProjectText.setText(((IProject) dialog.getFirstResult()).getName());
					}

				}
			}
		});
	}

	private void createLaunchModelGroup(Composite parent)
	{
		Group group = new Group(parent, parent.getStyle());
		group.setText("Launch Mode:");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		group.setLayout(layout);

		SelectionListener launchModelSelectionListener = new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{
				updateLaunchModeEnablement();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				updateLaunchModeEnablement();
			}
		};

		radioLaunchModeEntryPoint = new Button(group, SWT.RADIO);
		radioLaunchModeEntryPoint.setText("Entry Point");
		radioLaunchModeEntryPoint.setSelection(false);
		radioLaunchModeEntryPoint.addSelectionListener(fListener);
		radioLaunchModeEntryPoint.addSelectionListener(launchModelSelectionListener);

		radioLaunchModeRemoteControl = new Button(group, SWT.RADIO);
		radioLaunchModeRemoteControl.setText("Remote Control");
		radioLaunchModeRemoteControl.setSelection(false);
		radioLaunchModeRemoteControl.addSelectionListener(fListener);
		radioLaunchModeRemoteControl.addSelectionListener(launchModelSelectionListener);

		radioLaunchModeConsole = new Button(group, SWT.RADIO);
		radioLaunchModeConsole.setText("Console");
		radioLaunchModeConsole.setSelection(false);
		radioLaunchModeConsole.addSelectionListener(fListener);
		radioLaunchModeConsole.addSelectionListener(launchModelSelectionListener);
	}

	private void createOperationEditor(Composite parent)
	{
		Group group = new Group(parent, parent.getStyle());
		group.setText("Entry Point:");
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
					if (VdmDebugPlugin.DEBUG)
					{
						e1.printStackTrace();
					}
				}
			}
		});

		label = new Label(group, SWT.NORMAL);
		label.setText("Function/Operation:");
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
		fRemoteControlClassText.setEnabled(true);
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

	}

	private void updateLaunchModeEnablement()
	{
		if (radioLaunchModeConsole.getSelection())
		{
			fRemoteControlClassText.setEnabled(false);
			fOperationText.setEnabled(false);
			fModuleNameText.setEnabled(false);
			fOperationButton.setEnabled(false);

		}
		if (radioLaunchModeEntryPoint.getSelection())
		{
			fRemoteControlClassText.setEnabled(false);
			fOperationText.setEnabled(true);
			fModuleNameText.setEnabled(true);
			fOperationButton.setEnabled(true);
		}
		if (radioLaunchModeRemoteControl.getSelection())
		{
			fRemoteControlClassText.setEnabled(true);
			fOperationText.setEnabled(false);
			fModuleNameText.setEnabled(false);
			fOperationButton.setEnabled(false);
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
		final ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new DecorationgVdmLabelProvider(new VdmUILabelProvider()), new MergedModuleVdmOutlineTreeContentProvider());
		// ElementTreeSelectionDialog dialog = new
		// ElementTreeSelectionDialog(getShell(), new
		// WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());

		dialog.setTitle(getModuleLabelName()
				+ " and function/operation selection");
		dialog.setMessage("Select a function or operation");

		dialog.addFilter(new ExecutableFilter());
		// dialog.setComparator(new
		// ResourceComparator(ResourceComparator.NAME));
		dialog.setAllowMultiple(false);
		dialog.setValidator(new ISelectionStatusValidator()
		{

			public IStatus validate(Object[] selection)
			{
				if (selection.length == 1
						&& (selection[0] instanceof ExplicitOperationDefinition || selection[0] instanceof ExplicitFunctionDefinition))
				{
					// new
					// Status(IStatus.OK,IDebugConstants.PLUGIN_ID,IStatus.OK,"Selection: "+
					// ((IAstNode)selection[0]).getName(),null);
					return Status.OK_STATUS;
				}
				return new Status(IStatus.CANCEL, IDebugConstants.PLUGIN_ID, "Invalid selection");
			}
		});

		final IProject project = getProject();
		IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);

		if (VdmTypeCheckerUi.typeCheck(getShell(), vdmProject))
		{
			dialog.setInput(vdmProject.getModel());
			dialog.addFilter(new ExecutableFilter());
			// dialog.setComparator(new
			// ResourceComparator(ResourceComparator...NAME));
			if (dialog.open() == IDialogConstants.OK_ID)
			{				
				if(dialog.getFirstResult() instanceof Module)
				{
					Module m = (Module) dialog.getFirstResult();
					defaultModule = m.getName();
					fModuleNameText.setText(DisplayNameCreator.getDisplayName(m));
					return;
				}
				
				Definition method = (Definition) dialog.getFirstResult();
				IAstNode module = null;

				if (method.classDefinition != null)
				{
					if (!method.isStatic())
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
					} else
					{
						module = method.classDefinition;
						defaultModule = method.classDefinition.getName();
						fModuleNameText.setText(DisplayNameCreator.getDisplayName(method.classDefinition));
					}
				} else if (method.location != null
						&& method.location.module != null)
				{
					defaultModule = method.location.module;
					fModuleNameText.setText(defaultModule);
				} else
				{
					defaultModule = "DEFAULT";
					fModuleNameText.setText(defaultModule);// undetermined
					// module
				}

				String opName = DisplayNameCreator.getDisplayName(method);

				staticOperation = isStaticCall(module, method);
				expression = getExpression(fModuleNameText.getText().trim(), opName.trim(), staticOperation);
				fOperationText.setText(opName);

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
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_CREATE_COVERAGE, true);
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

		if (radioLaunchModeRemoteControl.getSelection())
		{
			configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_CONTROL, fRemoteControlClassText.getText());
		} else
		{
			configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_CONTROL, "");
		}

		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_CREATE_COVERAGE, checkBoxGenerateLatexCoverage.getSelection());

		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_CONSOLE_ENTRY, radioLaunchModeConsole.getSelection());

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

			checkBoxGenerateLatexCoverage.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_CREATE_COVERAGE, false));

			radioLaunchModeConsole.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_CONSOLE_ENTRY, false));

			defaultModule = configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DEFAULT, "");

			expression = configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_EXPRESSION, "");

			if (fProjectText.getText().length() == 0)
			{
				String newLaunchConfigName = autoFillBaseSettings();
				if (newLaunchConfigName != null)
				{
					ILaunchConfigurationWorkingCopy wConfig = configuration.getWorkingCopy();
					wConfig.rename(newLaunchConfigName);
					wConfig.doSave();// we do not need to handle to the new
					// ILaunchConfiguration since no future
					// access is needed
				}
			}
			
			radioLaunchModeEntryPoint.setSelection(true);

			if (configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_OPERATION, "").length() > 0
					|| configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MODULE, "").length() > 0)
			{
				radioLaunchModeEntryPoint.setSelection(true);
				radioLaunchModeRemoteControl.setSelection(false);
				radioLaunchModeConsole.setSelection(false);
			}

			if (configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_CONTROL, "").length() > 0)
			{
				radioLaunchModeEntryPoint.setSelection(false);
				radioLaunchModeRemoteControl.setSelection(true);
				radioLaunchModeConsole.setSelection(false);
			}

			if (configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_CONSOLE_ENTRY, false))
			{
				radioLaunchModeEntryPoint.setSelection(false);
				radioLaunchModeRemoteControl.setSelection(false);
				radioLaunchModeConsole.setSelection(true);
			}
			updateLaunchModeEnablement();
		} catch (CoreException e)
		{
			if (VdmDebugPlugin.DEBUG)
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