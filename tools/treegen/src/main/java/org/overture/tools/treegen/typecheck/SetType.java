package org.overture.tools.treegen.typecheck;

public class SetType extends Type {

	// embedded set type
	public Type set_type;

	// constructor
	public SetType (Type tp) { set_type = tp; }
	
	// overloaded member function
	public boolean isSetType() { return true; }
	public boolean isCollection() { return true; }
}
