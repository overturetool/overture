package org.overture.ide.plugins.codegen.visitor;

import java.util.ArrayList;

import org.apache.velocity.VelocityContext;
import org.overture.ide.plugins.codegen.naming.TemplateParameters;
import org.overture.ide.plugins.codegen.nodes.TemplateCollectionElement;
import org.overture.ide.plugins.codegen.nodes.ValueDefinitionCG;

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
