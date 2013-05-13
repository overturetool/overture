package org.overture.ide.plugins.codegen.visitor;

import java.util.ArrayList;

import org.apache.velocity.VelocityContext;
import org.overture.ide.plugins.codegen.naming.VarNames;
import org.overture.ide.plugins.codegen.nodes.ValueDefinitionCG;

public class CodeGenContext
{
	private VelocityContext context;

	public CodeGenContext()
	{
		context = new VelocityContext();
	}

	public void put(VarNames key, String value)
	{
		context.put(key.toString(), value);
	}
	
	public void put(VarNames key, ArrayList<ValueDefinitionCG> valueDefs)
	{
		context.put(key.toString(), valueDefs);
	}
	
	public VelocityContext getVelocityContext()
	{
		return context;
	}
}
