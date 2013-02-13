package org.overture.tools.treegen.typecheck;

public class JavaType extends Type {
	
	// keep track of the embedded java type
	public String java_type = "";
	
	// default constructor
	public JavaType (){}
	
	// auxiliary constructor
	public JavaType(String pjt) { java_type = pjt; }
	
	// overloaded member functions
	public boolean isJavaType()	{ return true; }

}
