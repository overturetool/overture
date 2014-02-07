package org.overture.interpreter.runtime.state;

import org.overture.ast.modules.AModuleModules;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.IRuntimeState;
import org.overture.interpreter.util.Delegate;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

public class AModuleModulesRuntime implements IRuntimeState
{
	
	/** A delegate Java class, if one exists. */
	private Delegate delegate = null;
	/** A delegate Java object, if one exists. */
	private Object delegateObject = null;
	
	public final ITypeCheckerAssistantFactory assistantFactory = new TypeCheckerAssistantFactory();

	
	public AModuleModulesRuntime(AModuleModules node)
	{
		delegate = new Delegate(node.getName().getName(), node.getDefs());
	}

	public boolean hasDelegate()
	{
		if (delegate.hasDelegate(assistantFactory))
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
