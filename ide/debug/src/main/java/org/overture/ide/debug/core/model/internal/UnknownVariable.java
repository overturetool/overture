/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.overture.ide.debug.core.model.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.model.IVdmStackFrame;

public class UnknownVariable extends VdmDebugElement implements IVariable,
		IValue {

	private final IVdmStackFrame frame;
	private final VdmValue owner;
	private final int index;

	public UnknownVariable(IVdmStackFrame frame, VdmValue owner, int index) {
		this.frame = frame;
		this.owner = owner;
		this.index = index;
	}

	public String getName() throws DebugException {
		return "(" + index + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getReferenceTypeName() throws DebugException {
		return owner.getType().getName();
	}

	public IValue getValue() throws DebugException {
		return this;
	}

	public boolean hasValueChanged() throws DebugException {
		return false;
	}

	public IDebugTarget getDebugTarget() {
		return frame.getDebugTarget();
	}

	public void setValue(String expression) throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR,
				VdmDebugPlugin.PLUGIN_ID, DebugException.NOT_SUPPORTED,
				"setValue", null)); //$NON-NLS-1$
	}

	public void setValue(IValue value) throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR,
				VdmDebugPlugin.PLUGIN_ID, DebugException.NOT_SUPPORTED,
				"setValue", null)); //$NON-NLS-1$
	}

	public boolean supportsValueModification() {
		return false;
	}

	public boolean verifyValue(String expression) throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR,
				VdmDebugPlugin.PLUGIN_ID, DebugException.NOT_SUPPORTED,
				"verifyValue", null)); //$NON-NLS-1$
	}

	public boolean verifyValue(IValue value) throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR,
				VdmDebugPlugin.PLUGIN_ID, DebugException.NOT_SUPPORTED,
				"verifyValue", null)); //$NON-NLS-1$
	}

	public String getValueString() {
		return ""; //$NON-NLS-1$
	}

	public IVariable[] getVariables() throws DebugException {
		return VdmValue.NO_VARIABLES;
	}

	public boolean hasVariables() throws DebugException {
		return false;
	}

	public boolean isAllocated() throws DebugException {
		return false;
	}

	public String toString() {
		return getValueString();
	}

}
