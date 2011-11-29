package com.lausdahl.ast.creator.methods;

import java.util.HashSet;
import java.util.Set;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.utils.EnumUtil;

public class KindNodeMethod extends Method
{
	public KindNodeMethod(Environment env)
	{
		super(null,env);
	}
	public KindNodeMethod(IClassDefinition c,Environment env)
	{
		super(c,env);
	}

	@Override
	protected void prepare()
	{
		
		StringBuilder sb = new StringBuilder();
		sb.append("\t/**\n");
		sb.append("\t * Returns the {@link NodeEnum"+classDefinition.getName().getPostfix()+"} corresponding to the\n");
		sb.append("\t * type of this {@link Node} node.\n");
		sb.append("\t * @return the {@link NodeEnum"+classDefinition.getName().getPostfix()+"} for this node\n");
		sb.append("\t */");
		this.javaDoc = sb.toString();
		name = "kindNode";
		annotation = "@Override";
		returnType=EnumUtil.getEnumTypeName(env.node,env);
		body = "\t\treturn "+EnumUtil.getEnumTypeName(env.node,env)+"."+EnumUtil.getEnumElementName(classDefinition)+";";

		
		// @Override public NodeEnum kindNode() {
		// return NodeEnum._BINOP;
		// }
	}
	
	@Override
	public Set<String> getRequiredImports()
	{
		Set<String> imports = new HashSet<String>();
		imports.addAll(super.getRequiredImports());
		
		imports.add(env.getDefaultPackage()+"."+EnumUtil.getEnumTypeName(env.node,env));
		
		return imports;
	}
	
	@Override
	protected void prepareVdm()
	{
		skip = true;
	}
}
