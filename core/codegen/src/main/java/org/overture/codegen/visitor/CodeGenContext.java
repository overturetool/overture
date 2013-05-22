package org.overture.codegen.visitor;

import org.apache.velocity.VelocityContext;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.merging.CG;

public class CodeGenContext
{
	private VelocityContext context;

	public CodeGenContext()
	{
		context = new VelocityContext();
	}
	
	public void put(String name, Class<CG> formatter)
	{
		context.put(name, formatter);
	}
	
	public void put(String name, INode node)
	{
		context.put(name, node);
	}
	
	public VelocityContext getVelocityContext()
	{
		return context;
	}
}
