//package com.lausdahl.ast.creator.methods;
//
//import com.lausdahl.ast.creator.IClassDefinition;
//
//public class CaseTypeCheckMethod extends Method
//{
//	public CaseTypeCheckMethod()
//	{
//		super(null);
//	}
//
//	public CaseTypeCheckMethod(IClassDefinition c)
//	{
//		super(c);
//	}
//
//	@Override
//	protected void prepare()
//	{
//		IClassDefinition c = classDefinition;
//		this.name = "case" + c.getName();
//		this.returnType = "<Typ extends Node> Typ";
//		this.arguments.add(new Argument(c.getName(), "source"));
//		this.arguments.add(new Argument("Environment", "env"));
//		this.arguments.add(new Argument("NameScope", "scope"));
//		this.arguments.add(new Argument("TypeList ", "qualifiers"));
//		this.requiredImports.add("com.lausdahl.runtime.Environment");
//		this.requiredImports.add("com.lausdahl.runtime.NameScope");
//		this.requiredImports.add("com.lausdahl.runtime.TypeList");
//
//		this.body = "\t\treturn null;";// sb.toString();
//	}
//}
