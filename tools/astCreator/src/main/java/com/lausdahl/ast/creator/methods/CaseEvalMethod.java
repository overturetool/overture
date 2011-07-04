//package com.lausdahl.ast.creator.methods;
//
//import com.lausdahl.ast.creator.IClassDefinition;
//
//public class CaseEvalMethod extends Method
//{
//	public CaseEvalMethod()
//	{
//		super(null);
//	}
//
//	public CaseEvalMethod(IClassDefinition c)
//	{
//		super(c);
//
//	}
//
//	@Override
//	protected void prepare()
//	{
//		IClassDefinition c = classDefinition;
//		this.name = "case" + c.getName();
//		this.returnType = "IValue";
//		this.arguments.add(new Argument(c.getName(), "source"));
//		this.arguments.add(new Argument("Context", "ctxt"));
//		this.requiredImports.add("com.lausdahl.runtime.Context");
//		this.requiredImports.add("com.lausdahl.ast.values.IValue");
//
//		this.body = "\t\treturn null;";// sb.toString();
//	}
//}
