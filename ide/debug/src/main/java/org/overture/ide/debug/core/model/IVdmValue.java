/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationCommand;

public interface IVdmValue extends IValue {
	String getInstanceId();

	IVdmType getType();

	String getEvalName();

	String getRawValue();

	/**
	 * Returns the physical memory address or <code>null</code> if it is not
	 * available.
	 */
	String getMemoryAddress();

	/**
	 * Returns the text that will be displayed in the 'details' pane of the
	 * 'Variables' view.
	 */
	String getDetailsString();

	IVariable getVariable(int offset) throws DebugException;

	IVdmEvaluationCommand createEvaluationCommand(String messageTemplate,
			IVdmThread thread);
}
