package org.overture.codegen.vdm2jml.predgen.info;

import java.util.List;

import org.overture.codegen.vdm2jml.util.NameGen;

public abstract class AbstractTypeInfo
{
	protected boolean optional;

	public AbstractTypeInfo(boolean optional)
	{
		this.optional = optional;
	}

	abstract public boolean allowsNull();
	
	abstract public List<LeafTypeInfo> getLeafTypesRecursively();

	abstract public String consCheckExp(String enclosingClass, String javaRootPackage, String arg, NameGen nameGen);
	
	public String consIsNullCheck(String arg)
	{
		return "(" + arg + " == null)";
	}
	
	public static String consSubjectCheck(String className, String methodName, String subject)
	{
		return consSubjectCheckExtraArg(className, methodName, subject, null);
	}
	
	public static String consSubjectCheckExtraArg(String className, String methodName, String subject, String arg)
	{
		String call =  className + "." + methodName + "(" + subject;
		
		if(arg != null)
		{
			call += "," + arg;
		}
		
		call += ")";
		
		return call;
	}
}
