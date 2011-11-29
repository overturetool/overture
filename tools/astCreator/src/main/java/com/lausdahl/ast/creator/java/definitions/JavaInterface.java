package com.lausdahl.ast.creator.java.definitions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.methods.Method;

public class JavaInterface extends JavaTypeDefinition
{
	Set<JavaInterface> supers= new HashSet<JavaInterface>();
	public List<Method> methods = new Vector<Method>();
	List<IInterfaceDefinition> genericArguments = new Vector<IInterfaceDefinition>();
}
