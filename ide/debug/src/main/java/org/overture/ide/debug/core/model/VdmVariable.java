package org.overture.ide.debug.core.model;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.overture.ide.debug.core.Activator;
import org.overture.ide.debug.utils.communication.DBGPProxyException;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.runtime.ModuleInterpreter;
import org.overturetool.vdmj.values.Value;

public class VdmVariable extends VdmDebugElement implements IVariable
{

	private String name;
	private String referenceTypeName;
	private VdmValue value;
	private boolean constant = true;
	// private DebugThreadProxy proxy;
	private VdmStackFrame stackFrame;

	public VdmVariable(VdmDebugTarget target, String name,
			String referenceTypeName, VdmValue value, boolean constant)
	{
		super(target);
		this.name = name;
		this.referenceTypeName = referenceTypeName;
		this.value = value;
		this.constant = constant;
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
		return false;
	}

	public void setValue(String expression) throws DebugException
	{
		
		try
		{
			if(stackFrame.proxy.propertySet(name, value.getKey(), expression))
			{
				if(value instanceof VdmSimpleValue)
				{
					((VdmSimpleValue)value).updateValue(expression);
					stackFrame.fireChangeEvent(DebugEvent.CONTENT);
				}
				//System.out.println("updated");
			}else{
				//System.out.println("NOT updated");
			}
		} catch (DBGPProxyException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setValue(IValue value) throws DebugException
	{
		// TODO Auto-generated method stub

	}

	public boolean supportsValueModification()
	{
		return !constant;
	}

	public boolean verifyValue(String expression) throws DebugException
	{
		try
		{
			Settings.dialect = Dialect.VDM_SL;
			Interpreter i = new ModuleInterpreter(new ModuleList());
			i.init(null);
			Value newValue = i.evaluate(expression, i.initialContext);
			return newValue != null
					&& newValue.kind().equals(referenceTypeName);
		} catch (Exception e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean verifyValue(IValue value) throws DebugException
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void setStackFrame(VdmStackFrame stackFrame)
	{
		this.stackFrame = stackFrame;
		if (value != null)
		{
			value.setStackFrame(stackFrame);
		}
	}

}
