/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.ui.launching;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.util.modules.CombinedDefaultModule;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.utils.JarClassSelector;
import org.overture.ide.ui.internal.viewsupport.DecorationgVdmLabelProvider;
import org.overture.ide.ui.internal.viewsupport.VdmUILabelProvider;
import org.overture.ide.ui.outline.DisplayNameCreator;
import org.overture.ide.ui.outline.ExecutableFilter;
import org.overture.ide.ui.outline.VdmOutlineTreeContentProvider;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.messages.Console;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ParserException;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

/**
 * Main launch configuration tab for overture scripts
 */
public abstract class AbstractVdmMainLaunchConfigurationTab extends
		AbstractLaunchConfigurationTab
{
	private static final String MODULE_POST_FIX = "()";
	public final ITypeCheckerAssistantFactory assistantFactory = new TypeCheckerAssistantFactory();

	private class ModuleNameFinder extends DepthFirstAnalysisAdaptor {

		private String moduleName;
		
		@Override
		public void caseAVariableExp(AVariableExp node) throws AnalysisException {
			
			// The expression reader assumes VDM++ when the "Class" text field
			// is parsed. The expression specified in the "Class" text field is
			// either a constructor call (VDMPP/VDM-RT), e.g. A() or A(5) or
			// referring to a module, e.g. DEFAULT
			
			moduleName = node.getOriginal();
		}
		
		public String getModuleName()
		{
			return moduleName;
		}
	}
	
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
			if (elems.length > 0
					&& elems[0] instanceof AModuleModules
					&& ((AModuleModules) elems[0]).getName().getName().equals("DEFAULT"))
			{
				Set<AModuleModules> set = new HashSet<AModuleModules>();

				for (Object aModuleModules : elems)
				{

					set.add((AModuleModules) aModuleModules);
				}

				CombinedDefaultModule comb = new CombinedDefaultModule(set);

				return new Object[] { comb };

			}
			return elems;
		}
	}

	class WidgetListener implements ModifyListener, SelectionListener
	{
		public void modifyText(ModifyEvent e)
		{
			updateLaunchConfigurationDialog();
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
			/* do nothing */
		}

		public void widgetSelected(SelectionEvent e)
		{
			updateLaunchConfigurationDialog();
		}
	}

	protected final static String STATIC_CALL_SEPERATOR = "`";
	protected final static String CALL_SEPERATOR = ".";

	protected Text fProjectText;
	private Button fOperationButton;
	private Text fModuleNameText;
	private Text fOperationText;
	private Text fRemoteControlClassText;
	private Button fRemoteControlnButton;
	protected Button checkBoxGenerateLatexCoverage = null;

	private Button radioLaunchModeConsole = null;
	private Button radioLaunchModeEntryPoint = null;
	private Button radioLaunchModeRemoteControl = null;
	protected String defaultModule = "";
	protected String expression = "";
	private boolean staticOperation = false;
	private WidgetListener fListener = new WidgetListener();

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
		setErrorMessage(null);
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
					String moduleText = fModuleNameText.getText().trim();
					
					defaultModule = findModuleName();
					expression = getExpression(moduleText, fOperationText.getText().trim(), staticOperation);
					return validateTypes(project, expression);
				}
			}

			return true;
		}
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
		if (classname == null || classname.endsWith("."))
		{
			return false;
		}
		String[] parts = classname.split("[\\.]");
		if (parts.length == 0)
		{
			return false;
		}
		for (String part : parts)
		{
			CharacterIterator iter = new StringCharacterIterator(part);
			// Check first character (there should at least be one character for each part) ...
			char c = iter.first();
			if (c == CharacterIterator.DONE)
			{
				return false;
			}
			if (!Character.isJavaIdentifierStart(c)
					&& !Character.isIdentifierIgnorable(c))
			{
				return false;
			}
			c = iter.next();
			// Check the remaining characters, if there are any ...
			while (c != CharacterIterator.DONE)
			{
				if (!Character.isJavaIdentifierPart(c)
						&& !Character.isIdentifierIgnorable(c))
				{
					return false;
				}
				c = iter.next();
			}
		}
		return true;
	}

	protected abstract boolean validateTypes(IVdmProject project,
			String expression);

	private boolean validateOperation()
	{
		if (fOperationText == null || fOperationText.getText().length() == 0)
		{
			setErrorMessage("No operation specified");
			return false;
		}
		LexTokenReader ltr;
		ltr = new LexTokenReader(fOperationText.getText(), Dialect.VDM_RT, Console.charset);

		ExpressionReader reader = new ExpressionReader(ltr);
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
		if (fModuleNameText == null || fModuleNameText.getText().length() == 0)
		{
			setErrorMessage("No " + getModuleLabelName() + " specified");
			return false;
		}
		ExpressionReader reader = consExpressionReader();
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
		
		return true;
	}
	
	public String findModuleName()
	{
		ExpressionReader rdr = consExpressionReader();
		try {
			PExp exp = rdr.readExpression();
			
			ModuleNameFinder an = new ModuleNameFinder();
			
			exp.apply(an);
			
			return an.getModuleName();
			
		} catch (AnalysisException e) {
			
			e.printStackTrace();
		}
		
		return null;
	}

	private ExpressionReader consExpressionReader() {
		LexTokenReader ltr = new LexTokenReader(fModuleNameText.getText(), Dialect.VDM_PP, Console.charset);
		ExpressionReader reader = new ExpressionReader(ltr);
		return reader;
	}

	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
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

	protected void createProjectSelection(Composite parent)
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

					@Override
					public Object[] getElements(Object element)
					{
						List<IProject> elements = new Vector<IProject>();
						Object[] arr = super.getElements(element);
						if (arr != null)
						{
							for (Object object : arr)
							{
								try
								{
									if (object instanceof IProject
											&& ((IProject) object).getAdapter(IVdmProject.class) != null
											&& isSupported((IProject) object))
									{
										elements.add((IProject) object);
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

		fRemoteControlnButton = createPushButton(group, "Browse...", null);
		fRemoteControlnButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				try
				{
					chooseRemoteControlClass();
				} catch (CoreException e1)
				{
					if (VdmDebugPlugin.DEBUG)
					{
						e1.printStackTrace();
					}
				}
			}
		});
	}

	protected void chooseRemoteControlClass() throws CoreException
	{
		final IProject project = getProject();
		IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);

		String selection = JarClassSelector.selectClass(getShell(), vdmProject.getModelBuildPath().getLibrary());

		if (selection != null)
		{
			fRemoteControlClassText.setText(selection);
		}
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
			fRemoteControlnButton.setEnabled(false);
			fOperationText.setText("");
			fOperationText.setEnabled(false);
			fModuleNameText.setText("");
			fModuleNameText.setEnabled(false);
			fOperationButton.setEnabled(false);

		}
		if (radioLaunchModeEntryPoint.getSelection())
		{
			fRemoteControlClassText.setEnabled(false);
			fRemoteControlnButton.setEnabled(false);
			fOperationText.setEnabled(true);
			fModuleNameText.setEnabled(true);
			fOperationButton.setEnabled(true);
		}
		if (radioLaunchModeRemoteControl.getSelection())
		{
			fRemoteControlClassText.setEnabled(true);
			fRemoteControlnButton.setEnabled(true);
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

		dialog.setTitle(getModuleLabelName()
				+ " and function/operation selection");
		dialog.setMessage("Select a function or operation");
		
		dialog.setComparator(new ViewerComparator(new Comparator<String>() {

			@Override
			public int compare(String str1, String str2) {
		        int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
		        if (res == 0) {
		            res = str1.compareTo(str2);
		        }
		        return res;
			}
		}));

		dialog.addFilter(new ExecutableFilter());
		dialog.setAllowMultiple(false);
		dialog.setValidator(new ISelectionStatusValidator()
		{

			public IStatus validate(Object[] selection)
			{
				if (selection.length == 1
						&& (selection[0] instanceof SOperationDefinition
								&& ((SOperationDefinition) selection[0]).getBody() != null || selection[0] instanceof SFunctionDefinition
								&& ((SFunctionDefinition) selection[0]).getBody() != null))
				{
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
			if (dialog.open() == IDialogConstants.OK_ID)
			{
				if (dialog.getFirstResult() instanceof AModuleModules)
				{
					AModuleModules m = (AModuleModules) dialog.getFirstResult();
					defaultModule = m.getName().getName();
					fModuleNameText.setText(DisplayNameCreator.getDisplayName(m));
					return;
				}

				PDefinition method = (PDefinition) dialog.getFirstResult();
				INode module = null;

				if (method.getClassDefinition() != null)
				{
					if (!assistantFactory.createPAccessSpecifierAssistant().isStatic(method.getAccess()))
					{
						boolean foundConstructor = false;
						for (PDefinition def : method.getClassDefinition().getDefinitions())
						{
							if (def instanceof AExplicitOperationDefinition
									&& ((AExplicitOperationDefinition) def).getIsConstructor())
							{
								foundConstructor = true;
								module = def;
								defaultModule = def.getName().getName();
								fModuleNameText.setText(DisplayNameCreator.getDisplayName(def));
							}
						}
						if (!foundConstructor)
						{
							module = method.getClassDefinition();
							defaultModule = method.getClassDefinition().getName().getName();
							fModuleNameText.setText(DisplayNameCreator.getDisplayName(method.getClassDefinition())
									+ MODULE_POST_FIX);
						}
					} else
					{
						module = method.getClassDefinition();
						defaultModule = method.getClassDefinition().getName().getName();
						fModuleNameText.setText(DisplayNameCreator.getDisplayName(method.getClassDefinition()));
					}
				} else if (method.getLocation() != null
						&& method.getLocation().getModule() != null)
				{
					defaultModule = method.getLocation().getModule();
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

	private boolean isStaticCall(INode module, INode operation)
	{
		boolean staticAccess = true;
		if (module != null && !(module instanceof AModuleModules))
		{
			if (operation instanceof SOperationDefinition
					&& !assistantFactory.createPAccessSpecifierAssistant().isStatic(((SOperationDefinition) operation).getAccess()))
			{
				staticAccess = false;
			} else if (operation instanceof SFunctionDefinition
					&& !assistantFactory.createPAccessSpecifierAssistant().isStatic(((SFunctionDefinition) operation).getAccess()))
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

		if (!fProjectText.getText().equals(""))
		{
			IResource[] resources = new IResource[] { (IResource) ResourcesPlugin.getWorkspace().getRoot().getProject(fProjectText.getText()) };

			configuration.setMappedResources(resources);
		} else
		{
			configuration.setMappedResources(null);
		}

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
	protected String autoFillBaseSettings()
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

					String launchConfigName = DebugPlugin.getDefault().getLaunchManager().generateLaunchConfigurationName(name);
					return launchConfigName;
				}
			}
		}
		return null;
	}

}
