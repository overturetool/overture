package com.lausdahl.ast.creator.methods;

import java.util.HashSet;
import java.util.Set;

import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.env.Environment;

public class ConvertMethod extends Method
{
	Field from;
	Field to;

	IClassDefinition fromCDef;
	IClassDefinition toCDef;

	public ConvertMethod(IClassDefinition c, Field from,
			Field to)
	{
		super(c);
		this.from = from;
		this.to = to;
	}

	public ConvertMethod(IClassDefinition c,
			IClassDefinition from, IClassDefinition to)
	{
		super(c);
		this.fromCDef = from;
		this.toCDef = to;
	}

	@Override
	protected void prepare(Environment env)
	{
		this.name = "convert";
		if (from != null)
		{
			this.returnType = to.getType(env);
		} else
		{
			this.returnType = toCDef.getName().getName();
		}
		this.isAbstract = true;

		if (from != null)
		{
			if (from.isList)
			{
				this.arguments.add(new Argument(Environment.listDef.getName().getCanonicalName()
						+ "<? extends " + from.type.getName().getCanonicalName() + ">", "node"));
			} else
			{
				this.arguments.add(new Argument(from.type.getName().getCanonicalName(), "node"));
			}
		} else
		{
			this.arguments.add(new Argument(fromCDef.getName().getCanonicalName(), "node"));
		}
	}

	@Override
	public Set<String> getRequiredImports(Environment env)
	{
		Set<String> list = new HashSet<String>();
		if (to != null)
		{
			list.add(to.type.getName().getCanonicalName());
			if (to.isList)
			{
				list.add(Environment.listDef.getName().getCanonicalName());
			}
		} else
		{
			list.add(toCDef.getName().getCanonicalName());
		}
		return list;
	}
}
