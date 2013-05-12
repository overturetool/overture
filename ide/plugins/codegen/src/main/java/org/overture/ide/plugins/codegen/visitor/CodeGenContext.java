package org.overture.ide.plugins.codegen.visitor;

import java.util.HashMap;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.overture.ast.definitions.AClassClassDefinition;

public class CodeGenContext
{
	private HashMap<AClassClassDefinition, VelocityContext> contexts;

	public CodeGenContext()
	{
		contexts = new HashMap<AClassClassDefinition, VelocityContext>();
	}

	public void addClass(AClassClassDefinition key)
	{
		contexts.put(key, new VelocityContext());
	}
	
	public VelocityContext getContext(AClassClassDefinition key)
	{
		return contexts.get(key);
	}
	
	public Set<AClassClassDefinition> getKeys()
	{
		return contexts.keySet();
	}
	
	public void putInContext(AClassClassDefinition key, String keyStr, Object value)
	{
		contexts.get(key).put(keyStr, value);
	}

}
