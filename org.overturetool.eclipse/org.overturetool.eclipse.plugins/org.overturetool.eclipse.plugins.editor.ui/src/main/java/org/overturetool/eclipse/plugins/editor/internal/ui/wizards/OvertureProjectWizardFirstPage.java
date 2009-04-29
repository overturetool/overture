/**
 * 
 */
package org.overturetool.eclipse.plugins.editor.internal.ui.wizards;

import java.util.Observable;

import org.eclipse.dltk.launching.IInterpreterInstall;
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
import org.overturetool.eclipse.plugins.editor.ui.OverturePreferenceConstants;

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
			// TODO Auto-generated method stub
			dialectSetting = comboDialect.getText();
		}
	}
	
	
	@Override
	protected IInterpreterGroup createInterpreterGroup(Composite parent) {
		return null;
	}

	@Override
	protected IInterpreterInstall getInterpreter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Observable getInterpreterGroupObservable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void handlePossibleInterpreterChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean interpeterRequired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean supportInterpreter() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected void createCustomGroups(Composite composite) {
		// TODO Auto-generated method stub
		super.createCustomGroups(composite);
		
		Group group = SWTFactory.createGroup(composite, "Dialect", 2, 2, GridData.FILL_HORIZONTAL);
	
		Label label = new Label(group, SWT.NONE);
		label.setText("Select dialect");
		comboDialect = new Combo(group, SWT.READ_ONLY);
		comboDialect.setItems(new String[] {OverturePreferenceConstants.OVERTURE_OVERTURE_MODELLING_LANGUAGE,
											OverturePreferenceConstants.OVERTURE_VDM_PLUS_PLUS,
											OverturePreferenceConstants.OVERTURE_VDM_PLUS_PLUS_REALTIME,
											OverturePreferenceConstants.OVERTURE_VDM_SPECIFICATION_LANGUAGE});
		comboDialect.select(0);
		comboDialect.addModifyListener(fListener);
	}
	
	@Override 
	protected IDialogSettings getDialogSettings() {
		// TODO Auto-generated method stub
		IDialogSettings temp = super.getDialogSettings();	
		return temp;
	}

}