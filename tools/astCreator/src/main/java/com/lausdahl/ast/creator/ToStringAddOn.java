package com.lausdahl.ast.creator;

import java.util.List;
import java.util.Vector;

public class ToStringAddOn
{
	public static class ToStringPart
	{
		public enum ToStringPartType
		{
			String, Field, RawJava, Plus, Unknown, Import
		};

		public ToStringPartType type = ToStringPartType.String;
		public String content = "";

		@Override
		public String toString()
		{
			if (type == ToStringPartType.Field)
			{
				return "[" + content + "]";
			} else
			{
				return content;
			}
		}
	}

	public List<ToStringPart> parts = new Vector<ToStringPart>();

	@Override
	public String toString()
	{
		String tmp = new String();
		for (ToStringPart p : parts)
		{
			tmp += p + " ";
		}
		return tmp;
	}
}
