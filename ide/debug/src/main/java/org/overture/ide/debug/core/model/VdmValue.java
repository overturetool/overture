package org.overture.ide.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class VdmValue extends VdmDebugElement implements IValue
{

	private String referenceTypeName;
	private String value;

	public VdmValue(VdmDebugTarget target, String referenceTypeName, String value) {
		super(target);
		this.referenceTypeName = referenceTypeName;
		this.value = value;
	}

	public void setDebugTarget(VdmDebugTarget target)
	{
		super.fTarget = target;
	}

	public String getReferenceTypeName() throws DebugException
	{
		return referenceTypeName;
	}

	public String getValueString() throws DebugException
	{
		return value;
	}

	public IVariable[] getVariables() throws DebugException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasVariables() throws DebugException
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAllocated() throws DebugException
	{
		// TODO Auto-generated method stub
		return false;
	}

}
