package org.overture.ide.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

public class VdmSimpleValue extends VdmValue
{
	private String value;
	

	public VdmSimpleValue(String referenceTypeName, String value, String key) {
		super(referenceTypeName,key);
		this.value = value;
		
	}

	public boolean hasVariables() throws DebugException
	{
		return false;
	}

	public String getValueString() throws DebugException
	{
		return value;
	}

	public IVariable[] getVariables() throws DebugException
	{
		return new IVariable[0];
	}
	
	
	
	@Override
	public String toString()
	{
		try
		{
			return getReferenceTypeName() + " : "+ getValueString();
		} catch (DebugException e)
		{
			return super.toString();
		}
	}

}
