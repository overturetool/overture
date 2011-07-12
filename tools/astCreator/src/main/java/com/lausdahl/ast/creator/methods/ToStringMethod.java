package com.lausdahl.ast.creator.methods;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

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

	String bodyCache = null;

	@Override
	protected void prepare()
	{
		this.name = "toString";
		this.returnType = "String";

		if (this.bodyCache != null)
		{
			this.body = this.bodyCache;
			return;
		}

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
			Map<String, String> importMap = new Hashtable<String, String>();
			for (ToStringAddOn addon : env.getToStringAddOns())
			{
				for (ToStringPart p : addon.parts)
				{
					if (p.type == ToStringPartType.Import)
					{
						String name = p.content.substring(p.content.lastIndexOf('.') + 1);
						importMap.put(name, p.content);
					}
				}
			}
			sb.append("\t\treturn \"\" + ");

			for (ToStringAddOn addon : c.getToStringAddOns())
			{
				String tmp = "";
				for (int i = 0; i < addon.parts.size(); i++)
				{
					ToStringPart p = addon.parts.get(i);

					switch (p.type)
					{
						case Field:
							boolean found = false;
							for (Field f : c.getInheritedFields())
							{
								if (f.getName().equals("_" + p.content))
								{

									tmp += f.getName();
									found = true;
									break;
								}
							}

							for (Field f : c.getFields())
							{
								if (f.getName().equals("_" + p.content))
								{
									tmp += f.getName();
									found = true;
									break;
								}
							}
							if (!found)
							{
								showError("Faild to find field \"_" + p.content
										+ "\" in class " + c.getName());
							}
							break;
						case RawJava:
							for (Entry<String, String> entry : importMap.entrySet())
							{
								if (p.content.contains(entry.getKey()))
								{
									requiredImports.add(entry.getValue());
								}
							}

							tmp += p.content.substring(1, p.content.length() - 1);
							break;
						case Plus:
							if (((getPartType(addon, i - 1) == ToStringPartType.Field || getPartType(addon, i - 1) == ToStringPartType.String) && getPartType(addon, i + 1) == ToStringPartType.RawJava)
									||

									(getPartType(addon, i - 1) == ToStringPartType.RawJava && (getPartType(addon, i + 1) == ToStringPartType.Field || getPartType(addon, i + 1) == ToStringPartType.String)))
							{
								tmp += p.content;
							}
							break;
						case String:
							tmp += p.content;
							break;

					}
					if ((getPartType(addon, i) == ToStringPartType.String && getPartType(addon, i + 1) == ToStringPartType.Field)
							|| (getPartType(addon, i) == ToStringPartType.Field && getPartType(addon, i + 1) == ToStringPartType.String))
					{
						tmp += "+";
					}
				}
				// tmp = tmp.substring(0, tmp.length() - 1);
				sb.append(tmp);
				sb.append(";");
				break;
			}
		}

		this.body = sb.toString();
		this.bodyCache = this.body;
	}

	private static ToStringPartType getPartType(ToStringAddOn addon, int index)
	{
		if (!addon.parts.isEmpty() && addon.parts.size() > index && index >= 0)
		{
			// ignore +
			if (addon.parts.get(index).content.equals("+"))
			{
				index--;
				return getPartType(addon, index);
			}
			return addon.parts.get(index).type;
		}
		return ToStringPartType.Unknown;
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

	static List<String> reportedErrors = new Vector<String>();

	private void showError(String text)
	{
		if (reportedErrors.contains(text))
		{
			return;
		}
		System.err.println();
		System.err.println(text);
		reportedErrors.add(text);
	}
}
