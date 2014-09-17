/*
 * #%~
 * org.overture.ide.debug
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.debug.core.model.internal;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;
import org.overture.ide.debug.core.model.IVdmThread;
import org.overture.ide.debug.core.model.IVdmType;
import org.overture.ide.debug.core.model.IVdmValue;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationCommand;

final class VdmVariableWrapperValue implements IVdmValue
{

	private final VdmVariableWrapper owner;

	VdmVariableWrapperValue(VdmVariableWrapper VdmVariableWrapper)
	{
		this.owner = VdmVariableWrapper;
	}

	public String getReferenceTypeName()
	{
		return ""; //$NON-NLS-1$
	}

	public String getRawValue()
	{
		return ""; //$NON-NLS-1$
	}

	public String getValueString()
	{
		return ""; //$NON-NLS-1$
	}

	public IVariable[] getVariables() throws DebugException
	{
		return this.owner.getChildren();
	}

	public boolean hasVariables()
	{
		return this.owner.hasChildren();
	}

	public boolean isAllocated()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public IDebugTarget getDebugTarget()
	{
		return owner.target;
	}

	public ILaunch getLaunch()
	{
		return getDebugTarget().getLaunch();
	}

	public String getModelIdentifier()
	{
		return getDebugTarget().getModelIdentifier();
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		return null;
	}

	public IVdmEvaluationCommand createEvaluationCommand(
			String messageTemplate, IVdmThread thread)
	{
		return null;
	}

	public String getEvalName()
	{
		return null;
	}

	public String getInstanceId()
	{
		return null;
	}

	public IVdmType getType()
	{
		return this.owner.getType();
	}

	public IVariable getVariable(int offset)
	{
		return null;
	}

	public String getMemoryAddress()
	{
		return null;
	}

	public String getDetailsString()
	{
		return getValueString();
	}
}
