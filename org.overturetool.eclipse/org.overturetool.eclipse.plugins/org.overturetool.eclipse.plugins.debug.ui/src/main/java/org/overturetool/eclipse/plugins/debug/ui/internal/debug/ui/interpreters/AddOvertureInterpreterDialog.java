/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.interpreters;


import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterLibraryBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.AddScriptInterpreterDialog;
import org.eclipse.dltk.internal.debug.ui.interpreters.IAddInterpreterDialogRequestor;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.swt.widgets.Shell;


public class AddOvertureInterpreterDialog extends AddScriptInterpreterDialog {
	public AddOvertureInterpreterDialog(IAddInterpreterDialogRequestor requestor, Shell shell, IInterpreterInstallType[] interpreterInstallTypes, IInterpreterInstall editedInterpreter) {
		super(requestor, shell, interpreterInstallTypes, editedInterpreter);
	}

	protected AbstractInterpreterLibraryBlock createLibraryBlock(AddScriptInterpreterDialog dialog) {
		return new OvertureInterpreterLibraryBlock(dialog);
	}

	protected boolean useInterpreterArgs() {
		return false;
	}
	
}
