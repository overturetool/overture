package org.overture.tools.astcreator.utils;

import java.util.HashSet;
import java.util.Set;

import org.overture.tools.astcreator.definitions.BaseClassDefinition;
import org.overture.tools.astcreator.definitions.ExternalEnumJavaClassDefinition;
import org.overture.tools.astcreator.definitions.ExternalJavaClassDefinition;
import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.definitions.IClassDefinition.ClassType;
import org.overture.tools.astcreator.definitions.IInterfaceDefinition;
import org.overture.tools.astcreator.definitions.InterfaceDefinition;
import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.java.definitions.JavaName;
import org.overture.tools.astcreator.methods.CloneMethod;
import org.overture.tools.astcreator.methods.CloneWithMapMethod;
import org.overture.tools.astcreator.methods.ConstructorMethod;
import org.overture.tools.astcreator.methods.ConstructorTreeFieldsOnlyMethod;
import org.overture.tools.astcreator.methods.DefaultConstructorMethod;
import org.overture.tools.astcreator.methods.EqualsMethod;
import org.overture.tools.astcreator.methods.GetChildrenMethod;
import org.overture.tools.astcreator.methods.KindMethod;
import org.overture.tools.astcreator.methods.KindNodeMethod;
import org.overture.tools.astcreator.methods.KindNodeString;
import org.overture.tools.astcreator.methods.KindString;
import org.overture.tools.astcreator.methods.Method;
import org.overture.tools.astcreator.methods.RemoveChildMethod;
import org.overture.tools.astcreator.methods.ToStringMethod;

public class ClassFactory {
	public static IClassDefinition create(String packageName, String rawName,
			IClassDefinition superClass, ClassType type, Environment env) {
		String preFix = "";
		String postFix = "";
		switch (type) {
		case Alternative:
			preFix = "A";
			postFix += NameUtil.getAssembledNamePostFix(env, superClass);
			// IClassDefinition tmpSuper = superClass;
			// boolean stop = false;
			// while(tmpSuper!=null && env.isTreeNode(tmpSuper)&& !stop )
			// {
			// switch(env.classToType.get(tmpSuper))
			// {
			// case Production:
			// stop = true;
			// case SubProduction:
			// postFix+=tmpSuper.getName().getRawName();
			// break;
			// }
			// tmpSuper = tmpSuper.getSuperDef();
			// }
			break;
		case Production:
			preFix = "P";
			postFix += "Base";
			break;
		case SubProduction:
			preFix = "S";
			postFix = superClass.getName().getRawName() + "Base";
			break;
		case Token:
			preFix = "T";
			break;
		default:
			break;
		}
		JavaName name = new JavaName(packageName, preFix,
				NameUtil.firstLetterUpper(rawName), postFix);
		name.setTag(rawName);
		BaseClassDefinition classDef = new BaseClassDefinition(name,
				env.getAstPackage());
		classDef.setSuper(superClass);
		classDef.methods.addAll(createMethods(type, classDef));
		classDef.setAbstract(type == ClassType.Production
				|| type == ClassType.SubProduction);
		classDef.setFinal(type == ClassType.Token);
		env.addClass(classDef);
		env.classToType.put(classDef, type);
		return classDef;
	}

	private static Set<Method> createMethods(ClassType type,
			IClassDefinition classDef) {
		Set<Method> methods = new HashSet<Method>();

		methods.add(new ConstructorMethod(classDef));
		methods.add(new ConstructorTreeFieldsOnlyMethod(classDef));
		if (type != ClassType.Token) {
			methods.add(new DefaultConstructorMethod(classDef));
			methods.add(new RemoveChildMethod(classDef));
		}

		methods.add(new ToStringMethod(classDef));
		methods.add(new GetChildrenMethod(classDef));

		methods.add(new CloneMethod(classDef, type));
		methods.add(new CloneWithMapMethod(classDef, type));

		methods.add(new EqualsMethod(classDef));

		switch (type) {
		case Alternative:
		case Custom:
			methods.add(new KindMethod(classDef, false));
			methods.add(new KindString(classDef, false));
			break;
		case Production:
			methods.add(new KindNodeMethod(classDef));
			methods.add(new KindNodeString(classDef));
			methods.add(new KindMethod(classDef, true));
			break;
		case SubProduction:
			methods.add(new KindMethod(classDef, false));
			methods.add(new KindString(classDef, false));
			methods.add(new KindMethod(classDef, true));
			break;
		case Token:
			break;
		default:
			break;
		}
		return methods;
	}

	public static IClassDefinition createExternalJavaEnum(String rawName,
			ClassType token, String name, Environment env) {
		IClassDefinition c = new ExternalEnumJavaClassDefinition(rawName, null,
				ClassType.Token, name, env);
		c.getName().setTag(rawName);
		env.addClass(c);
		env.classToType.put(c, ClassType.Token);
		return c;
	}

	public static IClassDefinition createExternalJava(String rawName,
			ClassType token, String name, boolean nodeType, Environment env) {
		IClassDefinition c = new ExternalJavaClassDefinition(rawName, null,
				ClassType.Token, name, nodeType, env);
		c.getName().setTag(rawName);
		env.addClass(c);
		env.classToType.put(c, ClassType.Token);
		return c;
	}

	public static IClassDefinition createCustom(JavaName name, Environment env) {
		IClassDefinition c = new BaseClassDefinition(name, env.getAstPackage());
		c.addMethod(new ConstructorMethod(c));
		c.addMethod(new DefaultConstructorMethod(c));
		env.addClass(c);
		env.classToType.put(c, ClassType.Custom);
		return c;
	}

	public static boolean createInterface(IClassDefinition c, Environment env) {
		if (env.getInterfaceForCommonTreeNode(c) != null) {
			return false;
		}

		InterfaceDefinition intf = new InterfaceDefinition(c.getName().clone(),
				env.getAstPackage());
		intf.methods.addAll(c.getMethods());

		// intf.getName().setPackageName(c.getName().getPackageName()
		// + ".intf");
		String prefix = "I";
		String postFix = "";
		if (env.isTreeNode(c)) {
			switch (env.classToType.get(c)) {
			case Production:
				prefix = "P";
				break;
			case SubProduction:
				prefix = "S";
				// postFix = c.getSuperDef().getName().getRawName();
				postFix += NameUtil.getAssembledNamePostFix(env,
						c.getSuperDef());
				break;
			default:
				break;
			}
		}
		JavaName name = intf.getName();
		name.setPrefix(prefix /* + intf.getName().getPrefix() */);
		name.setPostfix(postFix);
		intf.setTag(c.getName().getTag());
		intf.filterMethodsIfInherited = true;
		IInterfaceDefinition superClass = env.getInterfaceForCommonTreeNode(c
				.getSuperDef());

		if (superClass == null) {
			return false;
		}
		intf.supers.add(superClass);
		c.addInterface(intf);// this line must not be called before the
								// interface name is completed
		env.addCommonTreeInterface(c, intf);

		return true;
	}
}
