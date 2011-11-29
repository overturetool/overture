package com.lausdahl.ast.creator.java.definitions;

import java.util.HashSet;
import java.util.Set;

public class JavaTypeDefinition
{
	protected JavaName name;
	protected String packageName;
	protected Set<JavaTypeDefinition> imports = new HashSet<JavaTypeDefinition>();
	protected String annotation = "";
	protected String javaDoc ="/**\n"+
	 "* Generated file by AST Creator\n"+
	 "* @author Kenneth Lausdahl\n"+
	 "*\n"+
	 "*/\n";
}
