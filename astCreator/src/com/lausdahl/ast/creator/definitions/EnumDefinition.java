package com.lausdahl.ast.creator.definitions;

import java.util.List;
import java.util.Vector;

public class EnumDefinition extends BaseClassDefinition
{
	public List<String> elements = new Vector<String>();

	public EnumDefinition(String name)
	{
		super(name);
	}
	
	@Override
	public String getName()
	{
		return super.getName();
	}
	
	@Override
	public String getJavaSourceCode()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(IClassDefinition.classHeader + "\n");

		if (getPackageName() != null)
		{
			sb.append("\npackage " + getPackageName() + ";\n\n\n");
		}

		for (String importName : getImports())
		{
			sb.append("import " + importName + ";\n");
		}

		sb.append("public enum " + getName());
		sb.append("\n{\n");
		
		if(!elements.isEmpty())
		{
			StringBuilder intfs = new StringBuilder();
			for (String intfName : elements)
			{
				intfs.append("\t"+intfName + ",\n");
			}
			sb.append(intfs.subSequence(0, intfs.length() - 2));
		}
		
		sb.append("\n}\n");

		return sb.toString();
	}

}
