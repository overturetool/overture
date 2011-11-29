//package com.lausdahl.ast.creator.methods.visitors;
//
//import com.lausdahl.ast.creator.definitions.IClassDefinition;
//import com.lausdahl.ast.creator.env.Environment;
//import com.lausdahl.ast.creator.methods.Method;
//import com.lausdahl.ast.creator.utils.NameUtil;
//
//public class QuestionAnswerCaseMethod extends Method
//{
//	public QuestionAnswerCaseMethod()
//	{
//		super(null,null);
//	}
//
//	public QuestionAnswerCaseMethod(IClassDefinition c,Environment env)
//	{
//		super(c,env);
//	}
//
//	@Override
//	protected void prepare()
//	{
//		IClassDefinition c = classDefinition;
//		StringBuilder sb = new StringBuilder();
//		sb.append("\t/**\n");
//		sb.append("\t* Called by the {@link " + AnalysisUtil.getClass(env, c).getName().getName()
//				+ "} node from {@link " + AnalysisUtil.getClass(env, c).getName().getName() + "#apply(Switch)}.\n");
//		sb.append("\t* @param node the calling {@link " + AnalysisUtil.getClass(env, c).getName().getName()
//				+ "} node\n");
//		sb.append("\t* @param question the provided question\n");
//		sb.append("\t*/");
//		this.javaDoc = sb.toString();
//		this.name = "case" + NameUtil.getClassName(AnalysisUtil.getClass(env, c).getName().getName());
//		this.arguments.add(new Argument(AnalysisUtil.getClass(env, c).getName().getName(), "node"));
//		this.arguments.add(new Argument("Q", "question"));
//		// this.annotation="@override";
//		this.body = "\t\treturn null;";
//		this.returnType = "A";
//	}
//}
