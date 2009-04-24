package org.overturetool.eclipse.plugins.editor.internal.ui.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

public class OverturePropertyPage extends PropertyPage {

	protected Control createContents(Composite parent) {
		return new Composite(parent, SWT.NONE);
	}

}
