package com.lausdahl.ast.creator;

import java.util.List;
import java.util.Vector;

import org.antlr.runtime.tree.CommonTree;

import com.lausdahl.ast.creator.methods.CloneMethod;
import com.lausdahl.ast.creator.methods.CloneWithMapMethod;
import com.lausdahl.ast.creator.methods.ConstructorMethod;
import com.lausdahl.ast.creator.methods.DefaultConstructorMethod;
import com.lausdahl.ast.creator.methods.GetMethod;
import com.lausdahl.ast.creator.methods.KindMethod;
import com.lausdahl.ast.creator.methods.KindNodeMethod;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.methods.RemoveChildMethod;
import com.lausdahl.ast.creator.methods.SetMethod;
import com.lausdahl.ast.creator.methods.ToStringMethod;

public class CommonTreeClassDefinition extends BaseClassDefinition implements
		IClassDefinition
{
	// public static final List<CommonTreeClassDefinition> classes = new Vector<CommonTreeClassDefinition>();
	Environment env;

	private ClassType type = ClassType.Alternative;
	public IClassDefinition superClass;
	public CommonTree thisClass;

	public CommonTreeClassDefinition(CommonTree thisClass,
			IClassDefinition superClass, ClassType type, Environment env)
	{
		super(null);

		this.thisClass = thisClass;
		setSuperClass(superClass);

		this.type = type;
		this.env = env;
		super.name = getName();

		if (type != ClassType.Production /* && !fields.isEmpty() */)
		{
			methods.add(new ConstructorMethod(this, env));
			if (type != ClassType.Token)
			{
				methods.add(new DefaultConstructorMethod(this, env));
			}
		}

		if (type != ClassType.Token)
		{
			methods.add(new RemoveChildMethod(this, fields, env));
		}

		methods.add(new ToStringMethod(this, env));

		if (this.type != ClassType.Production)
		{
			methods.add(new CloneMethod(this, type, env));
			methods.add(new CloneWithMapMethod(this, type, env));
		}

		if (this.type == ClassType.Production)
		{
			methods.add(new KindNodeMethod(this, env));
		}

		if (this.type != ClassType.Token)
		{
			methods.add(new KindMethod(this, env));
		}

		env.addClass(this);
	}

	public void setSuperClass(IClassDefinition superClass)
	{
		this.superClass = superClass;
		if (superClass != null)
		{
			setPackageName(this.superClass.getPackageName());
			imports.add(this.superClass.getPackageName()+"."+this.superClass.getName());
		}
	}

	public ClassType getType()
	{
		return this.type;
	}

	public String getName()
	{
		if (thisClass != null)
		{
			String name = firstLetterUpper(thisClass.getText());
			switch (type)
			{
				case Alternative:
					name = "A" + name;
					break;
				case Production:
					name = "P" + name;
					break;
				case Token:
					name = "T" + name;
					break;
			}

			if (getSuperClassDefinition() != null)
			{
				name += getSuperName().substring(1);
			}
			String tmp = javaClassName(name);
			if (VDM && tmp.contains("."))
			{
				return tmp.substring(tmp.lastIndexOf('.') + 1);
			} else
			{
				return tmp;
			}
		}
		return null;
	}

	public String getSuperName()
	{
		switch (type)
		{
			case Alternative:
				if (getSuperClassDefinition() != null)
				{
					return getSuperClassDefinition().getName();
				}
				break;
			case Production:
				return "Node";

			case Token:
				return "Token";

			default:
				return "Node";

		}

		return null;
	}

	@Override
	public boolean isAbstract()
	{
		return type == ClassType.Production;
	}

	@Override
	public boolean isFinal()
	{
		return type == ClassType.Token;
	}

	public IClassDefinition getSuperClassDefinition()
	{
		// for (CommonTreeClassDefinition c : classes)
		// {
		// if (c.thisClass == superClass)
		// {
		// return c;
		// }
		// }
		// return null;
		// IClassDefinition s= env.getSuperClass(this);
		// if(s instanceof CommonTreeClassDefinition)
		// {
		// return (CommonTreeClassDefinition) s;
		// }
		// return null;
		return superClass;
	}

	@Override
	public void addField(Field field)
	{
		super.addField(field);
		Method setM = new SetMethod(this, field, env);
		methods.add(setM);

		Method getM = new GetMethod(this, field, env);
		methods.add(getM);

	}

	// @Override
	// public String toString()
	// {
	// String tmp = IClassDefinition.classHeader
	// + "\n\npackage generated.node;\n\n\n";
	//
	// List<String> imports = new Vector<String>();
	// for (Method m : methods)
	// {
	// for (String string : m.requiredImports)
	// {
	// if (!imports.contains(string))
	// {
	// imports.add(string);
	// }
	// }
	// }
	//
	// for (Field m : fields)
	// {
	// for (String string : m.getRequiredImports())
	// {
	// if (!imports.contains(string) && m.isList)
	// {
	// imports.add(string);
	// }
	// }
	// }
	//
	// if (this.type != ClassType.Production)
	// {
	// for (String string : new CloneMethod(this,type).requiredImports)
	// {
	// if (!imports.contains(string))
	// {
	// imports.add(string);
	// }
	// }
	// }
	//
	// for (String string : imports)
	// {
	// tmp += "import " + string + ";\n";
	// }
	//
	// tmp += "public " + (type == ClassType.Token ? "final " : "")
	// + (type == ClassType.Production ? "abstract " : "") + "class "
	// + getName();
	//
	// switch (type)
	// {
	//
	// case Alternative:
	// tmp += " extends " + getSuperName();
	// break;
	// case Production:
	// tmp += " extends Node";
	// break;
	// case Token:
	// tmp += " extends Token";
	// break;
	// default:
	// tmp += " extends Node";
	// break;
	// }
	//
	// tmp += "\n{";
	//
	// for (Field f : fields)
	// {
	// tmp += "\n\tprivate " + f.getType() + " " + f.getName() + ";";
	// }
	//
	// tmp += "\n\n";
	//
	// for (Method m : methods)
	// {
	// if (m.isConstructor)
	// {
	// tmp += m.toString() + "\n";
	// }
	// }
	//
	// if (type != ClassType.Production && !fields.isEmpty())
	// {
	// tmp += new ConstructorMethod(this).toString();
	// if (type != ClassType.Token)
	// {
	// tmp += new DefaultConstructorMethod(this).toString();
	// }
	// }
	//
	// for (Field f : fields)
	// {
	// Method setM = new SetMethod(this, f);
	// tmp += setM.toString() + "\n";
	//
	// Method getM = new GetMethod(f);
	// tmp += getM.toString() + "\n";
	// }
	// if (type != ClassType.Token)
	// {
	// tmp += new RemoveChildMethod(fields).toString();
	// }
	//
	// tmp += new ToStringMethod(this).toString();
	//
	// for (Method m : methods)
	// {
	// if (!m.isConstructor)
	// {
	// tmp += m.toString() + "\n";
	// }
	// }
	// if (this.type != ClassType.Production)
	// {
	// tmp += new CloneMethod(this,type).toString() + "\n";
	// tmp += new CloneWithMapMethod(this,type).toString() + "\n";
	// }
	// tmp += "\n}\n";
	// return tmp;
	// }

	// public static CommonTreeClassDefinition findClass(String name)
	// {
	// for (CommonTreeClassDefinition c : classes)
	// {
	// if (c.thisClass.getText().equals(name))
	// {
	// return c;
	// }
	// }
	// return null;
	// }

	// @Override
	// public List<Field> getFields()
	// {
	// return this.fields;
	// }

	@Override
	public boolean hasSuper()
	{
		return true;// this.getSuperClassDefinition() != null;
	}

	public String getEnumName()
	{
		return javaClassName(thisClass.getText()).toUpperCase();
	}

	public String getEnumTypeName()
	{
		return "E" + BaseClassDefinition.firstLetterUpper(thisClass.getText());
	}
}
