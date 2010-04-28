package org.overture.ide.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;

public abstract class VdmValue extends VdmDebugElement implements IValue
{

	private String referenceTypeName;
	protected VdmStackFrame stackFrame;
	protected final String key;

	public VdmValue(String referenceTypeName, String key) {
		super(null);
		this.referenceTypeName = referenceTypeName;
		this.key = key;
	}

	public String getKey()
	{
		return key;
	}

	public void setDebugTarget(VdmDebugTarget target)
	{
		super.fTarget = target;
	}

	public String getReferenceTypeName() throws DebugException
	{
		return referenceTypeName;
	}

//	public String getValueString() throws DebugException
//	{
//		return value;
//	}
//
//	public IVariable[] getVariables() throws DebugException
//	{
//		VdmVariable[] variables =stackFrame.proxy.getVariables(stackFrame.level, "NAME", key);
//		for (VdmVariable iVariable : variables)
//		{
//			iVariable.setStackFrame(stackFrame);
//			iVariable.setDebugTarget(stackFrame.getDebugTarget());
//		}
//		return variables;
//	}
//
//	public boolean hasVariables() throws DebugException
//	{
//		return hasChildern;
//	}

	public boolean isAllocated() throws DebugException
	{
		return false;
	}

	public void setStackFrame(VdmStackFrame stackFrame)
	{
		this.stackFrame = stackFrame;
	}

}
