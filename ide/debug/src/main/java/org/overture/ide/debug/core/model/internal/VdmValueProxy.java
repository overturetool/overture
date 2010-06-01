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

public class VdmValueProxy implements IVdmValue {

	private final IVdmValue origin;

	public VdmValueProxy(IVdmValue origin) {
		this.origin = origin;
	}

	public IVdmEvaluationCommand createEvaluationCommand(
			String messageTemplate, IVdmThread thread) {
		return origin.createEvaluationCommand(messageTemplate, thread);
	}

	public String getDetailsString() {
		return origin.getDetailsString();
	}

	public String getEvalName() {
		return origin.getEvalName();
	}

	public String getInstanceId() {
		return origin.getInstanceId();
	}

	public String getMemoryAddress() {
		return origin.getMemoryAddress();
	}

	public String getRawValue() {
		return origin.getRawValue();
	}

	public IVdmType getType() {
		return origin.getType();
	}

	public IVariable getVariable(int offset) throws DebugException {
		return origin.getVariable(offset);
	}

	public String getReferenceTypeName() throws DebugException {
		return origin.getReferenceTypeName();
	}

	public String getValueString() throws DebugException {
		return origin.getValueString();
	}

	public IVariable[] getVariables() throws DebugException {
		return origin.getVariables();
	}

	public boolean hasVariables() throws DebugException {
		return origin.hasVariables();
	}

	public boolean isAllocated() throws DebugException {
		return origin.isAllocated();
	}

	public IDebugTarget getDebugTarget() {
		return origin.getDebugTarget();
	}

	public ILaunch getLaunch() {
		return origin.getLaunch();
	}

	public String getModelIdentifier() {
		return origin.getModelIdentifier();
	}

	public Object getAdapter(Class adapter) {
		return origin.getAdapter(adapter);
	}

}
