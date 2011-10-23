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

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;
import org.overture.ide.debug.core.model.IVdmThread;
import org.overture.ide.debug.core.model.IVdmType;
import org.overture.ide.debug.core.model.IVdmValue;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationCommand;

final class VdmVariableWrapperValue implements IVdmValue {

	private final VdmVariableWrapper owner;

	VdmVariableWrapperValue(VdmVariableWrapper VdmVariableWrapper) {
		this.owner = VdmVariableWrapper;
	}

	public String getReferenceTypeName() {
		return ""; //$NON-NLS-1$
	}

	public String getRawValue() {
		return ""; //$NON-NLS-1$
	}

	public String getValueString() {
		return ""; //$NON-NLS-1$
	}

	public IVariable[] getVariables() throws DebugException {
		return this.owner.getChildren();
	}

	public boolean hasVariables() {
		return this.owner.hasChildren();
	}

	public boolean isAllocated() {
		// TODO Auto-generated method stub
		return false;
	}

	public IDebugTarget getDebugTarget() {
		return owner.target;
	}

	public ILaunch getLaunch() {
		return getDebugTarget().getLaunch();
	}

	public String getModelIdentifier() {
		return getDebugTarget().getModelIdentifier();
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		return null;
	}

	public IVdmEvaluationCommand createEvaluationCommand(
			String messageTemplate, IVdmThread thread) {
		return null;
	}

	public String getEvalName() {
		return null;
	}

	public String getInstanceId() {
		return null;
	}

	public IVdmType getType() {
		return this.owner.getType();
	}

	public IVariable getVariable(int offset) {
		return null;
	}

	public String getMemoryAddress() {
		return null;
	}

	public String getDetailsString() {
		return getValueString();
	}
}
