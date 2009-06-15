/**
 * 
 */
package org.overturetool.eclipse.plugins.editor.internal.ui.wizards;

import java.util.Observable;

import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.actions.ScrubLocalAction;
import org.overturetool.eclipse.plugins.editor.core.OvertureConstants;
import org.overturetool.eclipse.plugins.launching.internal.launching.IOvertureInstallType;


/**
 * @author David
 * 
 */
public class OvertureProjectWizardFirstPage extends ProjectWizardFirstPage {

	private WidgetListener fListener = new WidgetListener();
	private Combo comboDialect;
	private String dialectSetting;
	
	public String getDialectSetting() {		
		return dialectSetting;
	}

	class WidgetListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			dialectSetting = comboDialect.getText();
		}
	}
		

	@Override
	protected boolean interpeterRequired() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	protected void createCustomGroups(Composite composite) {
		// TODO Auto-generated method stub
		super.createCustomGroups(composite);
	
		Group group = SWTFactory.createGroup(composite, "Dialect", 2, 2, GridData.FILL_HORIZONTAL);
	
		Label label = new Label(group, SWT.NONE);
		label.setText("Select dialect");
		comboDialect = new Combo(group, SWT.READ_ONLY);
		if(this.getInterpreter() != null && this.getInterpreter() instanceof IOvertureInstallType){
			comboDialect.setItems(((IOvertureInstallType)this.getInterpreter()).getSupportedDialectStrings());
		}else{
			comboDialect.setItems(new String[]{"No supported dialects"});
		}
		comboDialect.addModifyListener(fListener);
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
		return null;
	}

}