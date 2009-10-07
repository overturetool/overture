package org.overture.ide.debug.ui.preferences;

import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class OvertureInterpretersBlock extends AbstractConfigurationBlock {

	public OvertureInterpretersBlock(OverlayPreferenceStore store) {
		super(store);
	}

	public Control createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite= new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		
		return composite;		
	}

}
