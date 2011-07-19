package com.lausdahl.ast.creator.methods;

import java.util.HashSet;
import java.util.Set;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;

public class GetMethod extends Method
{
	Field f;

	public GetMethod(IClassDefinition c, Field f,Environment env)
	{
		super(c,env);
		this.f = f;
	}

	@Override
	protected void prepare()
	{

		javaDoc = "\t/**\n";
		javaDoc += "\t * Returns the {@link "+f.getType()+"} node which is the {@code "+f.getName()+"} child of this {@link "+classDefinition.getSignatureName()+"} node.\n";
		javaDoc += "\t * @return the {@link "+f.getType()+"} node which is the {@code "+f.getName()+"} child of this {@link "+classDefinition.getSignatureName()+"} node\n";
		javaDoc += "\t*/";

		this.name = "get"
				+ CommonTreeClassDefinition.javaClassName(f.getName());
		// this.arguments.add(new Argument(f.getType(), "value"));
		this.returnType = f.getType(true);
		StringBuilder sb = new StringBuilder();

		sb.append("\t\treturn this." + f.getName() + ";");

		this.body = sb.toString();
	}
	
	@Override
	public Set<String> getRequiredImports()
	{
		Set<String> list = new HashSet<String>();
		list.addAll(super.getRequiredImports());
		
		if(f.isList && !f.isDoubleList)
		{
//			list.add(Environment.listDef.getImportName());
			list.add(Environment.linkedListDef.getImportName());
		}
		if(f.isDoubleList)
		{
//			list.add(Environment.collectionDef.getImportName());
			list.add(Environment.linkedListDef.getImportName());
//			imports.add(Environment.collectionDef.getImportName());
//			imports.add(Environment.linkedListDef.getImportName());
		}
		return list;
	}
}
