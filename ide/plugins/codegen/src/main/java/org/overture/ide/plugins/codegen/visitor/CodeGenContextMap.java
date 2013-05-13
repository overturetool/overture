package org.overture.ide.plugins.codegen.visitor;

import java.util.HashMap;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ide.plugins.codegen.naming.VarNames;
import org.overture.ide.plugins.codegen.nodes.ClassCG;

public class CodeGenContextMap
{
	//Suggestion: Make it remember the last context it was accessing since
	//it is likely to be accessed several times subsequently
	
	private HashMap<String, CodeGenContext> contexts;
	private HashMap<String, ClassCG> classes;
	
	public CodeGenContextMap()
	{
		contexts = new HashMap<String, CodeGenContext>();
		classes = new HashMap<String, ClassCG>();
	}

	public void addClass(ClassCG newClass)
	{
		String className = newClass.getClassName();
		
		contexts.put(className, new CodeGenContext());
		classes.put(className, newClass);
	}
	
	public ClassCG getClass(String key)
	{
		return classes.get(key);
	}
	
	public Set<String> getClassKeys()
	{
		return contexts.keySet();
	}
	
	public Set<String> getContextKeys()
	{
		return contexts.keySet();
	} 
	
	public CodeGenContext getContext(String key)
	{
		return contexts.get(key);
	}
	
	public void commit()
	{
		Set<String> keys = getClassKeys();
		
		for (String key : keys)
		{
			getClass(key).commit(getContext(key));
		}
	}
}
