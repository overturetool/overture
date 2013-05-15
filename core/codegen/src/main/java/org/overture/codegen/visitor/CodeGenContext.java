package org.overture.codegen.visitor;

import java.util.ArrayList;

import org.apache.velocity.VelocityContext;
import org.overture.codegen.naming.TemplateParameters;
import org.overture.codegen.nodes.TemplateCollectionElement;

public class CodeGenContext
{
	private VelocityContext context;

	public CodeGenContext()
	{
		context = new VelocityContext();
	}

	public void put(TemplateParameters param, String value)
	{
		context.put(param.toString(), value);
	}
	
	public <T extends TemplateCollectionElement> void put(TemplateParameters param, ArrayList<T> valueDefs)
	{
		context.put(param.toString(), valueDefs);
	}
	
	public VelocityContext getVelocityContext()
	{
		return context;
	}
}
