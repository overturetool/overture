package org.overture.tools.astcreator.methods;

import java.util.HashSet;
import java.util.Set;

import org.overture.tools.astcreator.definitions.Field;
import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.utils.NameUtil;

public class GetMethod extends Method
{
	Field f;

	public GetMethod(IClassDefinition c, Field f)
	{
		super(c);
		this.f = f;
	}

	@Override
	protected void prepare(Environment env)
	{

		javaDoc = "\t/**\n";
		//javaDoc += "\t * Returns the {@link "+f.getType()+"} node which is the {@code "+f.getName()+"} child of this {@link "+classDefinition.getName().getName()+"} node.\n";
		javaDoc += "\t * @return the {@link "+NameUtil.stripGenerics(f.getType(true,env))+"} node which is the {@code "+f.getName(env)+"} child of this {@link "+classDefinition.getName().getName()+"} node\n";
		javaDoc += "\t*/";

		this.name = "get"
				+ NameUtil.javaClassName(f.getName(env));
		// this.arguments.add(new Argument(f.getType(), "value"));
		this.returnType = f.getType(true,env);
		StringBuilder sb = new StringBuilder();

		String cast ="";
		if(classDefinition.isRefinedField(f,env))
		{
			cast = f.getCast(env);
		}
		sb.append("\t\treturn "+cast+"this."+ f.getName(env) + ";");

		this.body = sb.toString();
	}
	
	@Override
	public Set<String> getRequiredImports(Environment env)
	{
		Set<String> list = new HashSet<String>();
		list.addAll(super.getRequiredImports(env));
		//list.add(NameUtil.stripGenerics(f.i.getType()));
		
		if(f.isList && !f.isDoubleList)
		{
//			list.add(Environment.listDef.getImportName());
			list.add(Environment.linkedListDef.getName().getCanonicalName());
		}
		if(f.isDoubleList)
		{
//			list.add(Environment.collectionDef.getImportName());
			list.add(Environment.linkedListDef.getName().getCanonicalName());
//			imports.add(Environment.collectionDef.getImportName());
//			imports.add(Environment.linkedListDef.getImportName());
		}
		return list;
	}
	
	@Override
	public Set<String> getRequiredImportsSignature(Environment env)
	{
		Set<String> list = new HashSet<String>();
		list.addAll(super.getRequiredImportsSignature(env));
		//list.add(NameUtil.stripGenerics(f.getType()));
		return list;
	}
}
