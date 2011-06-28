package com.lausdahl.ast.creator;

import java.util.List;

public interface IInterfaceDefinition
{

	public abstract String getName();

	public abstract List<String> getImports();

	public abstract boolean isFinal();

	public abstract boolean isAbstract();

	public abstract String getPackageName();

	public abstract void setPackageName(String packageName);

	public abstract String getSignatureName();

	public abstract String getJavaSourceCode();

	public abstract String getVdmSourceCode();
	
	
	

}