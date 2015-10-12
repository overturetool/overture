package org.overture.codegen.vdm2jml.predgen.info;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.vdm2jml.JmlGenerator;

public class UnionInfo extends AbstractTypeInfo
{
	private List<AbstractTypeInfo> types;
	
	public UnionInfo(boolean optional)
	{
		super(optional);
		this.types = new LinkedList<>();
	}
	
	public List<AbstractTypeInfo> getTypes()
	{
		return types;
	}

	@Override
	public boolean allowsNull()
	{
		if(optional)
		{
			return true;
		}
		
		for(AbstractTypeInfo t : types)
		{
			if(t.allowsNull())
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public List<LeafTypeInfo> getLeafTypesRecursively()
	{
		List<LeafTypeInfo> leaves = new LinkedList<>();

		for(AbstractTypeInfo t : types)
		{
			leaves.addAll(t.getLeafTypesRecursively());
		}
		
		return leaves;
	}

	@Override
	public boolean contains(AbstractTypeInfo subject)
	{
		//TODO: Returning false leads to redundant dynamic type check which is not optimal
		return false;
	}
	
	@Override
	public String consCheckExp(String enclosingModule, String javaRootPackage)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append('(');

		String orSep = "";
		if(allowsNull())
		{
			sb.append(consIsNullCheck());
			orSep = JmlGenerator.JML_OR;
		}
		
		for (AbstractTypeInfo currentType : types)
		{
			sb.append(orSep);
			sb.append(currentType.consCheckExp(enclosingModule, javaRootPackage));
			orSep = JmlGenerator.JML_OR;
		}
		
		sb.append(')');
		
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		
		if(optional)
		{
			sb.append("[");
		}
		
		String sep = "";
		for(AbstractTypeInfo t : types)
		{
			sb.append(sep);
			sb.append(t.toString());
			sep = "|";
		}
		
		if(optional)
		{
			sb.append("]");
		}
		
		sb.append(')');
		
		return sb.toString();
	}
}
