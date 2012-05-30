package com.lausdahl.ast.creator.utils;

import java.util.HashSet;
import java.util.Set;

import com.lausdahl.ast.creator.definitions.BaseClassDefinition;
import com.lausdahl.ast.creator.definitions.ExternalEnumJavaClassDefinition;
import com.lausdahl.ast.creator.definitions.ExternalJavaClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.definitions.InterfaceDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.java.definitions.JavaName;
import com.lausdahl.ast.creator.methods.CloneMethod;
import com.lausdahl.ast.creator.methods.CloneWithMapMethod;
import com.lausdahl.ast.creator.methods.ConstructorMethod;
import com.lausdahl.ast.creator.methods.ConstructorTreeFieldsOnlyMethod;
import com.lausdahl.ast.creator.methods.DefaultConstructorMethod;
import com.lausdahl.ast.creator.methods.EqualsMethod;
import com.lausdahl.ast.creator.methods.GetChildrenMethod;
import com.lausdahl.ast.creator.methods.KindMethod;
import com.lausdahl.ast.creator.methods.KindNodeMethod;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.methods.RemoveChildMethod;
import com.lausdahl.ast.creator.methods.ToStringMethod;

public class ClassFactory
{
	public static IClassDefinition create(String packageName, String rawName,
			IClassDefinition superClass, ClassType type, Environment env)
	{
		String preFix = "";
		String postFix = "";
		switch (type)
		{
			case Alternative:
				preFix = "A";
				postFix+=NameUtil.getAssembledNamePostFix(env, superClass);
//				IClassDefinition tmpSuper = superClass;
//				boolean stop = false;
//				while(tmpSuper!=null && env.isTreeNode(tmpSuper)&& !stop )
//				{
//					switch(env.classToType.get(tmpSuper))
//					{
//						case Production:
//							stop = true;
//						case SubProduction:
//							postFix+=tmpSuper.getName().getRawName();
//							break;
//					}
//					tmpSuper = tmpSuper.getSuperDef();
//				}
				break;
			case Production:
				preFix = "P";
				postFix+="Base";
				break;
			case SubProduction:
				preFix = "S";
				postFix = superClass.getName().getRawName()+ "Base";
				break;
			case Token:
				preFix = "T";
				break;
		}
		JavaName name = new JavaName(packageName, preFix, NameUtil.firstLetterUpper(rawName), postFix);
		name.setTag(rawName);
		BaseClassDefinition classDef = new BaseClassDefinition(name);
		classDef.setSuper(superClass);
		classDef.methods.addAll(createMethods(type, classDef, env));
		classDef.setAbstract(type ==ClassType.Production || type == ClassType.SubProduction);
		classDef.setFinal(type == ClassType.Token);
		env.addClass(classDef);
		env.classToType.put(classDef, type);
		return classDef;
	}

	private static Set<Method> createMethods(ClassType type,
			IClassDefinition classDef, Environment env)
	{
		Set<Method> methods = new HashSet<Method>();

		methods.add(new ConstructorMethod(classDef, env));
		methods.add(new ConstructorTreeFieldsOnlyMethod(classDef, env));
		if (type != ClassType.Token)
		{
			methods.add(new DefaultConstructorMethod(classDef, env));
			methods.add(new RemoveChildMethod(classDef, env));
		}

		methods.add(new ToStringMethod(classDef, env));
		methods.add(new GetChildrenMethod(classDef, env));

		methods.add(new CloneMethod(classDef, type, env));
		methods.add(new CloneWithMapMethod(classDef, type, env));
		
		methods.add(new EqualsMethod(classDef,env));

		switch (type)
		{
			case Alternative:

			case Custom:
				methods.add(new KindMethod(classDef, false, env));
				break;
			case Production:
				methods.add(new KindNodeMethod(classDef, env));
				methods.add(new KindMethod(classDef, true, env));
				break;
			case SubProduction:
				methods.add(new KindMethod(classDef, false, env));
				methods.add(new KindMethod(classDef, true, env));
				break;
			case Token:
				break;

		}
		return methods;
	}

	public static IClassDefinition createExternalJavaEnum(String rawName, ClassType token, String name, Environment env)
	{
		IClassDefinition c= new ExternalEnumJavaClassDefinition(rawName, null, ClassType.Token, name, env);
		c.getName().setTag(rawName);
		env.addClass(c);
		env.classToType.put(c, ClassType.Token);
		return c;
	}

	public static IClassDefinition createExternalJava(String rawName, ClassType token,
			String name, boolean nodeType, Environment env)
	{
		IClassDefinition c = new ExternalJavaClassDefinition(rawName, null, ClassType.Token, name, nodeType, env);
		c.getName().setTag(rawName);
		env.addClass(c);
		env.classToType.put(c, ClassType.Token);
		return c;
	}
	
	public static IClassDefinition createCustom(JavaName name, Environment env)
	{
		IClassDefinition c = new BaseClassDefinition(name);
		c.addMethod(new ConstructorMethod(c, env));
		c.addMethod(new DefaultConstructorMethod(c, env));
		env.addClass(c);
		env.classToType.put(c, ClassType.Custom);
		return c;
	}

	public static boolean createInterface(IClassDefinition c, Environment env)
	{
		if(env.getInterfaceForCommonTreeNode(c)!=null)
		{
			return false;
		}
		
		InterfaceDefinition intf = new InterfaceDefinition(c.getName().clone());
		intf.methods.addAll(c.getMethods());
		
//		intf.getName().setPackageName(c.getName().getPackageName()
//				+ ".intf");
		String prefix = "I";
		String postFix="";
		if(env.isTreeNode(c))
		{
			switch(env.classToType.get(c))
			{
				case Production:
					prefix="P";
					break;
				case SubProduction:
					prefix ="S";
//					postFix = c.getSuperDef().getName().getRawName();
					postFix+=NameUtil.getAssembledNamePostFix(env, c.getSuperDef());
					break;
				
			}
		}
		JavaName name = intf.getName();
		name.setPrefix(prefix /*+ intf.getName().getPrefix()*/);
		name.setPostfix(postFix);
		name.setTag("");
		intf.filterMethodsIfInherited = true;
		IInterfaceDefinition superClass = env.getInterfaceForCommonTreeNode(c.getSuperDef());
		if(superClass==null)
		{
			return false;
		}
		intf.supers.add(superClass);
		c.addInterface(intf);//this line must not be called before the interface name is completed
		env.addCommonTreeInterface(c, intf);
		
		return true;
	}
}
