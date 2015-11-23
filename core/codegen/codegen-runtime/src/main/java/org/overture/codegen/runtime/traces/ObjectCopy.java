package org.overture.codegen.runtime.traces;

import org.overture.codegen.runtime.copying.DeepCopy;

public class ObjectCopy extends ModuleCopy
{
	private Object instance;
	
	public ObjectCopy(Object orig)
	{
		super(orig.getClass());
		
		// Copy instance
		this.instance = DeepCopy.copy(orig);
		reset();
	}

	@Override
	public void reset()
	{
		super.reset();
		val = DeepCopy.copy(instance);
	}
}
