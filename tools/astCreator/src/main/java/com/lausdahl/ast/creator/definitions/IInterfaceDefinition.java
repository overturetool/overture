package com.lausdahl.ast.creator.definitions;

import java.util.List;
import java.util.Set;

public interface IInterfaceDefinition
{

	public abstract String getName();

	public abstract Set<String> getImports();

	public abstract boolean isFinal();

	public abstract boolean isAbstract();

	public abstract String getPackageName();

	public abstract void setPackageName(String packageName);

	public abstract String getSignatureName();

	public abstract String getJavaSourceCode();

	public abstract String getVdmSourceCode();
	
	public abstract void setNamePostfix(String postfix);
	
	public abstract String getNamePostfix();
	
	public abstract void setTag(String tag);
	
	public abstract String getTag();
	
	public abstract void setGenericArguments(IInterfaceDefinition... arguments);
	
	public abstract void setGenericArguments(List<IInterfaceDefinition> arguments);
	
	public abstract List<IInterfaceDefinition> getGenericArguments();
	
	public abstract void setAnnotation(String annotation);
	
	public abstract String getImportName();

}