package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.apache.velocity.VelocityContext;
import org.overture.codegen.cgast.AFieldCG;
import org.overture.codegen.templates.TemplateParameters;

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
	
	public void put(TemplateParameters param, boolean value)
	{
		context.put(param.toString(), value);
	}
	
	public void put(TemplateParameters param, LinkedList<AFieldCG> fields)
	{
		context.put(param.toString(), fields);
	}
	
//	public <T extends TemplateCollectionElement> void put(TemplateParameters param, ArrayList<T> valueDefs)
//	{
//		context.put(param.toString(), valueDefs);
//	}
	
	public VelocityContext getVelocityContext()
	{
		return context;
	}
}
