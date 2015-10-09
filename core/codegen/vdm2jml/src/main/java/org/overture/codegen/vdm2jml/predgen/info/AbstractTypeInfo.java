package org.overture.codegen.vdm2jml.predgen.info;

import java.util.List;

public abstract class AbstractTypeInfo
{
	public static String ARG_PLACEHOLDER = "%1$s";
	
	protected boolean optional;

	public AbstractTypeInfo(boolean optional)
	{
		this.optional = optional;
	}

	public void makeOptional()
	{
		this.optional = true;
	}

	abstract public boolean allowsNull();
	
	abstract public List<LeafTypeInfo> getLeafTypesRecursively();

	abstract public boolean contains(AbstractTypeInfo subject);

	abstract public String consCheckExp(String enclosingClass, String javaRootPackage);
}
