package org.overture.codegen.visitor;

import org.apache.velocity.VelocityContext;

public class CodeGenContext
{
	private VelocityContext context;

	public CodeGenContext()
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
