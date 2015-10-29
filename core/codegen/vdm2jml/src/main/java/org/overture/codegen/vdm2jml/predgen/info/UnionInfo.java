package org.overture.codegen.vdm2jml.predgen.info;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.codegen.vdm2jml.util.NameGen;

public class UnionInfo extends AbstractTypeInfo
{
	private List<AbstractTypeInfo> types;
	
	public UnionInfo(boolean optional, List<AbstractTypeInfo> types)
	{
		super(optional);
		this.types = types;
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
	public String consCheckExp(String enclosingModule, String javaRootPackage, String arg, NameGen nameGen)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append('(');

		String orSep = "";
		if(isOptional())
		{
			sb.append(consIsNullCheck(arg));
			orSep = JmlGenerator.JML_OR;
		}
		
		for (AbstractTypeInfo currentType : types)
		{
			sb.append(orSep);
			sb.append(currentType.consCheckExp(enclosingModule, javaRootPackage, arg, nameGen));
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
		
		if(isOptional())
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
		
		if(isOptional())
		{
			sb.append("]");
		}
		
		sb.append(')');
		
		return sb.toString();
	}
}
