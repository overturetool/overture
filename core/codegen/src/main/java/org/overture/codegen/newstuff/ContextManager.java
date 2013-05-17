package org.overture.codegen.newstuff;

import java.util.HashMap;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.visitor.CodeGenContext;

public class ContextManager
{	
	private HashMap<INode, CodeGenContext> contexts;
	
	public ContextManager()
	{
		contexts = new HashMap<INode, CodeGenContext>();
	}
	
	public void registerContext(INode owner, CodeGenContext context)
	{
		contexts.put(owner, context);
	}
	
	public CodeGenContext findContext(INode owner)
	{
		return contexts.get(owner);
	}
	
}
