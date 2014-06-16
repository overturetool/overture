package org.overture.codegen.merging;

import org.apache.velocity.VelocityContext;

public class MergeContext
{
	private VelocityContext context;

	public MergeContext()
	{
		context = new VelocityContext();
	}
	
	public void put(String name, Object value)
	{
		context.put(name, value);
	}
	
	public VelocityContext getVelocityContext()
	{
		return context;
	}
}
