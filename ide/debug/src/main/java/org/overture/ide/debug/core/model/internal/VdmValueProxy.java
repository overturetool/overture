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
import org.overture.ide.debug.core.model.IVdmType;
import org.overture.ide.debug.core.model.IVdmValue;

public class VdmValueProxy implements IVdmValue
{

	private final IVdmValue origin;

	public VdmValueProxy(IVdmValue origin)
	{
		this.origin = origin;
	}

	// public IVdmEvaluationCommand createEvaluationCommand(
	// String messageTemplate, IVdmThread thread)
	// {
	// return origin.createEvaluationCommand(messageTemplate, thread);
	// }

	public String getDetailsString()
	{
		return origin.getDetailsString();
	}

	public String getEvalName()
	{
		return origin.getEvalName();
	}

	public String getInstanceId()
	{
		return origin.getInstanceId();
	}

	public String getMemoryAddress()
	{
		return origin.getMemoryAddress();
	}

	public String getRawValue()
	{
		return origin.getRawValue();
	}

	public IVdmType getType()
	{
		return origin.getType();
	}

	public IVariable getVariable(int offset) throws DebugException
	{
		return origin.getVariable(offset);
	}

	public String getReferenceTypeName() throws DebugException
	{
		return origin.getReferenceTypeName();
	}

	public String getValueString() throws DebugException
	{
		return origin.getValueString();
	}

	public IVariable[] getVariables() throws DebugException
	{
		return origin.getVariables();
	}

	public boolean hasVariables() throws DebugException
	{
		return origin.hasVariables();
	}

	public boolean isAllocated() throws DebugException
	{
		return origin.isAllocated();
	}

	public IDebugTarget getDebugTarget()
	{
		return origin.getDebugTarget();
	}

	public ILaunch getLaunch()
	{
		return origin.getLaunch();
	}

	public String getModelIdentifier()
	{
		return origin.getModelIdentifier();
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		return origin.getAdapter(adapter);
	}

}
