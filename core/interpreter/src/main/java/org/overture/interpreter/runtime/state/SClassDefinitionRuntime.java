package org.overture.interpreter.runtime.state;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.interpreter.util.Delegate;
import org.overture.interpreter.values.NameValuePairMap;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueListenerList;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.IRuntimeState;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class SClassDefinitionRuntime implements IRuntimeState {

	/** True if the class has a sync section with per or mutex defs. */
	public boolean hasPermissions;
	/** The private or protected static values in the class. */
	public NameValuePairMap privateStaticValues = new NameValuePairMap();
	/** The public visible static values in the class. */
	public NameValuePairMap publicStaticValues = new NameValuePairMap();
	/** True if the class' static members are initialized. */
	public boolean staticInit = false;
	/** True if the class' static values are initialized. */
	public boolean staticValuesInit = false;
	/** A listener list. */
	public ValueListenerList invlistenerlist = null;
	
	/** A delegate Java object for any native methods. */
	private Delegate delegate = null;

	public SClassDefinitionRuntime(SClassDefinition def)
	{
		delegate = new Delegate(def.getName().getName(), PDefinitionAssistantTC.getDefinitions(def));
	}
	
	public boolean hasDelegate()
	{
		return delegate.hasDelegate();
	}

	public Object newInstance()
	{
		return delegate.newInstance();
	}
	
	public Value invokeDelegate(Object delegateObject, Context ctxt)
	{
		return delegate.invokeDelegate(delegateObject, ctxt);
	}
	
	public Value invokeDelegate(Context ctxt)
	{
		return delegate.invokeDelegate(null, ctxt);
	}
	
}
