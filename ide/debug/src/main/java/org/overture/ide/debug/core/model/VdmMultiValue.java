package org.overture.ide.debug.core.model;

import java.net.SocketTimeoutException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.overture.ide.debug.core.Activator;
import org.overture.ide.debug.core.IDebugConstants;

public class VdmMultiValue extends VdmValue
{
	protected VdmVariable[] variables = null;
	private String type;
	private String key;
	private Integer page;
	protected boolean isResolved = false;

	public VdmMultiValue(String referenceTypeName, String type, String key,
			Integer page, VdmVariable[] variables) {
		super(referenceTypeName);
		this.key = key;
		this.type = type;
		this.page = page;
		this.variables = variables;
	}

	public boolean hasVariables() throws DebugException
	{
		return true;
	}

	public String getValueString() throws DebugException
	{
		return type;
	}

	public IVariable[] getVariables() throws DebugException
	{
		if (key != null && !isResolved)
		{
			VdmVariable[] variables;
			try
			{
				variables = stackFrame.proxy.getVariables(stackFrame.level,
						"NAME",
						key,
						page);

				for (VdmVariable iVariable : variables)
				{
					iVariable.setStackFrame(stackFrame);
					iVariable.setDebugTarget(stackFrame.getDebugTarget());
				}
				this.variables = variables;
				isResolved = true;
			} catch (SocketTimeoutException e)
			{
				if (Activator.DEBUG)
				{
					e.printStackTrace();
				}
				throw new DebugException(new Status(IStatus.WARNING,
						IDebugConstants.PLUGIN_ID,
						"Cannot fetch variables from debug engine",
						e));
			}
		}
		return this.variables;
	}

	@Override
	public void setDebugTarget(VdmDebugTarget target)
	{
		if (variables != null)
		{
			for (VdmVariable var : variables)
			{
				var.setDebugTarget(target);
			}
		}
		super.setDebugTarget(target);
	}

	@Override
	public void setStackFrame(VdmStackFrame stackFrame)
	{
		if (variables != null)
		{
			for (VdmVariable var : variables)
			{
				var.setStackFrame(stackFrame);
			}
		}
		super.setStackFrame(stackFrame);
	}
}
