//package com.lausdahl.ast.creator.definitions;
//
//import java.util.List;
//import java.util.Vector;
//
//import com.lausdahl.ast.creator.env.Environment;
//import com.lausdahl.ast.creator.java.definitions.JavaName;
//import com.lausdahl.ast.creator.methods.ConstructorMethod;
//import com.lausdahl.ast.creator.methods.CustomSetMethod;
//import com.lausdahl.ast.creator.methods.DefaultConstructorMethod;
//import com.lausdahl.ast.creator.methods.GetMethod;
//import com.lausdahl.ast.creator.methods.Method;
//
//public class CustomClassDefinition extends BaseClassDefinition
//{
//	public static List<CustomClassDefinition> classes = new Vector<CustomClassDefinition>();
//
//	public Object tag;
//	Environment env;
//	public boolean isAbstract = false;
//
//	public CustomClassDefinition(JavaName name, Environment env)
//	{
//		super(name);
//
//		this.env = env;
//		methods.add(new ConstructorMethod(this, env));
//		methods.add(new DefaultConstructorMethod(this, env));
//
//		classes.add(this);
//	}
//
//	@Override
//	public void addField(Field field)
//	{
//		super.addField(field);
//		Method setM = new CustomSetMethod(this, field, env);
//		methods.add(setM);
//
//		Method getM = new GetMethod(this, field, env);
//		methods.add(getM);
//
//	}
//
//	@Override
//	public boolean isAbstract()
//	{
//		return this.isAbstract;
//	}
//
//	@Override
//	public void updateEnvironment(Environment env)
//	{
//		this.env = env;
//		super.updateEnvironment(env);
//	}
//
//}
