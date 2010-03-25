package org.overture.ide.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class VdmVariable extends VdmDebugElement implements IVariable
{

	private String name;
	private String referenceTypeName;
	private VdmValue value;

	public VdmVariable(VdmDebugTarget target, String name,
			String referenceTypeName, VdmValue value) {
		super(target);
		this.name = name;
		this.referenceTypeName = referenceTypeName;
		this.value = value;
	}
	
	public void setDebugTarget(VdmDebugTarget target)
	{
		super.fTarget = target;
		value.setDebugTarget(target);
	}

	public String getName() throws DebugException
	{
		return name;
	}

	public String getReferenceTypeName() throws DebugException
	{
		return referenceTypeName;
	}

	public IValue getValue() throws DebugException
	{
		return value;
	}

	public boolean hasValueChanged() throws DebugException
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void setValue(String expression) throws DebugException
	{
		// TODO Auto-generated method stub

	}

	public void setValue(IValue value) throws DebugException
	{
		// TODO Auto-generated method stub

	}

	public boolean supportsValueModification()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean verifyValue(String expression) throws DebugException
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean verifyValue(IValue value) throws DebugException
	{
		// TODO Auto-generated method stub
		return false;
	}

}
