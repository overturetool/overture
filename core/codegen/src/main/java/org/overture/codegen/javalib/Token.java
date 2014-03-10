package org.overture.codegen.javalib;

import java.io.Serializable;

public class Token implements Serializable
{
	private static final long serialVersionUID = -9188699884570692381L;

	private Object value;

	public Token(Object value)
	{
		this.value = value;
	}

	public Object getValue()
	{
		return value;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Token))
			return false;

		Token other = (Token) obj;

		if ((value == null && other.value != null)
				|| (value != null && !value.equals(other.value)))
			return false;

		return true;
	}

	@Override
	public String toString()
	{
		return "mk_token(" + value + ")";
	}
}
