package com.lausdahl.ast.creator.methods;

import java.util.HashSet;
import java.util.Set;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;

public class ConvertMethod extends Method
{
	Field from;
	Field to;

	IClassDefinition fromCDef;
	IClassDefinition toCDef;

	public ConvertMethod(IClassDefinition c, Environment env, Field from,
			Field to)
	{
		super(c, env);
		this.from = from;
		this.to = to;
	}

	public ConvertMethod(IClassDefinition c, Environment env,
			IClassDefinition from, IClassDefinition to)
	{
		super(c, env);
		this.fromCDef = from;
		this.toCDef = to;
	}

	@Override
	protected void prepare()
	{
		this.name = "convert";
		if (from != null)
		{
			this.returnType = to.getType();
		} else
		{
			this.returnType = toCDef.getName();
		}
		this.isAbstract = true;

		if (from != null)
		{
			if (from.isList)
			{
				this.arguments.add(new Argument(Environment.listDef.getImportName()
						+ "<? extends " + from.type.getImportName() + ">", "node"));
			} else
			{
				this.arguments.add(new Argument(from.type.getImportName(), "node"));
			}
		} else
		{
			this.arguments.add(new Argument(fromCDef.getImportName(), "node"));
		}
	}

	@Override
	public Set<String> getRequiredImports()
	{
		Set<String> list = new HashSet<String>();
		if (to != null)
		{
			list.add(to.type.getImportName());
			if (to.isList)
			{
				list.add(Environment.listDef.getImportName());
			}
		} else
		{
			list.add(toCDef.getImportName());
		}
		return list;
	}
}
