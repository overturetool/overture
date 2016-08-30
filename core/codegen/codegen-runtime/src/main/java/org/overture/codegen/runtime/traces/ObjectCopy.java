package org.overture.codegen.runtime.traces;

public class ObjectCopy extends ModuleCopy
{
	private Object instance;
	protected Object val;

	public ObjectCopy(Object orig)
	{
		super(orig.getClass());

		// Copy instance
		this.instance = deepCopy(orig);
		reset();
	}

	@Override
	public void reset()
	{
		super.reset();
		val = deepCopy(instance);
	}

	@Override
	public Object getValue()
	{
		return val;
	}
}
