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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.model.IVdmStackFrame;

public class UnknownVariable extends VdmDebugElement implements IVariable,
		IValue
{

	private final IVdmStackFrame frame;
	private final VdmValue owner;
	private final int index;

	public UnknownVariable(IVdmStackFrame frame, VdmValue owner, int index)
	{
		this.frame = frame;
		this.owner = owner;
		this.index = index;
	}

	public String getName() throws DebugException
	{
		return "(" + index + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getReferenceTypeName() throws DebugException
	{
		return owner.getType().getName();
	}

	public IValue getValue() throws DebugException
	{
		return this;
	}

	public boolean hasValueChanged() throws DebugException
	{
		return false;
	}

	public IDebugTarget getDebugTarget()
	{
		return frame.getDebugTarget();
	}

	public void setValue(String expression) throws DebugException
	{
		throw new DebugException(new Status(IStatus.ERROR, VdmDebugPlugin.PLUGIN_ID, DebugException.NOT_SUPPORTED, "setValue", null)); //$NON-NLS-1$
	}

	public void setValue(IValue value) throws DebugException
	{
		throw new DebugException(new Status(IStatus.ERROR, VdmDebugPlugin.PLUGIN_ID, DebugException.NOT_SUPPORTED, "setValue", null)); //$NON-NLS-1$
	}

	public boolean supportsValueModification()
	{
		return false;
	}

	public boolean verifyValue(String expression) throws DebugException
	{
		throw new DebugException(new Status(IStatus.ERROR, VdmDebugPlugin.PLUGIN_ID, DebugException.NOT_SUPPORTED, "verifyValue", null)); //$NON-NLS-1$
	}

	public boolean verifyValue(IValue value) throws DebugException
	{
		throw new DebugException(new Status(IStatus.ERROR, VdmDebugPlugin.PLUGIN_ID, DebugException.NOT_SUPPORTED, "verifyValue", null)); //$NON-NLS-1$
	}

	public String getValueString()
	{
		return ""; //$NON-NLS-1$
	}

	public IVariable[] getVariables() throws DebugException
	{
		return VdmValue.NO_VARIABLES;
	}

	public boolean hasVariables() throws DebugException
	{
		return false;
	}

	public boolean isAllocated() throws DebugException
	{
		return false;
	}

	public String toString()
	{
		return getValueString();
	}

}
