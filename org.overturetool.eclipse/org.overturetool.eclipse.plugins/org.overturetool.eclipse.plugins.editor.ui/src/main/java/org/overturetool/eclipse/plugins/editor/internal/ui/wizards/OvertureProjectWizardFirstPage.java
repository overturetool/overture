/**
 * 
 */
package org.overturetool.eclipse.plugins.editor.internal.ui.wizards;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.ResourceBundle.Control;

import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.dltk.launching.ScriptRuntime.DefaultInterpreterEntry;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.actions.ScrubLocalAction;
import org.overturetool.eclipse.plugins.editor.core.OvertureConstants;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;
import org.overturetool.eclipse.plugins.launching.internal.launching.IOvertureInstallType;


/**
 * @author David
 * 
 */
public class OvertureProjectWizardFirstPage extends ProjectWizardFirstPage {

	private DialectComboWidgetListener fDialectComboListener = new DialectComboWidgetListener();
	private InterpreterWidgetListener fInterpreterComboListener = new InterpreterWidgetListener();
	private Combo comboDialect;
	private Combo comboInterpreter;
	private Button buttonUseDefaultInterpreter;
	private String dialectSetting;
	
	public String getDialectSetting() {		
		return dialectSetting;
	}
	
	class DialectComboWidgetListener implements ModifyListener{
		public void modifyText(ModifyEvent e) {
			dialectSetting = comboDialect.getText();
		}		
	}
	class InterpreterWidgetListener implements SelectionListener{

		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
			
		}

		public void widgetSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			if(e.widget instanceof Combo){			
				setDialects(((Combo)e.widget).getItem(((Combo)e.widget).getSelectionIndex()));
			}else if (e.widget instanceof org.eclipse.swt.widgets.Button ){				
				if(((org.eclipse.swt.widgets.Button)e.widget).equals(buttonUseDefaultInterpreter) && buttonUseDefaultInterpreter.isEnabled()){
					setDialectFromDefaultInterpreter();
				}else if(comboInterpreter != null){
					String comboSelection = comboInterpreter.getItem(comboInterpreter.getSelectionIndex());
					setDialects(comboSelection);
				}				
			}else if(e.widget instanceof Link && buttonUseDefaultInterpreter.getSelection()){
				setDialectFromDefaultInterpreter();
			}
		}
			
	}
	
	@Override
	protected boolean interpeterRequired() {
		// TODO Auto-generated method stub
		return true;
	}
	
	private void setDialectFromDefaultInterpreter(){
		IInterpreterInstallType[] interpreterTypes = ScriptRuntime.getInterpreterInstallTypes(OvertureNature.NATURE_ID);
		for (IInterpreterInstallType interpreterInstallType : interpreterTypes) {
			IInterpreterInstall[] interpreters = interpreterInstallType.getInterpreterInstalls();
			for (IInterpreterInstall interpreterInstall : interpreters) {
				if(buttonUseDefaultInterpreter.getText().contains("'"+interpreterInstall.getName()+"'")){									
					setDialects(interpreterInstall.getName());
					//comboDialect.setItems(((IOvertureInstallType)interpreterInstall.getInterpreterInstallType()).getSupportedDialectStrings());
					
				}
			}
		}
	}
	
	@Override
	protected void createCustomGroups(Composite composite) {
		super.createCustomGroups(composite);		
		Group group = SWTFactory.createGroup(composite, "Dialect", 2, 2, GridData.FILL_HORIZONTAL);
	
		Label label = new Label(group, SWT.NONE);
		label.setText("Select dialect");
		comboDialect = new Combo(group, SWT.READ_ONLY);

		org.eclipse.swt.widgets.Control[] controls = composite.getChildren();
		for (org.eclipse.swt.widgets.Control control : controls) {
			
			if(control instanceof Group && ((Group)control).getText().equals("Execution")){
				Group contentsGroup = (Group)control;
				org.eclipse.swt.widgets.Control[] groupControls = contentsGroup.getChildren();
				
				
				for (org.eclipse.swt.widgets.Control control2 : groupControls) {					
					
					if(control2 instanceof org.eclipse.swt.widgets.Button){
						org.eclipse.swt.widgets.Button button = (org.eclipse.swt.widgets.Button)control2;
						button.addSelectionListener(fInterpreterComboListener);
						
						if(button.getText().startsWith("Use def&ault interpreter") && button.isEnabled()){
							buttonUseDefaultInterpreter = button;							
							IInterpreterInstallType[] interpreterTypes = ScriptRuntime.getInterpreterInstallTypes(OvertureNature.NATURE_ID);
							for (IInterpreterInstallType interpreterInstallType : interpreterTypes) {
								IInterpreterInstall[] interpreters = interpreterInstallType.getInterpreterInstalls();
								for (IInterpreterInstall interpreterInstall : interpreters) {
									if(button.getText().contains("'"+interpreterInstall.getName()+"'")){									
										setDialects(interpreterInstall.getName());
										//comboDialect.setItems(((IOvertureInstallType)interpreterInstall.getInterpreterInstallType()).getSupportedDialectStrings());
										
									}
								}
							}
						}
					}else if(control2 instanceof Combo){						
						comboInterpreter = (Combo)control2;
						comboInterpreter.addSelectionListener(fInterpreterComboListener);
						String comboSelection = comboInterpreter.getItem(comboInterpreter.getSelectionIndex());
						setDialects(comboSelection);
						
					}else if(control2 instanceof Link){
						((Link)control2).addSelectionListener(fInterpreterComboListener);
						
					}
				}
				
			}
			
		}
		
					
		comboDialect.addModifyListener(fDialectComboListener);
		
		
		
	}
	
	@Override
	protected void handlePossibleInterpreterChange() {
		// TODO Auto-generated method stub
		super.handlePossibleInterpreterChange();
	}
	
	private void setDialects(String selectedInterpreter){
		boolean dialectsFound = false;
		IInterpreterInstallType[] interpreterTypes = ScriptRuntime.getInterpreterInstallTypes(OvertureNature.NATURE_ID);
		for (IInterpreterInstallType interpreterInstallType : interpreterTypes) {
			IInterpreterInstall[] interpreters = interpreterInstallType.getInterpreterInstalls();
			for (IInterpreterInstall interpreterInstall : interpreters) {
				if(interpreterInstall.getName().equals(selectedInterpreter)){									
					comboDialect.setItems(((IOvertureInstallType)interpreterInstall.getInterpreterInstallType()).getSupportedDialectStrings());
					dialectsFound = true;
					int erik = 60;
				}
			}
		}
		if(!dialectsFound){
			comboDialect.setItems(new String[]{"No supported dialects"});
		}
		comboDialect.select(0);	
	}
	
	@Override 
	protected IDialogSettings getDialogSettings() {
		// TODO Auto-generated method stub
		IDialogSettings temp = super.getDialogSettings();	
		return temp;
	}

	@Override
	protected IInterpreterGroup createInterpreterGroup(Composite parent) {
		// TODO Auto-generated method stub
		return null;
	}



}