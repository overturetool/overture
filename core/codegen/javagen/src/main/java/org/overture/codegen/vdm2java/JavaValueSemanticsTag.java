package org.overture.codegen.vdm2java;

public class JavaValueSemanticsTag
{
	private boolean clone;

	public JavaValueSemanticsTag(boolean clone)
	{
		super();
		this.clone = clone;
	}

	public boolean mustClone()
	{
		return clone;
	}
}
