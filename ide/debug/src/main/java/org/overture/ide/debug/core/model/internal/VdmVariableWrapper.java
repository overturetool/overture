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
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.overture.ide.debug.core.model.AtomicVdmType;
import org.overture.ide.debug.core.model.IVdmStackFrame;
import org.overture.ide.debug.core.model.IVdmType;
import org.overture.ide.debug.core.model.IVdmValue;
import org.overture.ide.debug.core.model.IVdmVariable;

public class VdmVariableWrapper extends VdmDebugElement implements IVdmVariable
{

	final IDebugTarget target;
	private final String name;
	private IVariable[] children;

	private IVdmValue value = null;

	public VdmVariableWrapper(IDebugTarget target, String name,
			IVariable[] children)
	{
		this.target = target;
		this.name = name;
		this.children = children;
	}

	public IVariable[] getChildren() throws DebugException
	{
		if (children == null)
		{
			return new IVdmVariable[0];
		}
		return (IVdmVariable[]) children.clone();
	}

	public String getEvalName()
	{
		return name;
	}

	public String getId()
	{
		return null;
	}

	public String getValueString()
	{
		return ""; //$NON-NLS-1$
	}

	public boolean hasChildren()
	{
		if (children == null)
		{
			return false;
		}
		return children.length > 0;
	}

	public boolean isConstant()
	{
		return false;
	}

	public String getName() throws DebugException
	{
		return name;
	}

	public String getReferenceTypeName() throws DebugException
	{
		return "getReferenceTypeName"; //$NON-NLS-1$
	}

	public boolean hasValueChanged() throws DebugException
	{
		return false;
	}

	public void setValue(String expression) throws DebugException
	{

	}

	public void setValue(IValue value) throws DebugException
	{

	}

	public boolean supportsValueModification()
	{
		return false;
	}

	public boolean verifyValue(String expression) throws DebugException
	{
		return false;
	}

	public boolean verifyValue(IValue value) throws DebugException
	{
		return false;
	}

	public boolean shouldHasChildren()
	{
		return false;
	}

	public IVdmType getType()
	{
		return new AtomicVdmType("getType"); //$NON-NLS-1$
	}

	public IVdmStackFrame getStackFrame()
	{
		return null;
	}

	public IValue getValue() throws DebugException
	{
		if (value == null)
		{
			value = new VdmVariableWrapperValue(this);
		}
		return value;
	}

	public IDebugTarget getDebugTarget()
	{
		return target;
	}

	/**
	 * @param classes
	 */
	public void refreshValue(IVariable[] newChildren)
	{
		this.children = newChildren;
	}
}
