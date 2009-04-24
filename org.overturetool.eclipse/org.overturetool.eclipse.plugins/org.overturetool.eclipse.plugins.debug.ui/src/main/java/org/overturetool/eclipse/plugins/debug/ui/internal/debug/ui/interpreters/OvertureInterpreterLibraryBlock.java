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
import org.eclipse.dltk.internal.debug.ui.interpreters.LibraryLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;

/**
 * Control used to edit the libraries associated with a Interpreter install
 */
public class OvertureInterpreterLibraryBlock extends
		AbstractInterpreterLibraryBlock {

	public OvertureInterpreterLibraryBlock(AddScriptInterpreterDialog d) {
		super(d);
	}

	protected IBaseLabelProvider getLabelProvider() {
		return new LibraryLabelProvider();
	}
}
