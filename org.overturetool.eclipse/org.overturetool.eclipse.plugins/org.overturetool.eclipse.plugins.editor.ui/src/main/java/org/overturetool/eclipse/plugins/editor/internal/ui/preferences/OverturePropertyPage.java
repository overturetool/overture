package org.overturetool.eclipse.plugins.editor.internal.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.dltk.internal.core.builder.State;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PropertyPage;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;
import org.overturetool.eclipse.plugins.editor.internal.ui.UIPlugin;
import org.overturetool.eclipse.plugins.editor.internal.ui.preferences.OverturePropertyPage.InterpreterWidgetListener.Temp;
import org.overturetool.eclipse.plugins.editor.ui.OverturePreferenceConstants;
import org.overturetool.eclipse.plugins.launching.internal.launching.IOvertureInstallType;

public class OverturePropertyPage extends PropertyPage {

	private Group fGroup;
	private Combo comboInterpreter;
	private String[] fComplianceLabels;
	private Combo comboDialect;
	private InterpreterWidgetListener interpreterWidgetListener = new InterpreterWidgetListener();
	
	class InterpreterWidgetListener implements SelectionListener{

		class Temp extends InterpretersBlock{

			@Override
			protected String getCurrentNature() {
				// TODO Auto-generated method stub
				return null;
			}
			public Temp() {
				// TODO Auto-generated constructor stub
			}
			
		}
		
		
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
			
		}

		public void widgetSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			if(e.widget instanceof Combo){			
				setDialects(((Combo)e.widget).getItem(((Combo)e.widget).getSelectionIndex()));
			}
		}
			
	}
	
	protected Control createContents(Composite parent) {
		Temp
		temp.createControl(parent);
		
		fGroup = SWTFactory.createGroup(parent, "Interpreter and Dialect", 2, 2, GridData.FILL_HORIZONTAL);
		Label label = new Label(fGroup, SWT.NONE);
		label.setText("Select Interpreter");
		comboInterpreter = new Combo(fGroup, SWT.READ_ONLY);
		comboInterpreter.addSelectionListener(interpreterWidgetListener);
		fillInstalledInterpreters(comboInterpreter);
		Label label2 = new Label(fGroup, SWT.NONE);
		label2.setText("Select dialect");
		comboDialect = new Combo(fGroup, SWT.READ_ONLY);	
		
		IResource res = (IResource) getElement().getAdapter(IResource.class);
		IProject project = res.getProject();
		
		
		String dialect = "";
		String interpreterName = "";
		
		try {
			QualifiedName qn = new QualifiedName(UIPlugin.PLUGIN_ID,OverturePreferenceConstants.OVERTURE_DIALECT_KEY);
			dialect = project.getPersistentProperty(qn);
			qn = new QualifiedName(UIPlugin.PLUGIN_ID,OverturePreferenceConstants.OVERTURE_INTERPETER_KEY);
			interpreterName = project.getPersistentProperty(qn);
			setDialects(interpreterName);
			selectDialect(dialect);
			selectInterpreter(interpreterName);
			
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return fGroup;
		
	}
	
	private void selectInterpreter(String interpreterName){
		for(int i = 0; i < comboInterpreter.getItemCount(); i++){
			if(comboInterpreter.getItem(i).equals(interpreterName)){
				comboInterpreter.select(i);
			}
		}
	}
	
	private void selectDialect(String dialect){
		for(int i = 0; i < comboDialect.getItemCount(); i++){
			if(comboDialect.getItem(i).equals(dialect)){
				comboDialect.select(i);
			}
		}
	}
	
	private void setDialects(String selectedInterpreterName){
		IInterpreterInstall interpreter = getSelectedInterpreterInstall(selectedInterpreterName);
		if(interpreter != null){
		comboDialect.setItems(((IOvertureInstallType)interpreter.getInterpreterInstallType()).getSupportedDialectStrings());
		}else{
			comboDialect.setItems(new String[]{"No supported dialects"});
		}
		comboDialect.select(0);
	}
	
	private void fillInstalledInterpreters(Combo comboField) {
		String selectedItem = null;
		int selectionIndex = -1;
		if (true){//fUseProjectInterpreter.isSelected()) {
			
			selectionIndex = comboField.getSelectionIndex();
			if (selectionIndex != -1) {// paranoia
				selectedItem = comboField.getItems()[selectionIndex];
			}
		}

		IInterpreterInstall[] installedInterpreters = getWorkspaceInterpeters();

		selectionIndex = -1;// find new index
		fComplianceLabels = new String[installedInterpreters.length];
		for (int i = 0; i < installedInterpreters.length; i++) {
			fComplianceLabels[i] = installedInterpreters[i].getName();
			if (selectedItem != null
					&& fComplianceLabels[i].equals(selectedItem)) {
				selectionIndex = i;
			}
		}
		comboField.setItems(fComplianceLabels);			
	}
	private IInterpreterInstall[] getWorkspaceInterpeters() {
		List<IInterpreterInstall> standins = new ArrayList<IInterpreterInstall>();
		IInterpreterInstallType[] types = ScriptRuntime.getInterpreterInstallTypes(OvertureNature.NATURE_ID);
		for (IInterpreterInstallType interpreterInstallType : types) {
			IInterpreterInstall[] installs = interpreterInstallType.getInterpreterInstalls();
			for (IInterpreterInstall interpreterInstall : installs) {
				standins.add(interpreterInstall);
			}
			
		}
		return ((IInterpreterInstall[]) standins.toArray(new IInterpreterInstall[standins.size()]));
	}
	
	@Override
	public boolean performOk() {
		try {
			
			IResource res = (IResource) getElement().getAdapter(IResource.class);
			IProject project = res.getProject();
			
			QualifiedName qn = new QualifiedName(UIPlugin.PLUGIN_ID, OverturePreferenceConstants.OVERTURE_DIALECT_KEY);
			project.setPersistentProperty(qn, comboDialect.getItem(comboDialect.getSelectionIndex()));
			
			qn = new QualifiedName(UIPlugin.PLUGIN_ID, OverturePreferenceConstants.OVERTURE_INTERPETER_KEY);
			String interpreterName = comboInterpreter.getItem(comboInterpreter.getSelectionIndex());
			project.setPersistentProperty(qn, interpreterName);
			IInterpreterInstall projectInterpreter = getSelectedInterpreterInstall(interpreterName);
			final IEnvironment interpreterEnv = projectInterpreter.getEnvironment();
			EnvironmentManager.setEnvironmentId(project,interpreterEnv.getId(), false);	
		
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return super.performOk();
	}
	
	private IInterpreterInstall getSelectedInterpreterInstall(String selectedInterpreterName){
		IInterpreterInstall[] interpreters = getWorkspaceInterpeters();
		
		for (IInterpreterInstall interpreterInstall : interpreters) {
			if(interpreterInstall.getName().equals(selectedInterpreterName)){									
				return interpreterInstall;
									
			}
		}
		return null;
	}

}
