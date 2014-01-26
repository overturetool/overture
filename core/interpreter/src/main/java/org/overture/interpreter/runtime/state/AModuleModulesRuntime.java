package org.overture.interpreter.runtime.state;

import org.overture.ast.modules.AModuleModules;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.IRuntimeState;
import org.overture.interpreter.util.Delegate;
import org.overture.interpreter.values.Value;

public class AModuleModulesRuntime implements IRuntimeState
{
	
	/** A delegate Java class, if one exists. */
	private Delegate delegate = null;
	/** A delegate Java object, if one exists. */
	private Object delegateObject = null;

	
	public AModuleModulesRuntime(AModuleModules node)
	{
		delegate = new Delegate(node.getName().getName(), node.getDefs());
	}

	public boolean hasDelegate()
	{
		if (delegate.hasDelegate())
		{
			if (delegateObject == null)
			{
				delegateObject = delegate.newInstance();
			}

			return true;
		}

		return false;
	}

	public Value invokeDelegate(Context ctxt)
	{
		return delegate.invokeDelegate(delegateObject, ctxt);
	}
}
