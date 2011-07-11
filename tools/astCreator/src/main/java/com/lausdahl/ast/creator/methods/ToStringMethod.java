package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.ToStringAddOn;
import com.lausdahl.ast.creator.ToStringAddOn.ToStringPart;
import com.lausdahl.ast.creator.ToStringAddOn.ToStringPart.ToStringPartType;
import com.lausdahl.ast.creator.definitions.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;

public class ToStringMethod extends Method
{
	CommonTreeClassDefinition c;

	public ToStringMethod(CommonTreeClassDefinition c, Environment env)
	{
		super(c, env);
		this.c = c;
	}

	@Override
	protected void prepare()
	{
		this.name = "toString";
		this.returnType = "String";

		StringBuilder sb = new StringBuilder();

		if (c.getToStringAddOns().isEmpty())
		{
			switch (c.getType())
			{
				case Token:
				case Alternative:
					sb.append("\t\treturn");
					String tmp = "";
					for (Field f : c.getFields())
					{
						tmp += " ("
								+ f.getName()
								+ "!=null?"
								+ f.getName()
								+ ".toString():this.getClass().getSimpleName())+";
					}
					if (!c.getFields().isEmpty())
					{
						tmp = tmp.substring(0, tmp.length() - 1);
					}
					if (tmp.trim().length() == 0)
					{
						sb.append(" super.toString()");
					} else
					{
						sb.append(tmp);
					}

					sb.append(";");
					break;

				case Production:
				default:
					sb.append("\t\treturn super.toString();\n");
					break;
			}
		} else
		{
			sb.append("\t\treturn");

			for (ToStringAddOn addon : c.getToStringAddOns())
			{
				String tmp = " ";
				for (int i = 0; i < addon.parts.size(); i++)
				{
					ToStringPart p = addon.parts.get(i);

					switch (p.type)
					{
						case Field:
							for (Field f : c.getInheritedFields())
							{
								if (f.getName().equals("_" + p.content))
								{
									tmp += f.getName();
									break;
								}
							}

							for (Field f : c.getFields())
							{
								if (f.getName().equals("_" + p.content))
								{
									tmp += f.getName();
									break;
								}
							}

							if (i + 1 < addon.parts.size())
							{
								if (addon.parts.get(i + 1).type == ToStringPartType.String)
								{
									tmp += "+";
								}
							}
							break;
						case RawJava:
							if(p.content.contains("Utils"))
							{
								if(!requiredImports.contains("org.overturetool.util.Utils"))//TODO currently hardcoded
								{
									requiredImports.add("org.overturetool.util.Utils");
								}
							}
							tmp += p.content.substring(1, p.content.length() - 1);
							break;
						case Plus:
							tmp += p.content;
							break;
						case String:
							tmp += p.content;

							if (i + 1 < addon.parts.size())
							{
								tmp += "+";
							}
							break;

					}
				}
//				tmp = tmp.substring(0, tmp.length() - 1);
				sb.append(tmp);
				sb.append(";");
				break;
			}
		}

		this.body = sb.toString();
	}

	private boolean isVdmBasicType(String type)
	{
		return (type.contains("int") || type.contains("real")
				|| type.contains("char") || type.contains("String")
				|| type.contains("seq") || type.contains("set"));
	}

	@Override
	protected void prepareVdm()
	{
		this.name = "toString";
		this.returnType = "String";

		StringBuilder sb = new StringBuilder();

		switch (c.getType())
		{
			case Token:
			case Alternative:
				sb.append("\t\treturn");
				String tmp = "";
				for (Field f : c.getFields())
				{
					tmp += " (if "
							+ f.getName()
							+ "<>null then ("
							+ (isVdmBasicType(f.getType()) ? "toStringg("
									+ f.getName() + ")" : f.getName()
									+ ".toString()")
							+ ") else (this.getClass().getSimpleName()))+";
				}
				if (!c.getFields().isEmpty())
				{
					tmp = tmp.substring(0, tmp.length() - 1);
				}
				if (tmp.trim().length() == 0)
				{
					// sb.append(" super.toString()");
					sb.append(" \"" + c.getName() + "\"");
				} else
				{
					sb.append(tmp);
				}

				sb.append(";");
				break;

			case Production:
			default:
				// sb.append("\t\treturn super.toString();\n");
				sb.append("\t\treturn \"" + c.getName() + "\";\n");
				break;
		}

		this.body = sb.toString().replace('+', '^');
	}
}
