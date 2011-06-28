package com.lausdahl.ast.creator;

import java.util.List;
import java.util.Vector;

import com.lausdahl.ast.creator.methods.ConstructorMethod;
import com.lausdahl.ast.creator.methods.CustomSetMethod;
import com.lausdahl.ast.creator.methods.DefaultConstructorMethod;
import com.lausdahl.ast.creator.methods.GetMethod;
import com.lausdahl.ast.creator.methods.Method;

public class CustomClassDefinition extends BaseClassDefinition
{

	public static List<CustomClassDefinition> classes = new Vector<CustomClassDefinition>();

	// public String interfaceName;
	// public List<Field> fields = new Vector<Field>();

	public Object tag;
	Environment env;

	public CustomClassDefinition(String name, Environment env)
	{
		super(name);
		this.env = env;
		methods.add(new ConstructorMethod(this, env));
		methods.add(new DefaultConstructorMethod(this, env));

		classes.add(this);
	}

	@Override
	public void addField(Field field)
	{
		super.addField(field);
		Method setM = new CustomSetMethod(this, field, env);
		methods.add(setM);

		Method getM = new GetMethod(this, field, env);
		methods.add(getM);

	}

	@Override
	public String getName()
	{
		return name;
	}

	// @Override
	// public String toString()
	// {
	// String tmp = IClassDefinition.classHeader+ "\n\npackage generated.node;\n\n\n";
	//
	//
	// List<String> imports = new Vector<String>();
	// for (Method m : methods)
	// {
	// for (String string : m.getRequiredImports())
	// {
	// if(!imports.contains(string))
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
	// if(!imports.contains(string)&& m.isList)
	// {
	// imports.add(string);
	// }
	// }
	// }
	//
	// for (String string : imports)
	// {
	// tmp+="import "+string+";\n";
	// }
	//
	//
	// tmp += "public " + "class " + name;
	//
	// if (interfaceName != null)
	// {
	// tmp += " implements " + interfaceName;
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
	// if (!fields.isEmpty())
	// {
	// tmp += new DefaultConstructorMethod(this).toString();
	// tmp += new ConstructorMethod(this).toString();
	// }
	//
	// for (Field f : fields)
	// {
	// Method setM = new CustomSetMethod(this,f);
	// tmp += setM.toString() + "\n";
	//
	// Method getM = new GetMethod(f);
	// tmp += getM.toString() + "\n";
	// }
	//
	// for (Method m : methods)
	// {
	// tmp += m.toString() + "\n";
	// }
	//
	// tmp += "\n}\n";
	// return tmp;
	// }

	@Override
	public List<Field> getFields()
	{
		return fields;
	}

}
