package com.lausdahl.ast.creator.java.definitions;

import com.lausdahl.ast.creator.utils.NameUtil;

public class JavaName implements Cloneable
{
	protected String packageName;
	protected String name;
	private String postfix = "";
	private String prefix = "";
	private String tag = "";

	// public JavaName(String packageName, String name)
	// {
	// this.packageName = packageName;
	// this.name = name;
	// }

	public JavaName(String packageName, String... name)
	{
		this.packageName = packageName;
		if (name.length == 1)
		{
			this.name = name[0];
			return;
		}

		if (name.length > 0)
		{
			this.setPrefix(name[0]);
		}
		if (name.length > 1)
		{
			this.name = name[1];
		}
		if (name.length > 2)
		{
			this.setPostfix(name[2]);
		}
	}

	public String getName()
	{
		return NameUtil.getClassName(getPrefix() + name + getPostfix());
	}

	public String getCanonicalName()
	{
		return this.packageName + (this.packageName != null || this.packageName.isEmpty()? "." : "")
				+ getName();
	}

	public String getPackageName()
	{
		return this.packageName;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public void setPostfix(String postfix)
	{
		this.postfix = postfix;
	}

	public String getPostfix()
	{
		return postfix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public String getRawName()
	{
		return name;
	}

	public JavaName clone()
	{
		return new JavaName(packageName, prefix, name, postfix);
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}

	public String getTag()
	{
		return tag;
	}

	@Override
	public String toString()
	{
		return getName() + (tag.length() > 0 ? " TAG=" + tag : "");
	}

//	@Override
//	public int hashCode()
//	{
//		return getCanonicalName().hashCode();
//	}
}
