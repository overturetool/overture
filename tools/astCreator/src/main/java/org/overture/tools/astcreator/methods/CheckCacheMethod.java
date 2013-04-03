package org.overture.tools.astcreator.methods;

import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.env.Environment;

public class CheckCacheMethod extends Method
{


	public CheckCacheMethod(IClassDefinition c)
	{
		super(c);
		
	}



	@Override
	protected void prepare(Environment env)
	{
		StringBuilder sbDoc = new StringBuilder();
		sbDoc.append("\t/**\n");
		sbDoc.append("\t * Based on an internal cache an object will only ever be created once when parsed to this method." +
				" \n\t * This method is not optimized for performance since the {@code newValue} always will be created even when not used.\n"+
				"\t * <i>Remember that the new value is a sub tree</i>\n");
		sbDoc.append("\t * @return a new value for the input coming from either the cache or from the second argument.\n");
		sbDoc.append("\t */");
		
		this.javaDoc = sbDoc.toString();
		
		this.name = "checkCache";

		this.returnType = "Object";
		this.arguments.add(new Argument("Object", "source"));
		this.arguments.add(new Argument("Object", "newValue"));
		
		this.body = "\t\tif(_cache.containsKey(source))\n";
		this.body+="\t\t{\n";
		this.body+="\t\t\treturn _cache.get(source);\n";
		this.body+="\t\t}\n";
		this.body+="\t\t_cache.put(source, newValue);\n";
		this.body+="\t\treturn newValue;";
	}

}
