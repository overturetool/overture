/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.ui.preferences;

import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class OvertureGlobalConfigurationBlock extends AbstractConfigurationBlock  {

	public OvertureGlobalConfigurationBlock(OverlayPreferenceStore store, PreferencePage mainPreferencePage) {
		super(store, mainPreferencePage);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.dltk.ui.preferences.IPreferenceConfigurationBlock#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public Control createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite= new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		
		return composite;		
	}
	


}
